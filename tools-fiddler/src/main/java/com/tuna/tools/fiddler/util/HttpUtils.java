package com.tuna.tools.fiddler.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    public static ByteBuf endBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
    public static ByteBuf contentLengthBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
    public static ByteBuf lineSplit = UnpooledByteBufAllocator.DEFAULT.buffer(2);

    static {
        endBuf.writeByte('\r');
        endBuf.writeByte('\n');
        endBuf.writeByte('\r');
        endBuf.writeByte('\n');
        contentLengthBuf.writeBytes("\r\nContent-Length:".getBytes(StandardCharsets.UTF_8));
        lineSplit.writeByte('\r');
        lineSplit.writeByte('\n');
    }

    public static int parseContentLength(ByteBuf byteBuf) {
        int pos1 = ByteBufUtil.indexOf(contentLengthBuf, byteBuf) + contentLengthBuf.readableBytes();
        StringBuilder str = new StringBuilder();
        int limit = 20;
        int c = byteBuf.getByte(pos1++) & 0xFF;
        while (c != (int) '\r') {
            str.append((char) c);
            c = byteBuf.getByte(pos1++) & 0xFF;
        }
        return Integer.parseInt(str.toString().trim());
    }

    public static String printBuf(ByteBuf byteBuf) {
        CharSequence sequence =
                byteBuf.getCharSequence(byteBuf.readerIndex(), byteBuf.readableBytes(), Charset.defaultCharset());
        return sequence.toString();
    }
}
