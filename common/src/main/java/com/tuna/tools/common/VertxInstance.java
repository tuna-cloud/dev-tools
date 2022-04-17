package com.tuna.tools.common;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class VertxInstance {
    public static Vertx instance;

    public static Vertx getInstance() {
        if (instance == null) {
            instance = Vertx.vertx();
        }
        return instance;
    }

    public static Future<Void> close() {
        if (instance != null) {
            Future<Void> future = instance.close();
            instance = null;
            return future;
        }
        return Future.succeededFuture();
    }
}
