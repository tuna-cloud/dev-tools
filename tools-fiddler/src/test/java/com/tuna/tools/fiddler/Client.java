package com.tuna.tools.fiddler;

import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class Client {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions();
        options.setTrustAll(true);
        options.setSsl(true);
        HttpClient client = vertx.createHttpClient(options);
        client.request(HttpMethod.GET, 443, "www.bing.com", "/s?ie=utf-8&f=8&rsv_bp=1", r -> {
            HttpClientRequest request = r.result();
            request.send(s -> {
                if (s.succeeded()) {
                    HttpClientResponse response = s.result();
                    response.bodyHandler(buf -> System.out.println(buf.toString()));
                }
            });

        });
    }
}
