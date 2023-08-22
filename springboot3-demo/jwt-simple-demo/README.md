
# jks密钥
* 创建，注意这里后缀 `.jks` 其实是什么都可以，就是一个文件名称，不表示文件类型，也可以是 `.keystore`
```shell
$ keytool -genkeypair -alias jks -keyalg RSA -keysize 2048 -validity 36500 -keypass 123456 -keystore jks_key.jks -storepass 123456
您的名字与姓氏是什么?
  [Unknown]:  jks
您的组织单位名称是什么?
  [Unknown]:  jks
您的组织名称是什么?
  [Unknown]:  jks
您所在的城市或区域名称是什么?
  [Unknown]:  jks
您所在的省/市/自治区名称是什么?
  [Unknown]:  jks
该单位的双字母国家/地区代码是什么?
  [Unknown]:  jks
CN=jks, OU=jks, O=jks, L=jks, ST=jks, C=jks是否正确?
  [否]:  y

Warning:
JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore jks_key.jks -destkeystore jks_key.jks -deststoretype pkcs12" 迁移到行业标准格式 PKCS12。  

# 参数说明
    -keystore：设置生成的文件名称，包含后缀；
    -alias：设置别名
    -keysize：key的大小，一般设置为2048或4096
    -storepass：设置文件的密码
    -keypass：设置key的密码
    -keyalg：设置使用的加密算法，一般写RSA
    -validity：设置有效期
```

* 也可以直接在命令行指定`dname`信息
```shell
$ keytool -genkeypair -alias jks -keyalg RSA -keysize 2048 -validity 36500 -keypass 123456 -keystore jks_key.jks -storepass 123456 -dname "cn=jks,ou=jks,o=jks,l=jks,st=jks,c=jks"
```

* 按照提示迁移密钥类型，这样就可以去除警告了
```shell
$ keytool -importkeystore -srckeystore jks_key.jks -destkeystore jks_key.jks -deststoretype pkcs12
输入源密钥库口令:  123456
已成功导入别名 jks 的条目。
已完成导入命令: 1 个条目成功导入, 0 个条目失败或取消

Warning:
已将 "jks_key.jks" 迁移到 Non JKS/JCEKS。将 JKS 密钥库作为 "jks_key.jks.old" 进行了备份。
```
* 查看密码信息
```shell
$  keytool -list -v -keystore jks_key.jks   
```
* 导出公钥
```shell
$ keytool -list -rfc -keystore jks_key.jks | openssl x509 -inform pem -pubkey
输入密钥库口令:  123456
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApYlqcxpWcj0qQ3p5P4Ad
pnYq6+6U0JBMPbuTu/vciuVIix5HkLHzxjPqBCEUm783sopn4okn76xkHFeXM+CA
5R+Bp3PaYglITzQ98Cwu0Cr8iEmQJ8hoqgRYDGcsBwTXqmmf8rENoHEn3KQmqxsy
wB9oiLhtImctfYxgok5tYoiXkyjKhqELiaf2R6dKZYKrr9IECUVf7f+TOh3CTFq9
MHpKux9v/gfIm8fFXR8RWnPgpZV+/hjqB5zjvnKqy3jHmTPFa3Zb2unZfTIKR7a5
NQY8i1Sxm8+GXRdpNXmG8KtlQbeOUh5j0t/S3O9pH/cPIRIHBR2vRSdmOyjmfN9o
SwIDAQAB
-----END PUBLIC KEY-----
-----BEGIN CERTIFICATE-----
MIIDSTCCAjGgAwIBAgIEMdR+fTANBgkqhkiG9w0BAQsFADBUMQwwCgYDVQQGEwNq
a3MxDDAKBgNVBAgTA2prczEMMAoGA1UEBxMDamtzMQwwCgYDVQQKEwNqa3MxDDAK
BgNVBAsTA2prczEMMAoGA1UEAxMDamtzMCAXDTIzMDgyMTA3NTYwNloYDzIxMjMw
NzI4MDc1NjA2WjBUMQwwCgYDVQQGEwNqa3MxDDAKBgNVBAgTA2prczEMMAoGA1UE
BxMDamtzMQwwCgYDVQQKEwNqa3MxDDAKBgNVBAsTA2prczEMMAoGA1UEAxMDamtz
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApYlqcxpWcj0qQ3p5P4Ad
pnYq6+6U0JBMPbuTu/vciuVIix5HkLHzxjPqBCEUm783sopn4okn76xkHFeXM+CA
5R+Bp3PaYglITzQ98Cwu0Cr8iEmQJ8hoqgRYDGcsBwTXqmmf8rENoHEn3KQmqxsy
wB9oiLhtImctfYxgok5tYoiXkyjKhqELiaf2R6dKZYKrr9IECUVf7f+TOh3CTFq9
MHpKux9v/gfIm8fFXR8RWnPgpZV+/hjqB5zjvnKqy3jHmTPFa3Zb2unZfTIKR7a5
NQY8i1Sxm8+GXRdpNXmG8KtlQbeOUh5j0t/S3O9pH/cPIRIHBR2vRSdmOyjmfN9o
SwIDAQABoyEwHzAdBgNVHQ4EFgQUQm4bTIQXv/euuMxXE9tam8TRgoUwDQYJKoZI
hvcNAQELBQADggEBAKHXEALVlktY+boUZsVwncFWtqz0fBTr7BYxx9/FWNoZeZKs
1gpyVKLU92xy97n10kP+7gxeIu8y/frqTvW0ZrBqvvyazRIcCRw5vVwS3GuCp5nL
+Pxi0sBUzQtfIm9BwZoug8l4GY+CVNgetfNvKn8CPik2L3l20juL5D6xnziQMet4
ZUq9LoTJlNux+bHKkrkWSkpJUxI3Lc8ilymVqMVhm9+uIF1wygd4ul7U7sKCGbl5
48XFcY2vEB4Qvs7gGeL91dSr6P2QnJ1LnI/5hJSwO9PuPXy8oL2wfMx/1JB+37yZ
7rHDErDHqlnsmP9NjwCLWYD1TL/m3F98BAM01hQ=
-----END CERTIFICATE-----
```
* `-----BEGIN PUBLIC KEY----- 和 -----END PUBLIC KEY-----` 之间的是公钥，将其复制到 `jks_key.pub`

# 扩展
## 准备
* 先将`jks`转换为`pkcs12`后，即可通过`openssl`命令进行转换，注意这里后缀 `.p12` 其实是什么都可以，就是一个文件名称，不表示文件类型，也可以是 `.pfx`
```shell
$ keytool -importkeystore -srckeystore jks_key.jks -srcstoretype jks -destkeystore server.p12 -deststoretype pkcs12
```

## jks转pem
```shell
# 转换
$ openssl pkcs12 -in server.p12 -out server.pem -nodes 
# 查看pem信息
$ openssl x509 -noout -in server.pem -text
```

## jks转crt、key和pri_pem、pub_pem
* p12转crt，有的也叫cer，别纠结，一回事
```shell
$ openssl pkcs12 -in server.p12 -nokeys -clcerts -out server.crt
```
* p12转key
```shell
$ openssl pkcs12 -in server.p12 -nocerts -nodes -out server.key
```
* 验证crt和key是否匹配，输出结果一致则匹配
```shell
$ openssl x509 -noout -modulus -in server.crt | openssl md5
7c51ec684bf7dd827c02212abd6ba9b9
$ openssl rsa -noout -modulus -in server.key | openssl md5
7c51ec684bf7dd827c02212abd6ba9b9
```
* 通过crt和key合并为p12(pfx)
```shell
$ openssl pkcs12 -export -in server.crt -inkey server.key -out server.pfx
```

* key转pem结尾的私钥
```shell
$ openssl rsa -in server.key -out server_privkey.pem
```
* key转pem结尾的公钥
```shell
$ openssl rsa -in server.key -pubout -out server_public.pem
```

