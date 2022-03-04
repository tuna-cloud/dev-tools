# 证书生成
1. openssl req -newkey rsa:2048 -nodes -out tuna.csr -keyout tuna.key
  password:tuna.tools
2. openssl x509 -req -days 3650 -in tuna.csr -signkey tuna.key -out tuna.crt
3. openssl pkcs12 -export -in tuna.crt -inkey tuna.key -out tuna.p12
4. keytool -importkeystore -v -srckeystore tuna.p12 -srcstoretype pkcs12 -destkeystore tuna.jks -deststoretype jks
5. keytool -importkeystore -srckeystore tuna.jks -destkeystore tuna.jks -deststoretype pkcs12