package com.tuna.tools.fiddler.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class HttpUtilsTest extends TestCase {

    @Test
    public void test1() {
        for(int i = 0; i < 10; i++) {
            ByteBuf contentLengthBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
            contentLengthBuf.writeBytes("Content-Length:".getBytes(StandardCharsets.UTF_8));

            ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
            byteBuf.writeBytes(("POST /fd/ls/lsp.aspx HTTP/1.1\r\n" +
                    "Host: cn.bing.com\r\n" +
                    "Connection: keep-alive\r\n" +
                    "Content-Length: 0\r\n" +
                    "sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"\r\n" +
                    "Content-Type: text/xml\r\n" +
                    "sec-ch-ua-mobile: ?0\r\n" +
                    "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/99.0.4844.83 Safari/537.36\r\n" +
                    "sec-ch-ua-arch: \"x86\"\r\n" +
                    "sec-ch-ua-full-version: \"99.0.4844.83\"\r\n" +
                    "sec-ch-ua-platform-version: \"12.2.1\"\r\n" +
                    "sec-ch-ua-bitness: \"64\"\r\n" +
                    "sec-ch-ua-model: \r\n" +
                    "sec-ch-ua-platform: \"macOS\"\r\n" +
                    "Accept: */*\r\n" +
                    "Origin: https://cn.bing.com\r\n" +
                    "Sec-Fetch-Site: same-origin\r\n" +
                    "Sec-Fetch-Mode: cors\r\n" +
                    "Sec-Fetch-Dest: empty\r\n" +
                    "Referer: https://cn.bing.com/search?q=javafx+tableview&qs=n&form=QBRE&sp=-1&pq=&sc=0-0&sk=&cvid" +
                    "=D2484B2B0AA94BDBB998E3667F3B4FE3\r\n" +
                    "Accept-Encoding: gzip, deflate, br\r\n" +
                    "Accept-Language: zh-CN,zh;q=0.9\r\n" +
                    "Cookie: MUID=37DB85B61C656A2B3B8695D81D4B6B51; MUIDB=37DB85B61C656A2B3B8695D81D4B6B51; _EDGE_V=1; " +
                    "SRCHD=AF=NOFORM; SRCHUID=V=2&GUID=9AD6A20834F249E28014234A389836E7&dmnchg=1; MUIDV=NU=1; " +
                    "MicrosoftApplicationsTelemetryDeviceId=1798efd9-e9b5-48df-8ef5-f867442bc77d; " +
                    "_TTSS_IN=hist=WyJlbiIsInpoLUhhbnMiLCJhdXRvLWRldGVjdCJd; _TTSS_OUT=hist=WyJ6aC1IYW5zIiwiZW4iXQ==; " +
                    "_tarLang=default=en; imgv=flts=20220317; ZHCHATSTRONGATTRACT=TRUE; " +
                    "ABDEF=V=13&ABDV=11&MRNB=1648190058681&MRB=0; _SS=SID=2AD6EEEC173E6B2228B2FF9E16426A01; SUID=M; " +
                    "_EDGE_S=SID=2AD6EEEC173E6B2228B2FF9E16426A01&mkt=zh-cn; _FP=hta=on; ZHCHATWEAKATTRACT=TRUE; " +
                    "_HPVN=CS" +
                    "=eyJQbiI6eyJDbiI6MTIsIlN0IjoyLCJRcyI6MCwiUHJvZCI6IlAifSwiU2MiOnsiQ24iOjEyLCJTdCI6MCwiUXMiOjAsIlByb2QiOiJIIn0sIlF6Ijp7IkNuIjoxMiwiU3QiOjEsIlFzIjowLCJQcm9kIjoiVCJ9LCJBcCI6dHJ1ZSwiTXV0ZSI6dHJ1ZSwiTGFkIjoiMjAyMi0wMy0yNlQwMDowMDowMFoiLCJJb3RkIjowLCJHd2IiOjAsIkRmdCI6bnVsbCwiTXZzIjowLCJGbHQiOjAsIkltcCI6MTM3fQ==; ENSEARCH=BENVER=0; SRCHUSR=DOB=20220315&T=1648298523000&TPC=1648267626000; ipv6=hit=1648302124833&t=4; SRCHHPGUSR=SRCHLANG=zh-Hans&BRW=XW&BRH=T&CW=1792&CH=1016&SW=1792&SH=1120&DPR=2&UTC=480&DM=1&WTS=63783895323&HV=1648300580&BZA=1; SNRHOP=I=&TS=\r\n").getBytes(
                    StandardCharsets.UTF_8));
            int value = HttpUtils.parseContentLength(byteBuf);
            Assert.assertEquals(0, value);
        }
    }

    @Test
    public void test2() {
        String text = "POST /logstores/csdn-pc-tracking-page-exposure/track HTTP/1.1\r\n" +
                "Host: event.csdn.net\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 9539\r\n" +
                "sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"\r\n" +
                "x-log-apiversion: 0.6.0\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/99.0.4844.83 Safari/537.36\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Accept: */*\r\n" +
                "x-log-bodyrawsize: 1234\r\n" +
                "sec-ch-ua-platform: \"macOS\"\r\n" +
                "Origin: https://blog.csdn.net\r\n" +
                "Sec-Fetch-Site: same-site\r\n" +
                "Sec-Fetch-Mode: cors\r\n" +
                "Sec-Fetch-Dest: empty\r\n" +
                "Referer: https://blog.csdn.net/weixin_45551083/article/details/113512437\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: zh-CN,zh;q=0.9\r\n" +
                "\r\n" +
                "{\"__source__\":\"csdn\",\"__logs__\":[{\"cid\":\"10_20709423080-1625755963292-456130\"," +
                "\"sid\":\"10_1639056813225.275784\",\"pid\":\"blog\",\"uid\":\"shanpobaiyang\"," +
                "\"did\":\"10_20709423080-1625755963292-456130\",\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\"," +
                "\"ref\":\"https://cn.bing.com/\",\"curl\":\"https://blog.csdn" +
                ".net/weixin_45551083/article/details/113512437\",\"dest\":\"\",\"utm\":\"\",\"spm\":\"1001.2101.3001" +
                ".7765\",\"t\":\"1648309298\",\"eleTop\":\"\",\"cCookie\":\"c_session_id=10_1639056813225.275784;" +
                "c_sid=7b871dbcc0c360930df7871dc56ed0e3;c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog" +
                ".csdn.net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog" +
                ".csdn.net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"\"," +
                "\"utm\":\"\",\"spm\":\"1001.2101.3001.4232\",\"mod\":\"1592215036_002\",\"extend1\":\"关注\"," +
                "\"t\":\"1648309298\",\"eleTop\":\"2114\",\"cCookie\":\"c_session_id=10_1639056813225.275784;" +
                "c_sid=7b871dbcc0c360930df7871dc56ed0e3;c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog" +
                ".csdn.net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog" +
                ".csdn.net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"\"," +
                "\"utm\":\"\",\"spm\":\"1001.2101.3001.6334\",\"extend1\":\"专栏目录\",\"t\":\"1648309298\"," +
                "\"eleTop\":\"2114\",\"cCookie\":\"c_session_id=10_1639056813225.275784;" +
                "c_sid=7b871dbcc0c360930df7871dc56ed0e3;c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog" +
                ".csdn.net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog" +
                ".csdn.net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/107790347#comments_19962806\",\"utm\":\"\",\"spm\":\"1001" +
                ".2101.3001.4231\",\"mod\":\"popu_542\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"1467.25\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/110574834#comments_19885183\",\"utm\":\"\",\"spm\":\"1001" +
                ".2101.3001.4231\",\"mod\":\"popu_542\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"1541.25\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/104029160#comments_18407822\",\"utm\":\"\",\"spm\":\"1001" +
                ".2101.3001.4231\",\"mod\":\"popu_542\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"1594.25\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/110289593#comments_18102058\",\"utm\":\"\",\"spm\":\"1001" +
                ".2101.3001.4231\",\"mod\":\"popu_542\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"1647.25\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/107443330#comments_17720469\",\"utm\":\"\",\"spm\":\"1001" +
                ".2101.3001.4231\",\"mod\":\"popu_542\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"1700.25\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/123405986\",\"utm\":\"\",\"spm\":\"\"," +
                "\"mod\":\"popu_382\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"1957.25\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/122089232\",\"utm\":\"\",\"spm\":\"\"," +
                "\"mod\":\"popu_382\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"1992\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}," +
                "{\"cid\":\"10_20709423080-1625755963292-456130\",\"sid\":\"10_1639056813225.275784\"," +
                "\"pid\":\"blog\",\"uid\":\"shanpobaiyang\",\"did\":\"10_20709423080-1625755963292-456130\"," +
                "\"dc_sid\":\"7b871dbcc0c360930df7871dc56ed0e3\",\"ref\":\"https://cn.bing.com/\"," +
                "\"curl\":\"https://blog.csdn.net/weixin_45551083/article/details/113512437\",\"dest\":\"https://blog" +
                ".csdn.net/weixin_45551083/article/details/121113455\",\"utm\":\"\",\"spm\":\"\"," +
                "\"mod\":\"popu_382\",\"ab\":\"new\",\"t\":\"1648309298\",\"eleTop\":\"2026.75\"," +
                "\"cCookie\":\"c_session_id=10_1639056813225.275784;c_sid=7b871dbcc0c360930df7871dc56ed0e3;" +
                "c_segment=2;c_first_ref=cn.bing.com;c_pref=https%3A//blog.csdn" +
                ".net/nimasike/article/details/81122784;c_ref=https%3A//cn.bing.com/;c_first_page=https%3A//blog.csdn" +
                ".net/weixin_45551083/article/details/113512437;c_session_id=10_1639056813225.275784;" +
                "c_page_id=default;c_tos=r9czld;\",\"__time__\":1648309298}],\"__tags__\":{\"useragent\":\"Mozilla/5" +
                ".0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.83 " +
                "Safari/537.36\",\"platform\":\"PC\",\"log_id\":\"296\"}} \r\n";
        for(int i = 0; i < 10; i++) {
            ByteBuf contentLengthBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
            contentLengthBuf.writeBytes("Content-Length:".getBytes(StandardCharsets.UTF_8));

            ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
            byteBuf.writeBytes(text.getBytes(StandardCharsets.UTF_8));
            int value = HttpUtils.parseContentLength(byteBuf);
            Assert.assertEquals(9539, value);
        }
    }
}