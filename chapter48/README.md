
## 生成jks证书
```shell script
# 默认证书有效期3个月
$ keytool -genkeypair -alias oauth2 -keyalg RSA -keysize 2048 -keypass 123456 -keystore oauth2_key.jks -storepass 123456
# 指定证书有效期，这里设置有效期大约10年
$ keytool -genkeypair -alias oauth2 -keyalg RSA -keysize 2048 -validity 36500 -keypass 123456 -keystore oauth2_key.jks -storepass 123456

您的名字与姓氏是什么?
  [Unknown]:  han
您的组织单位名称是什么?
  [Unknown]:  han
您的组织名称是什么?
  [Unknown]:  han
您所在的城市或区域名称是什么?
  [Unknown]:  bj
您所在的省/市/自治区名称是什么?
  [Unknown]:  bj
该单位的双字母国家/地区代码是什么?
  [Unknown]:  zh
CN=han, OU=han, O=han, L=bj, ST=bj, C=zh是否正确?
  [否]:  y


Warning:
JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore oauth2_key.jks -destkeystore oauth2_key.jks -deststoretype pkcs12" 迁移到行业标准格式 PKCS12。
```

### 按照警告执行如下命令
```shell script
$ keytool -importkeystore -srckeystore oauth2_key.jks -destkeystore oauth2_key.jks -deststoretype pkcs12
输入源密钥库口令:  
已成功导入别名 oauth2 的条目。
已完成导入命令: 1 个条目成功导入, 0 个条目失败或取消

Warning:
已将 "oauth2_key.jks" 迁移到 Non JKS/JCEKS。将 JKS 密钥库作为 "oauth2_key.jks.old" 进行了备份。

```

## 导出公钥
```shell script
$ keytool -list -rfc --keystore oauth2_key.jks | openssl x509 -inform pem -pubkey
输入密钥库口令:  123456
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmVdbw3XtFamdasjItlko
m8SyJiH0DnSUm1tqJDHY9orHA+0oa+VAlvxiHHBegKMX6FmCX3HAoVzHuAlWAZp0
FyV0SUR4/tuOKOw/N7HbKGa/JZJSfaNdJAEobRxzd8woaLNlCRLelzDPhMy9kGtp
x+Kc60smeo6XpC7RT25Mf5DRvKRJo4RGbPQNj18hWKZtY/TFZYySpa57eI9VOM5u
fvWJkh3Jm5cOXHiHScmF4mdNATR3XQTHXD+TDu0rLgn7H4ap9uYDRTNVGVJ/JfYu
aCrzszMFt4b+JNxz1UL42tTgbtKj8TxUrTRGTI/7KiwD5wjtpISSxlqoK1c0qgCh
KQIDAQAB
-----END PUBLIC KEY-----
-----BEGIN CERTIFICATE-----
MIIDQTCCAimgAwIBAgIEZ0BO+jANBgkqhkiG9w0BAQsFADBRMQswCQYDVQQGEwJ6
aDELMAkGA1UECBMCYmoxCzAJBgNVBAcTAmJqMQwwCgYDVQQKEwNoYW4xDDAKBgNV
BAsTA2hhbjEMMAoGA1UEAxMDaGFuMB4XDTIwMTEwODEyNDI0M1oXDTIxMDIwNjEy
NDI0M1owUTELMAkGA1UEBhMCemgxCzAJBgNVBAgTAmJqMQswCQYDVQQHEwJiajEM
MAoGA1UEChMDaGFuMQwwCgYDVQQLEwNoYW4xDDAKBgNVBAMTA2hhbjCCASIwDQYJ
KoZIhvcNAQEBBQADggEPADCCAQoCggEBAJlXW8N17RWpnWrIyLZZKJvEsiYh9A50
lJtbaiQx2PaKxwPtKGvlQJb8YhxwXoCjF+hZgl9xwKFcx7gJVgGadBcldElEeP7b
jijsPzex2yhmvyWSUn2jXSQBKG0cc3fMKGizZQkS3pcwz4TMvZBracfinOtLJnqO
l6Qu0U9uTH+Q0bykSaOERmz0DY9fIVimbWP0xWWMkqWue3iPVTjObn71iZIdyZuX
Dlx4h0nJheJnTQE0d10Ex1w/kw7tKy4J+x+GqfbmA0UzVRlSfyX2Lmgq87MzBbeG
/iTcc9VC+NrU4G7So/E8VK00RkyP+yosA+cI7aSEksZaqCtXNKoAoSkCAwEAAaMh
MB8wHQYDVR0OBBYEFKM+xW8pT4EHc+5/svExkEKw8c3CMA0GCSqGSIb3DQEBCwUA
A4IBAQB3OHZ1BrT+2hUFxhAk7K/7hC8tHVF8iRXhGBdnZWHNz2GRfzxOeyS4Amkp
wCixoKg9GPhUL1fxBya7Z7wjn4jS5f9b5q40c/fP23zIrmbJ3rsqlMnx3vqrcnJB
KF1l9i9h4iLDoTSS1HC42CuPSCSV/4/g2zXrZPWMVMHPHM9Ul8q+aTE7VaRzRsf8
h7xCeqZXRg2z+wFHH4B1LMcfOHwpGWVJ46xgNqt3cx4IovVTcuXbcQGKgd2+/UuQ
vugI0kWUCOH+CME9wbncIEJlDT1510GJIyt4EWyU3nio+vsLnlKz2jRP7QSE5dxY
ps3Y1u9/nrBl1yK4+KEFAguUikbp
-----END CERTIFICATE-----
```
这段就是公钥，保存到文件即可
```shell script
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmVdbw3XtFamdasjItlko
m8SyJiH0DnSUm1tqJDHY9orHA+0oa+VAlvxiHHBegKMX6FmCX3HAoVzHuAlWAZp0
FyV0SUR4/tuOKOw/N7HbKGa/JZJSfaNdJAEobRxzd8woaLNlCRLelzDPhMy9kGtp
x+Kc60smeo6XpC7RT25Mf5DRvKRJo4RGbPQNj18hWKZtY/TFZYySpa57eI9VOM5u
fvWJkh3Jm5cOXHiHScmF4mdNATR3XQTHXD+TDu0rLgn7H4ap9uYDRTNVGVJ/JfYu
aCrzszMFt4b+JNxz1UL42tTgbtKj8TxUrTRGTI/7KiwD5wjtpISSxlqoK1c0qgCh
KQIDAQAB
-----END PUBLIC KEY-----
```

## 查看密钥信息
```shell script
$ keytool -list -v -keystore oauth2_key.jks
输入密钥库口令:  
密钥库类型: PKCS12
密钥库提供方: SUN

您的密钥库包含 1 个条目

别名: oauth2
创建日期: 2020-11-9
条目类型: PrivateKeyEntry
证书链长度: 1
证书[1]:
所有者: CN=han, OU=han, O=han, L=bj, ST=bj, C=zh
发布者: CN=han, OU=han, O=han, L=bj, ST=bj, C=zh
序列号: 67404efa
有效期为 Sun Nov 08 20:42:43 CST 2020 至 Sat Feb 06 20:42:43 CST 2021
证书指纹:
         MD5:  3E:15:19:80:91:10:00:A8:63:A6:5E:19:5A:0E:A9:E5
         SHA1: E8:BB:68:99:7E:33:6D:51:40:EF:C0:AC:91:A6:93:15:ED:FE:F8:3A
         SHA256: CE:C7:C3:BF:BB:94:28:64:1B:EC:1C:F3:A9:DC:40:C5:53:AD:F2:27:01:83:8D:37:90:E0:EB:DB:C9:73:A5:5C
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展: 

#1: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: A3 3E C5 6F 29 4F 81 07   73 EE 7F B2 F1 31 90 42  .>.o)O..s....1.B
0010: B0 F1 CD C2                                        ....
]
]



*******************************************
*******************************************

```