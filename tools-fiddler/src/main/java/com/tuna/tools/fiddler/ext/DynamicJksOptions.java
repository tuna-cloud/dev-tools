package com.tuna.tools.fiddler.ext;
/*
 * Copyright (c) 2011-2021 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import java.io.File;
import java.util.function.Function;

/**
 * Key or trust store options configuring private key and/or certificates based on Java Keystore files.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class DynamicJksOptions extends JksOptions {
    private String rootKey;
    private String rootCert;
    private DynamicKeyStoreHelper helper;

    /**
     * Default constructor
     */
    public DynamicJksOptions() {
        super();
    }

    /**
     * Copy constructor
     *
     * @param other the options to copy
     */
    public DynamicJksOptions(DynamicJksOptions other) {
        super(other);
        this.rootKey = other.getRootKey();
        this.rootCert = other.getRootCert();
    }

    /**
     * Create options from JSON
     *
     * @param json the JSON
     */
    public DynamicJksOptions(JsonObject json) {
        this();
        DynamicJksOptionsConverter.fromJson(json, this);
    }

    @Override
    public DynamicJksOptions copy() {
        return new DynamicJksOptions(this);
    }

    /**
     * Convert to JSON
     *
     * @return the JSON
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        DynamicJksOptionsConverter.toJson(this, json);
        return json;
    }

    @Override
    public KeyManagerFactory getKeyManagerFactory(Vertx vertx) throws Exception {
        DynamicKeyStoreHelper helper = getHelper(vertx);
        return helper != null ? helper.getKeyMgrFactory() : null;
    }

    public DynamicKeyStoreHelper getHelper(Vertx vertx) throws Exception {
        if (helper == null) {
            helper = new DynamicKeyStoreHelper(getPath() + File.separator + getRootKey(),
                    getPath() + File.separator + getRootCert());
        }
        return helper;
    }

    @Override
    public Function<String, X509KeyManager> keyManagerMapper(Vertx vertx) throws Exception {
        DynamicKeyStoreHelper helper = getHelper(vertx);
        return helper != null ? helper::getKeyMgr : null;
    }

    @Override
    public TrustManagerFactory getTrustManagerFactory(Vertx vertx) throws Exception {
        DynamicKeyStoreHelper helper = getHelper(vertx);
        return helper != null ? helper.getTrustMgrFactory((VertxInternal) vertx) : null;
    }

    @Override
    public Function<String, TrustManager[]> trustManagerMapper(Vertx vertx) throws Exception {
        DynamicKeyStoreHelper helper = getHelper(vertx);
        return helper != null ? helper::getTrustMgr : null;
    }

    public String getRootKey() {
        return rootKey;
    }

    public void setRootKey(String rootKey) {
        this.rootKey = rootKey;
    }

    public String getRootCert() {
        return rootCert;
    }

    public void setRootCert(String rootCert) {
        this.rootCert = rootCert;
    }
}
