package com.tuna.tools.fiddler.tls;

import com.google.common.collect.Sets;
import com.tuna.tools.fiddler.tls.bouncycastle.BCKCaFactory;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class CaFactoryTest extends TestCase {
    public static void main(String[] args) throws Exception {
        String cur = FileUtils.getUserDirectoryPath() + "/test";
        CaFactory factory = new BCKCaFactory(cur + File.separator + "root.key", cur + File.separator + "root.cert");
//        factory.createRootKeyAndCert(cur + File.separator + "root.cert", cur + File.separator + "root.key");
        KeyPair serverKey = factory.generateKeyPair(CaFactory.KEY_SIZE);
        X509Certificate serverCert = factory.createCASignedCert(serverKey.getPublic(), "baidu.com", Sets.newHashSet("www.baidu.com", "*.baidu.com", "baidu.com", "tuna.com"), Sets.newHashSet("127.0.0.1"));
        PEMToFile.certToJks("baidu.com", "123456", "123456", serverKey.getPrivate(), new Certificate[]{serverCert}, cur + File.separator + "baidu.jks");
    }
}