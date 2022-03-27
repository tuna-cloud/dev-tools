package com.tuna.tools.fiddler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.tuna.tools.fiddler.util.HttpUtils;
import com.tuna.tools.fiddler.util.TimeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.vertx.core.MultiMap;
import io.vertx.core.http.impl.headers.HeadersMultiMap;

import java.nio.charset.Charset;
import java.util.Map;

public class Log {
    private long ts;
    private String host;
    private int port;
    private String uri;
    private String method;
    private String clientIp;
    private String remoteIp;
    private String protocol;

    private Map<String, String> reqHeaders = Maps.newHashMap();
    private long reqHeaderSize;
    private ByteBuf requestBody = UnpooledByteBufAllocator.DEFAULT.buffer();
    private Map<String, String> rspHeaders = Maps.newHashMap();
    private long rspHeaderSize;
    private ByteBuf responseBody = UnpooledByteBufAllocator.DEFAULT.buffer();

    private int status;

    private long reqStartTime;
    private long reqStopTime;
    private long rspStopTime;

    private Object httpObject;

    public long getTs() {
        return ts;
    }

    public String getTime() {
        return TimeUtils.format(ts);
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(Map<String, String> reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    public long getReqHeaderSize() {
        return reqHeaderSize;
    }

    public void setReqHeaderSize(long reqHeaderSize) {
        this.reqHeaderSize = reqHeaderSize;
    }

    @JsonIgnore
    public ByteBuf getRequestBodyBuf() {
        return requestBody;
    }

    public String getRequestBody() {
        return HttpUtils.printBuf(requestBody);
    }

    public void writeRequestBody(ByteBuf requestBody) {
        this.requestBody.writeBytes(requestBody);
    }

    public Map<String, String> getRspHeaders() {
        return rspHeaders;
    }

    public void setRspHeaders(Map<String, String> rspHeaders) {
        this.rspHeaders = rspHeaders;
    }

    public long getRspHeaderSize() {
        return rspHeaderSize;
    }

    public void setRspHeaderSize(long rspHeaderSize) {
        this.rspHeaderSize = rspHeaderSize;
    }

    @JsonIgnore
    public ByteBuf getResponseBodyBuf() {
        return responseBody;
    }

    public String getResponseBody() {
        return HttpUtils.printBuf(responseBody);
    }

    public void writeResponseBody(ByteBuf responseBody) {
        this.responseBody.writeBytes(responseBody);
    }

    public long getReqStartTime() {
        return reqStartTime;
    }

    public void setReqStartTime(long reqStartTime) {
        this.reqStartTime = reqStartTime;
    }

    public long getReqStopTime() {
        return reqStopTime;
    }

    public void setReqStopTime(long reqStopTime) {
        this.reqStopTime = reqStopTime;
    }

    public long getRspStopTime() {
        return rspStopTime;
    }

    public void setRspStopTime(long rspStopTime) {
        this.rspStopTime = rspStopTime;
    }

    public long getConsummation() {
        if (rspStopTime == 0) {
            return 0;
        }
        return rspStopTime - reqStartTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonIgnore
    public Object getHttpObject() {
        return httpObject;
    }

    public void setHttpObject(Object httpObject) {
        this.httpObject = httpObject;
    }

    public String getBodySize() {
        return requestBody.readableBytes() + "/" + responseBody.readableBytes();
    }
}
