package test;

import auth.GoogleAuthenticator;

import java.util.Scanner;

public class TestMain {
    public static void main(String[] args) {
        try {
            String secretKey = GoogleAuthenticator.genSecretKey(4);
            String urlSchema = GoogleAuthenticator.genUrlSchema("test@test.com", secretKey);
            System.out.println("secretKey:" + secretKey);
            System.out.println("urlSchema:" + urlSchema);

            // otpauth://totp/test@test.com?secret=X7YENRJJVCLC2Z7VDNA77RRXF7DQWACN
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("请输入校验码：");
                String code = scanner.nextLine();
                if ("q".equalsIgnoreCase(code)) {
                    break;
                }
                boolean b = GoogleAuthenticator.auth(code, "X7YENRJJVCLC2Z7VDNA77RRXF7DQWACN");
                System.out.println("校验结果：" + b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
