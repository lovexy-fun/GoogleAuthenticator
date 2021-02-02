package auth;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GoogleAuthenticator {

    /**
     * 偏移窗口大小<br/>
     * 0表示偏移窗口大小为0不进行偏移。<br/>
     * 1表示前一个校验码、当前校验码和下一个校验码均有效<br/>
     */
    private static final int WINDOW_SIZE = 1;

    /**
     * 验证码刷新时间
     */
    private static final long CODE_TIME_UNIT = 30L;

    /**
     * URL SCHEMA模板<br/>
     * otpauth://totp/[用户自定义信息]?secret=[密钥]
     */
    private static final String URL_SCHEMA = "otpauth://totp/%s?secret=%s";

    /**
     * 生成随机Base32串
     * @param seedSize seed bytes大小，取值5的倍数
     * @return 返回8的倍数长度的Base32串
     * @throws NoSuchAlgorithmException 不存在SHA1PRNG算法时会抛出异常
     */
    private static String genRandomBase32(int seedSize) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] buffer = sr.generateSeed(seedSize);
        Base32 codec = new Base32();
        byte[] bEncodedKey = codec.encode(buffer);
        return new String(bEncodedKey);
    }

    /**
     * 生成校验码
     * @param key 密钥
     * @param time 时间
     * @return 返回6位数校验码
     * @throws NoSuchAlgorithmException 不支持HmacSHA1时会抛出异常
     * @throws InvalidKeyException 初始化异常时会抛出
     */
    private static int genVerifyCode(byte[] key, long time) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = time;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

    /**
     * @param level SecretKey级别，不同级别生成的SecretKey长度不同，取值为正整数
     * @return 返回对应等级长度的SecretKey
     * @throws NoSuchAlgorithmException 不存在SHA1PRNG算法时会抛出异常
     */
    public static String genSecretKey(int level) throws NoSuchAlgorithmException {
        level = level <= 0 ? 1 : level;
        return genRandomBase32(level * 5);
    }

    /**
     * 生成URL
     * @param info 自定义信息
     * @param secretKey 密钥
     * @return 返回url schema
     */
    public static String genUrlSchema(String info, String secretKey) {
        return String.format(URL_SCHEMA, info, secretKey);
    }

    /**
     * 验证校验码
     * @param code 校验码
     * @param secretKey 密钥
     * @param time 时间
     * @return 校验成功返回true，失败返回false
     * @throws NoSuchAlgorithmException 不支持HmacSHA1时会抛出异常
     * @throws InvalidKeyException 初始化异常时会抛出
     */
    public static boolean auth(int code, String secretKey, long time) throws InvalidKeyException, NoSuchAlgorithmException {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secretKey);
        long t = (time / 1000L) / CODE_TIME_UNIT;
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; i++) {
            int hash = genVerifyCode(decodedKey, t + i);
            if (hash == code) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证校验码
     * @param code 校验码
     * @param secretKey 密钥
     * @return 校验成功返回true，失败返回false
     * @throws NoSuchAlgorithmException 不支持HmacSHA1时会抛出异常
     * @throws InvalidKeyException 初始化异常时会抛出
     */
    public static boolean auth(int code, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return auth(code, secretKey, System.currentTimeMillis());
    }

    /**
     * 验证校验码
     * @param code 校验码
     * @param secretKey 密钥
     * @return 校验成功返回true，失败返回false
     * @throws NoSuchAlgorithmException 不支持HmacSHA1时会抛出异常
     * @throws InvalidKeyException 初始化异常时会抛出
     */
    public static boolean auth(String code, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        int intCode;
        try {
            intCode = Integer.valueOf(code);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return auth(intCode, secretKey, System.currentTimeMillis());
    }
}
