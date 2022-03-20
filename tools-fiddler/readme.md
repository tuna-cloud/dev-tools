
一:根证书
1. 创建根证书密钥文件(自己做CA) root.key
   openssl genrsa -des3 -out root.key 2048

2. 创建根证书的申请文件 root.csr
   openssl req -new -key root.key -out root.csr

3. 创建一个自当前日期起为期十年的根证书 root.crt
   openssl x509 -req -days 3650 -sha256 -signkey root.key -in root.csr -out root.crt

4. 导出P12
   openssl pkcs12 -export -in root.crt -inkey root.key -out root.p12 -name www.tuna.com
5. P12 -> jks
   keytool -importkeystore -v -srckeystore root.p12 -srcstoretype pkcs12 -destkeystore root.jks -deststoretype pkcs12 -alias tuna.com

二. 服务端证书
1.创建服务器证书密钥 server.key
openssl genrsa -des3 -out server.key 2048

2.创建服务器证书的申请文件 server.csr
openssl req -new -key server.key -out server.csr

3.创建自当前日期起有效期为期1年的服务器证书 server.crt
openssl x509 -req -days 365 -sha256 -CA root.crt -CAkey root.key -CAcreateserial -in server.csr -out server.crt -extensions req_ext -extfile ssl.conf
4. 导出P12
   openssl pkcs12 -export -in server.crt -inkey server.key -out server.jks -name baidu.com






   P12(pfx) ——> JKS
   keytool -importkeystore -v -srckeystore tuna.p12 -srcstoretype pkcs12 -destkeystore tuna.jks -deststoretype pkcs12 -alias tuna.com
   JSK ——>P12
   keytool -importkeystore -srckeystore test.jks -srcstoretype JKS -deststoretype PKCS12 -destkeystore test1.p12
   JKS——->CER
   keytool -export -alias tuna.com -keystore tuna.jks -storepass 123456 -file tuna.cer
   CER——->JKS
   keytool -import -v -alias test -file tuna.csr -keystore tuna.jks -storepass 123456 -noprompt
