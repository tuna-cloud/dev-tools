package com.tuna.tools.fiddler.tls;

import com.tuna.tools.fiddler.tls.bouncycastle.BCKCaFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

public interface CaFactory {
    String PASSWORD = "123456";
    String KEY_GENERATION_ALGORITHM = "RSA";

    /**
     * Generates an 2048 bit RSA key pair using SHA1PRNG for the Certificate Authority.
     */
    int KEY_SIZE = 2048;
    // distinguishing name
    String ROOT_COMMON_NAME = "tuna.com";
    String ORGANISATION = "Tuna";
    String LOCALITY = "Beijing";
    String STATE = "Beijing";
    String COUNTRY = "CN";
    String ROOT_DN = "CN=" + ROOT_COMMON_NAME + ", O=" + ORGANISATION + ", L=" + LOCALITY + ", ST=" + STATE + ", C=" + COUNTRY;
    /**
     * Current time minus 1 year, just in case software clock goes back due to
     * time synchronization
     */
    Date NOT_BEFORE = new Date(System.currentTimeMillis() - 86400000L * 5);
    /**
     * The maximum possible value in X.509 specification: 9999-12-31 23:59:59,
     * new Date(253402300799000L), but Apple iOS 8 fails with a certificate
     * expiration date grater than Mon, 24 Jan 6084 02:07:59 GMT (issue #6).
     * <p>
     * Hundred years in the future from starting the proxy should be enough.
     */
    Date NOT_AFTER = new Date(System.currentTimeMillis() + 86400000L * 360);

    static CaFactory createBCKFactory(String caPrivateKeyPath, String caX509CertificatePath) {
        return new BCKCaFactory(caPrivateKeyPath, caX509CertificatePath);
    }

    KeyPair generateKeyPair(int keySize) throws Exception;

    X509Certificate createAutoSignCaCert(String dn, KeyPair keyPair) throws Exception;

    X509Certificate createCASignedCert(PublicKey publicKey, String domain, Set<String> subjectAlternativeNameDomains, Set<String> subjectAlternativeNameIps, X509Certificate certificateAuthorityCert, PrivateKey certificateAuthorityPrivateKey) throws Exception;

    X509Certificate createCASignedCert(PublicKey publicKey, String domain, Set<String> subjectAlternativeNameDomains, Set<String> subjectAlternativeNameIps) throws Exception;

    void createRootKeyAndCert(String certFile, String privateKeyFile) throws Exception;
}
