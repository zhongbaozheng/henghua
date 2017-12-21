package com.hengdian.henghua.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EnDecodeUtil {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(simpleEnDecode("123", 'w'));
        System.out.println(simpleEnDecode(simpleEnDecode("123", 'w'), 'w'));
    }

    /**
     * MD5加密 生成32位md5码(大写字母)
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     */
    public final static String MD5InUpperCase(String text)
            throws NoSuchAlgorithmException {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        byte[] bytesInput = text.getBytes();
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest md5Inst = MessageDigest.getInstance("MD5");
        // 使用指定的字节更新摘要
        md5Inst.update(bytesInput);
        // 获得密文
        byte[] md5Bytes = md5Inst.digest();
        // 把密文转换成十六进制的字符串形式
        int j = md5Bytes.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md5Bytes[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * MD5加密 生成32位md5码(小写字母)
     *
     * @param text
     * @return 异常返回NULL
     */
    public static String MD5InLowerCase(String text) {

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }catch (NoSuchAlgorithmException e){
            return null;
        }
        char[] charArray = text.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    /**
     * 抑或运算简单加密解密算法 执行一次加密，两次解密
     *
     * @param text
     * @param key  用作密钥的字符
     * @return
     */
    public static String simpleEnDecode(String text, char key) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] ^ key);
        }
        return new String(chars);
    }

}
