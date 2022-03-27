package com.tuna.tools.fiddler;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import org.apache.commons.lang3.RandomUtils;

public class Client {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions();
        options.setTrustAll(true);
        options.setSsl(true);
        options.setProxyOptions(new ProxyOptions().setHost("127.0.0.1").setPort(8090).setType(ProxyType.HTTP));
        HttpClient client = vertx.createHttpClient(options);
        for (int i = 0; i < 1; i++) {
            client.request(HttpMethod.POST, 443, "yonbip.diwork.com", "/yonbip-fi-otp/classmapping/queryRoot", r -> {
                HttpClientRequest request = r.result();
                request.putHeader("Host", "yonbip.diwork.com");
                request.putHeader("Connection", "keep-alive");
                request.putHeader("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";" +
                        "v=\"99\"");
                request.putHeader("Accept", "application/json, text/javascript, */*; q=0.01");
                request.putHeader("Content-Type", "application/json");
                request.putHeader("X-Requested-With", "XMLHttpRequest");
                request.putHeader("sec-ch-ua-mobile", "?0");
                request.putHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.83 Safari/537.36");
                request.putHeader("sec-ch-ua-platform", "\"macOS\"");
                request.putHeader("Origin", "https://yonbip.diwork.com");
                request.putHeader("Sec-Fetch-Site", "same-origin");
                request.putHeader("Sec-Fetch-Mode", "cors");
                request.putHeader("Sec-Fetch-Dest", "empty");
                request.putHeader("Referer", "https://yonbip.diwork.com/yonbip-fi-fcweb/ucf-wh/home_index.html?locale=zh_CN&serviceCode=setting_contrastu8c&refimestamp=1648372742854");
                request.putHeader("Accept-Encoding", "gzip, deflate, br");
                request.putHeader("Accept-Language", "zh-CN,zh;q=0.9");
                request.putHeader("Cookie", "Hm_lvt_6495ea4f6fbb47d0b926af4228140e91=1640756556; " +
                        "_WorkbenchCross_=Ultraman; at=c5c1cac8-aace-4af8-b64b-fde2df1b4d90; " +
                        "yonyou_uid=e2b9bf88-7023-43d3-98ec-92c43ccc1561; " +
                        "yonyou_uname=%25E6%259D%258E%25E8%2590%258C%25E8%2590%258C; " +
                        "yht_username_diwork=ST-1300498-vNJ12Bu2E5z1QcycehYs-euc.yonyoucloud" +
                        ".com__e2b9bf88-7023-43d3-98ec-92c43ccc1561; " +
                        "yht_usertoken_diwork" +
                        "=%2Bnxi2cMn005Yhxcmwo81RbgmbU5dVjsMgob3O51znT3eoKKIrMfIJo3KIiTSsR1ECRoJoDeqOexhczmQL584ug%3D" +
                        "%3D; yht_access_token" +
                        "=bttOWFTeGg0Ykh5VnpldGNyM0hxQzBXMkluNks1QTZVWEQ1endyMkw5MHlzZGRvcHM5ZnJaUkhVcmdoM2V2ZzFpZnI5UTJUb2EwdHRLY2dJdFpNQnlxZkxNYURZL2I0clFIT3N4VSsyQlJ0WGFUelRKQTNZQ1gzYWtpZkZiei8yT2lfX2V1Yy55b255b3VjbG91ZC5jb20.__6e901f6fcce300a4b4b995f1f14fadbf_1648348380309; wb_at=LMjnpmmqvujuGCno4to7rynJbxbdgRrjdtbkxnmxntbkntckbnl; ARK_ID=JS7442b010001d63de22af6f2072c679117442; ARK_STARTUP=eyJTVEFSVFVQIjp0cnVlLCJTVEFSVFVQVElNRSI6IjIwMjItMDMtMjcgMTA6MzM6MDAuNTE3In0%3D; PHPSESSID=272ddjrulpg9snc3bam1ijd0am; ck_safe_chaoke_csrf_token=4226ebb212dabc7c958dee7fa7c92cb4; YKJ_IS_DIWORK=1; YKJ_DIWORK_DATA=%7B%22data%22%3A%7B%22is_diwork%22%3A1%2C%22cur_qzid%22%3A%22287259%22%7D%2C%22key%22%3A%22c459adf812925fb422fb5e2891d9694d%22%7D; tenantid=j6uzh3xb; Hm_lvt_e8002ef3d9e0d8274b5b74cc4a027d08=1648348386; Hm_lvt_b97569d26a525941d8d163729d284198=1648348386; businessDate=2022-03-27; locale=zh_CN; userId=e2b9bf88-7023-43d3-98ec-92c43ccc1561; tenantid=j6uzh3xb; yht_token=bttOWFTeGg0Ykh5VnpldGNyM0hxQzBXMkluNks1QTZVWEQ1endyMkw5MHlzZGRvcHM5ZnJaUkhVcmdoM2V2ZzFpZnI5UTJUb2EwdHRLY2dJdFpNQnlxZkxNYURZL2I0clFIT3N4VSsyQlJ0WGFUelRKQTNZQ1gzYWtpZkZiei8yT2lfX2V1Yy55b255b3VjbG91ZC5jb20.__6e901f6fcce300a4b4b995f1f14fadbf_1648348380309; tenantId=j6uzh3xb; userId=e2b9bf88-7023-43d3-98ec-92c43ccc1561; acw_tc=2760829d16483720229025722e608c05c50be1cec5570bc20deba84c76852f; Hm_lpvt_b97569d26a525941d8d163729d284198=1648372640; Hm_lpvt_e8002ef3d9e0d8274b5b74cc4a027d08=1648372640; FZ_STROAGE.diwork.com=eyJBUktTVVBFUiI6eyJ0ZW5hbnRfaWQiOiJqNnV6aDN4YiIsImNvbXBhbnkiOiLmsZ%2Foi4%2FlhYjkuLDnurPnsbPmnZDmlpnnp5HmioDmnInpmZDlhazlj7giLCJ1c2VyX2lkIjoiZTJiOWJmODgtNzAyMy00M2QzLTk4ZWMtOTJjNDNjY2MxNTYxIiwidXNlcl9uYW1lIjoi5p2O6JCM6JCMIiwicHJvZHVjdF9pZCI6InU4YzMuMCIsInByb2R1Y3RfbmFtZSI6IllvblN1aXRlIn0sIlNFRVNJT05JRCI6IjlkMDA3Yjg2ZmQ5NWJlNWUiLCJTRUVTSU9OREFURSI6MTY0ODM3Mjc0MzIyOCwiQU5TQVBQSUQiOiI0ZDBmMDBmNmI1ZmFiNDdkIiwiQU5TJERFQlVHIjoyLCJBTlNVUExPQURVUkwiOiJodHRwczovL2FydC5kaXdvcmsuY29tLyIsIkZSSVNUREFZIjoiMjAyMjAzMjciLCJGUklTVElNRSI6ZmFsc2UsIkFSS19MT0dJTklEIjoiZTJiOWJmODgtNzAyMy00M2QzLTk4ZWMtOTJjNDNjY2MxNTYxIiwiQVJLX0lEIjoiSlM3NDQyYjAxMDAwMWQ2M2RlMjJhZjZmMjA3MmM2NzkxMTc0NDIiLCJBUktGUklTVFBST0ZJTEUiOiIyMDIyLTAzLTI3IDEwOjMzOjAwLjUxMSIsIkFOU1NFUlZFUlRJTUUiOjB9");

                Buffer buffer = Buffer.buffer("{\"begin\":0,\"groupnum\":10,\"conditions\":[{\"field\":\"def1\"," +
                        "\"operator\":\"=\",\"value\":\"setting_contrastu8c\"},{\"field\":\"accpurposes\"," +
                        "\"operator\":\"=\",\"value\":\"01\"},{\"field\":\"targetvouchertype\",\"operator\":\"=\"," +
                        "\"value\":\"glvoucher\"}],\"paras\":[],\"fields\":[]}");
                request.send(buffer, s -> {
                    if (s.succeeded()) {
                        HttpClientResponse response = s.result();
                        response.bodyHandler(buf -> System.out.println(buf.toString()));
                    }
                });
            });
        }
    }
}
