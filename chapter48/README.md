
## 生成jks证书
```shell script
$ keytool -genkeypair -alias oauth2 -keyalg RSA -keypass 123456 -keystore oauth2_key.jks -storepass 123456

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

Warning:
JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore oauth2_key.jks -destkeystore oauth2_key.jks -deststoretype pkcs12" 迁移到行业标准格式 PKCS12。
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