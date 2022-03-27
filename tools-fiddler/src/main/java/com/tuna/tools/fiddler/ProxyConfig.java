package com.tuna.tools.fiddler;

public class ProxyConfig {
    private int port;
    private boolean https;
    private boolean setupSystemProxy;
    private boolean remoteConnection;
    private boolean http2;
    private String byPassUrls;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isHttps() {
        return https;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public boolean isSetupSystemProxy() {
        return setupSystemProxy;
    }

    public void setSetupSystemProxy(boolean setupSystemProxy) {
        this.setupSystemProxy = setupSystemProxy;
    }

    public boolean isRemoteConnection() {
        return remoteConnection;
    }

    public void setRemoteConnection(boolean remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    public boolean isHttp2() {
        return http2;
    }

    public void setHttp2(boolean http2) {
        this.http2 = http2;
    }

    public String getByPassUrls() {
        return byPassUrls;
    }

    public void setByPassUrls(String byPassUrls) {
        this.byPassUrls = byPassUrls;
    }
}
