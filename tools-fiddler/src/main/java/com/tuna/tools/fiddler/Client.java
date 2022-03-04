package com.tuna.tools.fiddler;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class Client {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClientOptions options = new NetClientOptions();
        options.setTrustAll(true);
        NetClient client = vertx.createNetClient(options);
        client.connect(443, "www.bing.com", result -> {
            if (result.succeeded()) {
                System.out.println("connect success");
                NetSocket socket = result.result();
                socket.handler(buf -> {
                    System.out.println(buf.toString());
                });
                socket.upgradeToSsl(rt -> {
                    if (rt.succeeded()) {
                        socket.write(" GET /?mkt=zh-CN HTTP/1.1\n" +
                                "Host: www.bing.com\n" +
                                "Connection: keep-alive\n" +
                                "Cache-Control: max-age=0\n" +
                                "sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"\n" +
                                "sec-ch-ua-mobile: ?0\n" +
                                "sec-ch-ua-full-version: \"98.0.4758.109\"\n" +
                                "sec-ch-ua-arch: \"x86\"\n" +
                                "sec-ch-ua-platform: \"macOS\"\n" +
                                "sec-ch-ua-platform-version: \"11.6.2\"\n" +
                                "sec-ch-ua-model: \"\"\n" +
                                "sec-ch-ua-bitness: \"64\"\n" +
                                "Upgrade-Insecure-Requests: 1\n" +
                                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36\n" +
                                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
                                "Sec-Fetch-Site: none\n" +
                                "Sec-Fetch-Mode: navigate\n" +
                                "Sec-Fetch-User: ?1\n" +
                                "Sec-Fetch-Dest: document\n" +
                                "Accept-Encoding: gzip, deflate, br\n" +
                                "Accept-Language: zh-CN,zh;q=0.9\n" +
                                "Cookie: MUID=37DB85B61C656A2B3B8695D81D4B6B51; _EDGE_V=1; SRCHD=AF=NOFORM; SRCHUID=V=2&GUID=9AD6A20834F249E28014234A389836E7&dmnchg=1; MUIDV=NU=1; MUIDB=37DB85B61C656A2B3B8695D81D4B6B51; _TTSS_IN=hist=WyJlbiIsInpoLUhhbnMiLCJhdXRvLWRldGVjdCJd; _TTSS_OUT=hist=WyJ6aC1IYW5zIiwiZW4iXQ==; _tarLang=default=en; ABDEF=V=13&ABDV=11&MRNB=1646033196856&MRB=0; _SS=SID=26A3C6BFE7B760992D47D7E5E665618A; _EDGE_S=SID=26A3C6BFE7B760992D47D7E5E665618A&mkt=zh-cn; SNRHOP=I=&TS=; SUID=M; SRCHUSR=DOB=20211119&T=1646376397000&TPC=1646361341000; ipv6=hit=1646379997775&t=4; _HPVN=CS=eyJQbiI6eyJDbiI6NjEsIlN0IjoyLCJRcyI6MCwiUHJvZCI6IlAifSwiU2MiOnsiQ24iOjYxLCJTdCI6MCwiUXMiOjAsIlByb2QiOiJIIn0sIlF6Ijp7IkNuIjo2MSwiU3QiOjEsIlFzIjowLCJQcm9kIjoiVCJ9LCJBcCI6dHJ1ZSwiTXV0ZSI6dHJ1ZSwiTGFkIjoiMjAyMi0wMy0wNFQwMDowMDowMFoiLCJJb3RkIjowLCJHd2IiOjAsIkRmdCI6bnVsbCwiTXZzIjowLCJGbHQiOjAsIkltcCI6NDM1fQ==; SRCHHPGUSR=SRCHLANG=zh-Hans&BRW=HTP&BRH=M&CW=940&CH=959&SW=1792&SH=1120&DPR=2&UTC=480&DM=1&WTS=63781973197&HV=1646377113&BZA=0&VRPSTPTW=1&VCW=1777&VCH=1016\n" +
                                "\n");
                    } else {
                        rt.cause().printStackTrace();
                    }
                });
            }
        });
    }
}
