package com.tuna.tools.fiddler.tls.bouncycastle;

import com.tuna.tools.fiddler.tls.CaFactory;
import com.tuna.tools.fiddler.tls.PEMToFile;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.IPAddress;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class BCKCaFactory implements CaFactory {
    private static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;
    private static final String SIGNATURE_ALGORITHM = "SHA256WithRSAEncryption";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private Logger logger = LogManager.getLogger();
    private PrivateKey caPrivateKey;
    private X509Certificate caX509Certificate;

    public BCKCaFactory() {
    }

    public BCKCaFactory(String caPrivateKeyPath, String caX509CertificatePath) {
        caPrivateKey = PEMToFile.privateKeyFromPEMFile(caPrivateKeyPath);
        caX509Certificate = PEMToFile.x509FromPEMFile(caX509CertificatePath);
    }

    @Override
    public KeyPair generateKeyPair(int keySize) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_GENERATION_ALGORITHM, PROVIDER_NAME);
        generator.initialize(keySize, new SecureRandom());
        return generator.generateKeyPair();
    }

    @Override
    public X509Certificate createAutoSignCaCert(String dn, KeyPair keyPair) throws Exception {
        // signers name
        X500Name issuerName = new X500Name(dn);

        // serial
        BigInteger serial = BigInteger.valueOf(new Random().nextInt(Integer.MAX_VALUE));

        // create the certificate - version 3 (with subjects name same as issues as self signed)
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerName, serial, NOT_BEFORE, NOT_AFTER, issuerName, keyPair.getPublic());
        builder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(keyPair.getPublic()));
        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.cRLSign);
        builder.addExtension(Extension.keyUsage, false, usage);

        ASN1EncodableVector purposes = new ASN1EncodableVector();
        purposes.add(KeyPurposeId.id_kp_serverAuth);
        purposes.add(KeyPurposeId.id_kp_clientAuth);
        purposes.add(KeyPurposeId.anyExtendedKeyUsage);
        builder.addExtension(Extension.extendedKeyUsage, false, new DERSequence(purposes));

        X509Certificate cert = signCertificate(builder, keyPair.getPrivate());
        cert.checkValidity(new Date());
        cert.verify(keyPair.getPublic());

        return cert;
    }

    @Override
    public void createRootKeyAndCert(String certFile, String privateKeyFile) throws Exception {
        KeyPair keyPair = generateKeyPair(KEY_SIZE);
        X509Certificate cert = createAutoSignCaCert(ROOT_DN, keyPair);
        saveAsPEMFile(cert, certFile, "X509 Certificate");
        saveAsPEMFile(keyPair.getPrivate(), privateKeyFile, "Private Key");
        this.caPrivateKey = keyPair.getPrivate();
        this.caX509Certificate = cert;
    }

    /**
     * Create a server certificate for the given domain and subject alternative names, signed by the given Certificate Authority.
     */
    @Override
    public X509Certificate createCASignedCert(PublicKey publicKey, String domain, Set<String> subjectAlternativeNameDomains, Set<String> subjectAlternativeNameIps, X509Certificate certificateAuthorityCert, PrivateKey certificateAuthorityPrivateKey) throws Exception {

        // signers name
        X500Name issuer = new X509CertificateHolder(certificateAuthorityCert.getEncoded()).getSubject();

        // subjects name - the same as we are self signed.
        X500Name subject = new X500Name("CN=" + domain + ", O=" + ORGANISATION + ", L=" + LOCALITY + ", ST=" + STATE + ", C=" + COUNTRY);

        // serial
        BigInteger serial = BigInteger.valueOf(new Random().nextInt(Integer.MAX_VALUE));

        // create the certificate - version 3
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer, serial, NOT_BEFORE, NOT_AFTER, subject, publicKey);
        builder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(publicKey));
        builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));

        // subject alternative name
        List<ASN1Encodable> subjectAlternativeNames = new ArrayList<>();
        if (subjectAlternativeNameDomains != null) {
            subjectAlternativeNames.add(new GeneralName(GeneralName.dNSName, domain));
            for (String subjectAlternativeNameDomain : subjectAlternativeNameDomains) {
                subjectAlternativeNames.add(new GeneralName(GeneralName.dNSName, subjectAlternativeNameDomain));
            }
        }
        if (subjectAlternativeNameIps != null) {
            for (String subjectAlternativeNameIp : subjectAlternativeNameIps) {
                if (IPAddress.isValidIPv6WithNetmask(subjectAlternativeNameIp)
                        || IPAddress.isValidIPv6(subjectAlternativeNameIp)
                        || IPAddress.isValidIPv4WithNetmask(subjectAlternativeNameIp)
                        || IPAddress.isValidIPv4(subjectAlternativeNameIp)) {
                    subjectAlternativeNames.add(new GeneralName(GeneralName.iPAddress, subjectAlternativeNameIp));
                }
            }
        }
        if (subjectAlternativeNames.size() > 0) {
            DERSequence subjectAlternativeNamesExtension = new DERSequence(subjectAlternativeNames.toArray(new ASN1Encodable[0]));
            builder.addExtension(Extension.subjectAlternativeName, false, subjectAlternativeNamesExtension);
        }
        X509Certificate signedX509Certificate = signCertificate(builder, certificateAuthorityPrivateKey);

        // validate
        signedX509Certificate.checkValidity(new Date());
        signedX509Certificate.verify(certificateAuthorityCert.getPublicKey());

        return signedX509Certificate;
    }

    @Override
    public X509Certificate createCASignedCert(PublicKey publicKey, String domain, Set<String> subjectAlternativeNameDomains, Set<String> subjectAlternativeNameIps) throws Exception {
        return createCASignedCert(publicKey, domain, subjectAlternativeNameDomains, subjectAlternativeNameIps, caX509Certificate, caPrivateKey);
    }

    private SubjectKeyIdentifier createSubjectKeyIdentifier(Key key) throws IOException {
        try (ASN1InputStream is = new ASN1InputStream(new ByteArrayInputStream(key.getEncoded()))) {
            ASN1Sequence seq = (ASN1Sequence) is.readObject();
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(seq);
            return new BcX509ExtensionUtils().createSubjectKeyIdentifier(info);
        }
    }

    private X509Certificate signCertificate(X509v3CertificateBuilder certificateBuilder, PrivateKey privateKey) throws OperatorCreationException, CertificateException {
        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(PROVIDER_NAME).build(privateKey);
        return new JcaX509CertificateConverter().setProvider(PROVIDER_NAME).getCertificate(certificateBuilder.build(signer));
    }

    private void saveAsPEMFile(Object object, String absolutePath, String type) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("created dynamic {} PEM file at{}", type, absolutePath);
        }
        try (FileWriter pemfileWriter = new FileWriter(createFileIfNotExists(type, new File(absolutePath)))) {
            try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(pemfileWriter)) {
                jcaPEMWriter.writeObject(object);
            }
        }
    }

    private File createFileIfNotExists(String type, File file) {
        if (!file.exists()) {
            try {
                FileUtils.forceMkdirParent(file);
                if (!file.createNewFile()) {
                    logger.info("failed to create the file {} while attempting to save Certificate Authority {} PEM file", type, file.getAbsolutePath());
                }
            } catch (Throwable throwable) {
                logger.error("failed to create the file {} while attempting to save Certificate Authority {} PEM file, err: {}", type, file.getAbsolutePath(), throwable);
            }
        }
        return file;
    }

    public PrivateKey getCaPrivateKey() {
        return caPrivateKey;
    }

    public X509Certificate getCaX509Certificate() {
        return caX509Certificate;
    }
}
