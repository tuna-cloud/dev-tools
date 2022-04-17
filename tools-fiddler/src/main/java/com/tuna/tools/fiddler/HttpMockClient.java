package com.tuna.tools.fiddler;

import com.tuna.commons.utils.JacksonUtils;
import com.tuna.tools.common.VertxInstance;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;

import java.util.Map;

public class HttpMockClient {
    private JsonObject log;
    private JsonObject response = new JsonObject();
    private HttpClient httpClient;

    public HttpMockClient() {
        HttpClientOptions options = new HttpClientOptions();
        options.setTryUseCompression(true);
        options.setTcpFastOpen(true);
        options.setTrustAll(true);
        options.setLogActivity(true);
        httpClient = VertxInstance.getInstance().createHttpClient(options);
    }

    public Future<JsonObject> mock(JsonObject log) {
        this.log = log;
        Promise<JsonObject> promise = Promise.promise();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setHost(log.getString("host"));
        if (log.getInteger("port") == 443) {
            requestOptions.setSsl(true);
        } else {
            requestOptions.setSsl(false);
        }
        requestOptions.setTimeout(30 * 1000);
        requestOptions.setPort(log.getInteger("port"));
        requestOptions.setURI(log.getString("uri"));
        requestOptions.setMethod(HttpMethod.valueOf(log.getString("method")));
        JsonObject obj = log.getJsonObject("headers");
        for (Map.Entry<String, Object> stringObjectEntry : obj) {
            requestOptions.addHeader(stringObjectEntry.getKey(), stringObjectEntry.getValue().toString());
        }

        httpClient.request(requestOptions).onComplete(request -> {
            if (request.succeeded()) {
                if (log.getString("method").equals("POST")) {
                    request.result().send(Buffer.buffer(log.getString("requestBody"))).onComplete(responseResult -> {
                        if (responseResult.succeeded()) {
                            response.put("status", responseResult.result().statusCode());
                            JsonObject header = new JsonObject();
                            for (Map.Entry<String, String> entry : responseResult.result().headers().entries()) {
                                header.put(entry.getKey(), entry.getValue());
                            }
                            response.put("header", header);
                            responseResult.result().body(buffer -> {
                                if (buffer.succeeded()) {
                                    response.put("body", buffer.result().toString());
                                    promise.complete(response);
                                } else {
                                    promise.fail(buffer.cause());
                                }
                            });
                        } else {
                            promise.fail(responseResult.cause());
                        }
                    });
                } else {
                    request.result().send().onComplete(result -> {
                        if (result.succeeded()) {
                            response.put("status", result.result().statusCode());
                            JsonObject header = new JsonObject();
                            for (Map.Entry<String, String> entry : result.result().headers().entries()) {
                                header.put(entry.getKey(), entry.getValue());
                            }
                            response.put("header", header);
                            result.result().body(buffer -> {
                                if (buffer.succeeded()) {
                                    response.put("body", buffer.result().toString());
                                    promise.complete(response);
                                } else {
                                    promise.fail(buffer.cause());
                                }
                            });
                        } else {
                            promise.fail(result.cause());
                        }
                    });
                }
            } else {
                promise.fail(request.cause());
            }
        });
        return promise.future();
    }

    public void close() {
        httpClient.close();
    }
}
