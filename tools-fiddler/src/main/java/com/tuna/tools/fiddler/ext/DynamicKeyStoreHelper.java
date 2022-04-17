package com.tuna.tools.fiddler.ext;

import com.google.common.collect.Maps;
import com.tuna.tools.fiddler.tls.CaFactory;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.vertx.core.impl.VertxInternal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DynamicKeyStoreHelper {
    private static final Logger logger = LogManager.getLogger(DynamicKeyStoreHelper.class);

    private final Map<String, X509KeyManager> wildcardMgrMap = new HashMap<>();
    private final Map<String, X509KeyManager> mgrMap = new HashMap<>();

    private final CaFactory caFactory;
    private Map<String, Key> privateKeyMap = Maps.newConcurrentMap();
    private Map<String, Certificate> certMap = Maps.newConcurrentMap();

    public DynamicKeyStoreHelper(String rootKey, String rootCert) {
        logger.info("rootKey: {} rootCert: {}", rootKey, rootCert);
        caFactory = CaFactory.createBCKFactory(rootKey, rootCert);
    }

    public void createCertIfNotExist(String domain, Set<String> subjectAlternativeNameDomains,
                                     Set<String> subjectAlternativeNameIps) throws Exception {
        if (getKeyMgr(domain) != null) {
            return;
        }
        KeyPair serverKey = caFactory.generateKeyPair(CaFactory.KEY_SIZE);
        X509Certificate serverCert = caFactory.createCASignedCert(serverKey.getPublic(), domain,
                subjectAlternativeNameDomains, subjectAlternativeNameIps);
        updateCache(domain, subjectAlternativeNameDomains, serverKey, serverCert);
    }

    protected void updateCache(String domain, Set<String> subjectAlternativeNameDomains, KeyPair keyPair,
                               X509Certificate certificate) {
        privateKeyMap.put(domain, keyPair.getPrivate());
        certMap.put(domain, certificate);

        X509KeyManager mgr = new X509KeyManager() {
            @Override
            public String[] getClientAliases(String s, Principal[] principals) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String[] getServerAliases(String s, Principal[] principals) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
                throw new UnsupportedOperationException();
            }

            @Override
            public X509Certificate[] getCertificateChain(String alias) {
                return new X509Certificate[] {(X509Certificate) certMap.get(domain)};
            }

            @Override
            public PrivateKey getPrivateKey(String alias) {
                return (PrivateKey) privateKeyMap.get(domain);
            }
        };
        if (domain.startsWith("*.")) {
            wildcardMgrMap.put(domain.substring(2), mgr);
        } else {
            mgrMap.put(domain, mgr);
        }
        for (String nameDomain : subjectAlternativeNameDomains) {
            if (nameDomain.startsWith("*.")) {
                wildcardMgrMap.put(nameDomain.substring(2), mgr);
            } else {
                mgrMap.put(nameDomain, mgr);
            }
        }
    }

    public KeyManagerFactory getKeyMgrFactory() throws Exception {
        // key manager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(createEmptyKeyStore(), CaFactory.PASSWORD.toCharArray());
        return keyManagerFactory;
    }

    public X509KeyManager getKeyMgr(String serverName) {
        X509KeyManager mgr = mgrMap.get(serverName);
        if (mgr == null && !wildcardMgrMap.isEmpty()) {
            int index = serverName.indexOf('.') + 1;
            if (index > 0) {
                String s = serverName.substring(index);
                mgr = wildcardMgrMap.get(s);
            }
        }
        if (mgr == null) {
            logger.error("getKeyMgr: {} mgr is null", serverName);
        }
        return mgr;
    }

    public KeyManager[] getKeyMgr() throws Exception {
        return getKeyMgrFactory().getKeyManagers();
    }

    public TrustManager[] getTrustMgr(String serverName) {
        TrustManagerFactory fact = null;
        try {
            fact = getTrustMgrFactory(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fact != null ? fact.getTrustManagers() : null;
    }

    public TrustManagerFactory getTrustMgrFactory(VertxInternal vertx) {
        return InsecureTrustManagerFactory.INSTANCE;
    }

    public TrustManager[] getTrustMgrs(VertxInternal vertx) throws Exception {
        return getTrustMgrFactory(vertx).getTrustManagers();
    }

    /**
     * Creates an empty keystore. The keystore uses the default keystore type set in
     * the file 'lib/security/security.java' (located in the JRE) by the 'keystore.type' property.
     * However, if the default is set to the 'JKS' format, the this function will instead attempt to
     * use the newer 'PKCS12' format, if it exists.
     * <p>
     * The PKCS12 format is the default format for keystores for Java >=9 and available on GraalVM.
     * <p>
     * PKCS12 is an extensible, standard, and widely-supported format for storing cryptographic keys.
     * As of JDK 8, PKCS12 keystores can store private keys, trusted public key certificates, and
     * secret keys.
     * <p>
     * The "old" default "JKS" (available since Java 1.2) can only store private keys and trusted
     * public-key certificates, and they are based on a proprietary format that is not easily
     * extensible to new cryptographic algorithms.
     *
     * @return keystore instance
     * @throws KeyStoreException if the underlying engine cannot create an instance
     */
    private static KeyStore createEmptyKeyStore() throws KeyStoreException {
        final KeyStore keyStore;
        String defaultKeyStoreType = KeyStore.getDefaultType();

        if (defaultKeyStoreType.equalsIgnoreCase("jks") && Security.getAlgorithms("KeyStore").contains("PKCS12")) {
            keyStore = KeyStore.getInstance("PKCS12");
        } else {
            keyStore = KeyStore.getInstance(defaultKeyStoreType);
        }
        try {
            keyStore.load(null, null);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            // these exceptions should never be thrown as there is no initial data
            // provided to the initialization of the keystore
            throw new KeyStoreException("Failed to initialize the keystore", e);
        }
        return keyStore;
    }
}
