package com.tuna.tools.fiddler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.ReferenceCountUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.impl.VertxHandler;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class ChannelTest {

    @Test
    public void test() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast("codec", new HttpServerCodec());
        channel.pipeline().addLast("inflater", new HttpContentDecompressor());
        channel.pipeline().addLast("handler", new ChannelInboundHandler() {
            @Override
            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof HttpContent) {
                    HttpContent content = (HttpContent) msg;
                    if (!content.decoderResult().isSuccess()) {
                        DecoderResult result = content.decoderResult();
                        ReferenceCountUtil.release(content);
                        result.cause().printStackTrace();
                        return;
                    }
                    Buffer buffer = Buffer.buffer(VertxHandler.safeBuffer(content.content()));
                    System.out.println("request: " + buffer.toString());
                }
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

            }

            @Override
            public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            }

            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

            }
        });
        
        String msg1 = "POST /yonbip-fi-otp/classmapping/update HTTP/1.1\r\n" +
                "Host: yonbip.diwork.com\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 2078\r\n" +
                "sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"\r\n" +
                "Accept: application/json, text/javascript, */*; q=0.01\r\n" +
                "Content-Type: application/json\r\n" +
                "X-Requested-With: XMLHttpRequest\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/99.0.4844.83 Safari/537.36\r\n" +
                "sec-ch-ua-platform: \"macOS\"\r\n" +
                "Origin: https://yonbip.diwork.com\r\n" +
                "Sec-Fetch-Site: same-origin\r\n" +
                "Sec-Fetch-Mode: cors\r\n" +
                "Sec-Fetch-Dest: empty\r\n" +
                "Referer: https://yonbip.diwork.com/yonbip-fi-fcweb/ucf-wh/home_index" +
                ".html?locale=zh_CN&serviceCode=setting_contrastu8c&refimestamp=1648368716956\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: zh-CN,zh;q=0.9\r\n" +
                "Cookie: Hm_lvt_6495ea4f6fbb47d0b926af4228140e91=1640756556; _WorkbenchCross_=Ultraman; " +
                "at=c5c1cac8-aace-4af8-b64b-fde2df1b4d90; yonyou_uid=e2b9bf88-7023-43d3-98ec-92c43ccc1561; " +
                "yonyou_uname=%25E6%259D%258E%25E8%2590%258C%25E8%2590%258C; " +
                "yht_username_diwork=ST-1300498-vNJ12Bu2E5z1QcycehYs-euc.yonyoucloud" +
                ".com__e2b9bf88-7023-43d3-98ec-92c43ccc1561; " +
                "yht_usertoken_diwork" +
                "=%2Bnxi2cMn005Yhxcmwo81RbgmbU5dVjsMgob3O51znT3eoKKIrMfIJo3KIiTSsR1ECRoJoDeqOexhczmQL584ug%3D%3D; " +
                "yht_access_token" +
                "=bttOWFTeGg0Ykh5VnpldGNyM0hxQzBXMkluNks1QTZVWEQ1endyMkw5MHlzZGRvcHM5ZnJaUkhVcmdoM2V2ZzFpZnI5UTJUb2EwdHRLY2dJdFpNQnlxZkxNYURZL2I0clFIT3N4VSsyQlJ0WGFUelRKQTNZQ1gzYWtpZkZiei8yT2lfX2V1Yy55b255b3VjbG91ZC5jb20.__6e901f6fcce300a4b4b995f1f14fadbf_1648348380309; wb_at=LMjnpmmqvujuGCno4to7rynJbxbdgRrjdtbkxnmxntbkntckbnl; ARK_ID=JS7442b010001d63de22af6f2072c679117442; ARK_STARTUP=eyJTVEFSVFVQIjp0cnVlLCJTVEFSVFVQVElNRSI6IjIwMjItMDMtMjcgMTA6MzM6MDAuNTE3In0%3D; PHPSESSID=272ddjrulpg9snc3bam1ijd0am; ck_safe_chaoke_csrf_token=4226ebb212dabc7c958dee7fa7c92cb4; YKJ_IS_DIWORK=1; YKJ_DIWORK_DATA=%7B%22data%22%3A%7B%22is_diwork%22%3A1%2C%22cur_qzid%22%3A%22287259%22%7D%2C%22key%22%3A%22c459adf812925fb422fb5e2891d9694d%22%7D; tenantid=j6uzh3xb; Hm_lvt_e8002ef3d9e0d8274b5b74cc4a027d08=1648348386; Hm_lvt_b97569d26a525941d8d163729d284198=1648348386; businessDate=2022-03-27; locale=zh_CN; userId=e2b9bf88-7023-43d3-98ec-92c43ccc1561; tenantid=j6uzh3xb; yht_token=bttOWFTeGg0Ykh5VnpldGNyM0hxQzBXMkluNks1QTZVWEQ1endyMkw5MHlzZGRvcHM5ZnJaUkhVcmdoM2V2ZzFpZnI5UTJUb2EwdHRLY2dJdFpNQnlxZkxNYURZL2I0clFIT3N4VSsyQlJ0WGFUelRKQTNZQ1gzYWtpZkZiei8yT2lfX2V1Yy55b255b3VjbG91ZC5jb20.__6e901f6fcce300a4b4b995f1f14fadbf_1648348380309; tenantId=j6uzh3xb; userId=e2b9bf88-7023-43d3-98ec-92c43ccc1561; acw_tc=276077d516483679520628126e24cbac913e66c5d2f072559fbc233222640a; Hm_lpvt_b97569d26a525941d8d163729d284198=1648368660; Hm_lpvt_e8002ef3d9e0d8274b5b74cc4a027d08=1648368660; FZ_STROAGE.diwork.com=eyJBUktTVVBFUiI6eyJ0ZW5hbnRfaWQiOiJqNnV6aDN4YiIsImNvbXBhbnkiOiLmsZ%2Foi4%2FlhYjkuLDnurPnsbPmnZDmlpnnp5HmioDmnInpmZDlhazlj7giLCJ1c2VyX2lkIjoiZTJiOWJmODgtNzAyMy00M2QzLTk4ZWMtOTJjNDNjY2MxNTYxIiwidXNlcl9uYW1lIjoi5p2O6JCM6JCMIiwicHJvZHVjdF9pZCI6InU4YzMuMCIsInByb2R1Y3RfbmFtZSI6IllvblN1aXRlIn0sIlNFRVNJT05JRCI6ImM2YzY4ZmYyNDNmMTdkYjUiLCJTRUVTSU9OREFURSI6MTY0ODM2ODcxNzMyNiwiQU5TQVBQSUQiOiI0ZDBmMDBmNmI1ZmFiNDdkIiwiQU5TJERFQlVHIjoyLCJBTlNVUExPQURVUkwiOiJodHRwczovL2FydC5kaXdvcmsuY29tLyIsIkZSSVNUREFZIjoiMjAyMjAzMjciLCJGUklTVElNRSI6ZmFsc2UsIkFSS19MT0dJTklEIjoiZTJiOWJmODgtNzAyMy00M2QzLTk4ZWMtOTJjNDNjY2MxNTYxIiwiQVJLX0lEIjoiSlM3NDQyYjAxMDAwMWQ2M2RlMjJhZjZmMjA3MmM2NzkxMTc0NDIiLCJBUktGUklTVFBST0ZJTEUiOiIyMDIyLTAzLTI3IDEwOjMzOjAwLjUxMSIsIkFOU1NFUlZFUlRJTUUiOjB9\r\n"
                + "\r\n";
        String msg2 = "{\"id\":\"47C07974-EA44-440C-B58A-2FB958F98E21\"," +
                "\"creator\":\"f51e12b5-ac6d-4975-8274-8521e1260466\",\"creationtime\":\"2021-06-21 15:37:15\"," +
                "\"modifier\":\"e2b9bf88-7023-43d3-98ec-92c43ccc1561\",\"modifiedtime\":\"2021-12-07 09:33:16\"," +
                "\"pk_org\":\"2193502232140032\",\"pk_group\":null,\"description\":\"\",\"ts\":\"2021-12-07 " +
                "09:33:16\",\"dr\":0,\"state\":0,\"tenantid\":\"j6uzh3xb\"," +
                "\"srctplid\":\"C71E4C6E-0FC6-4B32-82E4-C6E58EE99330\",\"ytenantid\":\"j6uzh3xb\",\"code\":\"AR\"," +
                "\"name\":\"应收账款\",\"name2\":\"Accounts Receivable\",\"name3\":\"應收賬款\",\"name4\":null," +
                "\"name5\":null,\"name6\":null,\"parentid\":null,\"classifyid\":null,\"enable\":null," +
                "\"def1\":\"setting_contrastu8c\",\"def2\":null,\"def3\":null,\"def4\":null,\"def5\":null," +
                "\"def6\":null,\"def7\":null,\"def8\":null,\"def9\":null,\"def10\":null,\"def11\":null," +
                "\"def12\":null,\"def13\":null,\"def14\":null,\"def15\":null,\"def16\":null,\"def17\":null," +
                "\"def18\":null,\"def19\":null,\"def20\":null,\"def21\":null,\"def22\":null,\"def23\":null," +
                "\"def24\":null,\"def25\":null,\"def26\":null,\"def27\":null,\"def28\":null,\"def29\":null," +
                "\"def30\":null,\"bodies\":[{\"id\":\"38CC3E5E-EAB9-4459-9653-6ECA36B743C7\",\"dr\":0,\"state\":0," +
                "\"tenantid\":\"j6uzh3xb\",\"ytenantid\":\"j6uzh3xb\",\"rownumber\":2,\"targetvalue\":\"112201\"," +
                "\"classvalue1\":\"2193320442517248\"},{\"id\":\"3EC94BB4-C7BE-4F59-9C3A-C0AE84961205\",\"dr\":0," +
                "\"state\":0,\"tenantid\":\"j6uzh3xb\",\"ytenantid\":\"j6uzh3xb\",\"rownumber\":3," +
                "\"targetvalue\":\"112202\",\"classvalue1\":\"2217410821806080\"}],\"permission\":10," +
                "\"issocial\":false,\"accpurposes\":\"01\",\"targetvouchertype\":\"glvoucher\",\"classtype2\":null," +
                "\"classtype3\":null,\"classtype4\":null,\"classtype5\":null,\"classtype6\":null,\"classtype7\":null," +
                "\"classtype8\":null,\"classtype9\":null,\"classtype10\":null,\"classtype11\":null," +
                "\"classtype12\":null,\"classtype13\":null,\"classtype14\":null,\"classtype15\":null," +
                "\"classtype16\":null,\"classtype17\":null,\"classtype18\":null,\"classtype19\":null," +
                "\"classtype20\":null,\"targettype\":\"1f0a5c05-8c4b-11eb-86a6-506b4b2aa3d6\"," +
                "\"accbookid\":\"9C91619F-7026-4AF0-9813-75AD8711803E\",\"globalid\":null,\"version\":null," +
                "\"accsubjectchart\":\"3BE79B3A-72E2-444A-B0A6-10E98AA2849A\",\"defaultaccsubject\":null," +
                "\"outorgid\":null,\"classtype1\":\"1ee59b52-8c4b-11eb-86a6-506b4b2aa3d6\"} \r\n";

        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(msg1.getBytes(StandardCharsets.UTF_8));
        byteBuf.writeBytes(msg2.getBytes(StandardCharsets.UTF_8));
        byteBuf.writeBytes(msg1.getBytes(StandardCharsets.UTF_8));
        channel.writeInbound(byteBuf);

        Thread.sleep(3000L);
        ByteBuf byteBuf1 = UnpooledByteBufAllocator.DEFAULT.buffer();
        byteBuf1.writeBytes(msg2.getBytes(StandardCharsets.UTF_8));
        channel.writeInbound(byteBuf1);

        Thread.sleep(5000L);
    }
}
