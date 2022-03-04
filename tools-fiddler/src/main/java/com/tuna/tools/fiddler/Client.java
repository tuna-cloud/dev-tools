package com.tuna.tools.fiddler;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class Client {
    public static void main1(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClientOptions options = new NetClientOptions();
        options.setTrustAll(true);
        NetClient client = vertx.createNetClient(options);
        client.connect(8090, "127.0.0.1", result -> {
            if (result.succeeded()) {
                System.out.println("connect success");
                NetSocket socket = result.result();
                socket.handler(buf -> {
                    System.out.println(buf.toString());
                });
                socket.upgradeToSsl(rt -> {
                    if (rt.succeeded()) {
                        System.out.println("upgrade success");
                        socket.write("GET https://www.baidu.com/ HTTP/1.1\n" +
                                "Host: www.baidu.com\n" +
                                "Connection: keep-alive\n" +
                                "Cache-Control: max-age=0\n" +
                                "sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"\n" +
                                "sec-ch-ua-mobile: ?0\n" +
                                "sec-ch-ua-platform: \"macOS\"\n" +
                                "Upgrade-Insecure-Requests: 1\n" +
                                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36\n" +
                                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
                                "Sec-Fetch-Site: none\n" +
                                "Sec-Fetch-Mode: navigate\n" +
                                "Sec-Fetch-User: ?1\n" +
                                "Sec-Fetch-Dest: document\n" +
                                "Accept-Encoding: gzip, deflate, br\n" +
                                "Accept-Language: zh-CN,zh;q=0.9\n" +
                                "Cookie: BIDUPSID=BCA4E8DD580A6859881E8D4CDA877FBA; PSTM=1625898686; __yjs_duid=1_83d57eb4f6efa627bf47d13cf0d546b71625898824423; BAIDUID=C37569114B7BBE9BA885A20AFB64693D:FG=1; BAIDUID_BFESS=C37569114B7BBE9BA885A20AFB64693D:FG=1; Hm_lvt_aec699bb6442ba076c8981c6dc490771=1629423306; COOKIE_SESSION=10719080_0_9_9_5_27_0_0_9_8_1_8_10719235_0_31_0_1640142177_0_1640142146%7C9%230_0_1640142146%7C1; BD_HOME=1; H_PS_PSSID=35835_34429_35106_35865_34584_35845_35246_35949_35804_35984_35322_26350_35723_35940; BD_UPN=123253; BA_HECTOR=048gak87ag010420u41h20peb0q\n\n");
                    } else {
                        rt.cause().printStackTrace();
                    }
                });
            }
        });
    }
}
