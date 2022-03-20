package com.tuna.tools.fiddler;

import com.tuna.tools.fiddler.ext.DynamicJksOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import org.apache.commons.io.FileUtils;

public class HttpServerTest {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
        options.setSsl(true);
        options.setSni(true);
        options.setPort(443);
        options.setKeyStoreOptions(new DynamicJksOptions());
        options.addEnabledSecureTransportProtocol("TLSv1");
        options.addEnabledSecureTransportProtocol("TLSv1.1");
        options.addEnabledSecureTransportProtocol("TLSv1.2");
        HttpServer server = vertx.createHttpServer(options);
        server.requestHandler(req -> req.response().end("hellow"));
        server.listen(ar -> {
            if (ar.succeeded()) {
                System.out.println("bind " + ar.result().actualPort() + " success");
            } else {
                ar.cause().printStackTrace();
            }
        });
    }
}
