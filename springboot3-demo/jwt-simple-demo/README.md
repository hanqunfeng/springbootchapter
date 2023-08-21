
# jks密钥
```shell
# 创建
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

# 安装提示迁移密钥
$ keytool -importkeystore -srckeystore jks_key.jks -destkeystore jks_key.jks -deststoretype pkcs12
输入源密钥库口令:  123456
已成功导入别名 jks 的条目。
已完成导入命令: 1 个条目成功导入, 0 个条目失败或取消

Warning:
已将 "jks_key.jks" 迁移到 Non JKS/JCEKS。将 JKS 密钥库作为 "jks_key.jks.old" 进行了备份。


# 导出公钥
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

# -----BEGIN PUBLIC KEY----- 和 -----END PUBLIC KEY----- 之间的是公钥，将其复制到  jks_key.pub
```
