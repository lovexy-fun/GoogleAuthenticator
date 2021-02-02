# GoogleAuthenticator
谷歌身份验证器工具

需要commons-codec包
```xml
<!-- https://mvnrepository.com/artifact/commons-codec/commons-codec -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.14</version>
</dependency>
```

通过工具类只能生成url，生成二维码的功能可以根据个人需求进行后端实现或者前端实现或者调用API生成。

生成的URL格式为：`otpauth://totp/[用户自定义信息]?secret=[密钥]`

测试类中已经生成了一个URL：[otpauth://totp/test@test.com?secret=X7YENRJJVCLC2Z7VDNA77RRXF7DQWACN](otpauth://totp/test@test.com?secret=X7YENRJJVCLC2Z7VDNA77RRXF7DQWACN)

二维码：

![](https://raw.githubusercontent.com/lovexy-fun/GoogleAuthenticator/master/img/qrcode.png)

谷歌和微软都有Authenticator软件。

iOS可以直接在APP Store下载，Android的要下载谷歌的需要梯子，推荐Android使用微软的Authenticator。

[GoogleAuthenticator5.10.apk](https://raw.githubusercontent.com/lovexy-fun/GoogleAuthenticator/master/apk/GoogleAuthenticator5.10.apk)