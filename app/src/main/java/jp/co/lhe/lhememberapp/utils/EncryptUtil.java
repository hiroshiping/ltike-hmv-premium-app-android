package jp.co.lhe.lhememberapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import jp.co.lhe.lhememberapp.R;

public class EncryptUtil {

    /**
     * 第一パスワード
     */
    private static final String FIRST_PASSWORD = "3acd187a938ce5ccd6e03a97eee57be708bf5455";

    /**
     * 初期ベクトル
     */
    private static final byte[] IV = {74, 12, 65, -70, 29, 0, -14, 117, 72, -47, 101, -32, 80, 71, -74, 16};

    /** 暗号化キー生成時の変換回数. */
    private static final int ITERATION_COUNT = 1024;
    /** 暗号化キーの長さ. */
    private static final int KEY_LENGTH = 256;

    /**
     * 暗号化キー生成アルゴリズム
     */
    private static final String KEY_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
    /**
     * 暗号化アルゴリズム
     */
    private static final String ENCRYPT_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * コンストラクタ
     */
    private EncryptUtil() {
    }

    /**
     * <pre>
     * 暗号化を行う
     * </pre>
     *
     * @param context  コンテキスト
     * @param contents 暗号化対象の文字列
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws PackageManager.NameNotFoundException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String encryptString(Context context, String contents)
        throws NoSuchAlgorithmException, InvalidKeySpecException,
        PackageManager.NameNotFoundException, InvalidKeyException, NoSuchPaddingException,
        InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        // 共通鍵の生成
        SecretKey secretKey = generateKey(context);

        // 共通鍵を使って暗号化
        byte[] encrypted = encryptByteArray(contents.getBytes(), secretKey);

        // Byte配列をStringに変換し、結果として返す
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    /**
     * <pre>
     * 復号化を行う
     * </pre>
     *
     * @param context  コンテキスト
     * @param contents 復号化対象の文字列
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws PackageManager.NameNotFoundException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decryptString(Context context, String contents)
        throws NoSuchAlgorithmException, InvalidKeySpecException,
        PackageManager.NameNotFoundException, InvalidKeyException, NoSuchPaddingException,
        InvalidAlgorithmParameterException, IllegalBlockSizeException,
        BadPaddingException {

        // 共通鍵の生成
        SecretKey secretKey = generateKey(context);

        // Byte配列への変換
        byte[] crypted = Base64.decode(contents, Base64.DEFAULT);

        // 復号化
        byte[] decrypted = decryptByte(crypted, secretKey);

        // Stringに変換し、結果として返す
        return new String(decrypted);
    }

    /**
     * <pre>
     * 与えられたKeyを使って暗号化を行う
     * </pre>
     *
     * @param src 　暗号化対象の文字列をByte配列に変換したもの
     * @param key 共通鍵
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static byte[] encryptByteArray(byte[] src, Key key)
        throws NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidKeyException, InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException {

        // 暗号アルゴリズムにAESを指定
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);

        // 暗号化モードに設定し、Keyを指定
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));

        // 暗号化の実行
        byte[] encrypted = cipher.doFinal(src);

        return encrypted;
    }

    /**
     * <pre>
     * 与えられたKeyを使って復号化を行う
     * </pre>
     *
     * @param src 復号化対象の文字列をByte配列に変換したもの
     * @param key 共通鍵
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static byte[] decryptByte(byte[] src, SecretKey key)
        throws NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidKeyException, InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException {

        // 暗号アルゴリズムにAESを指定
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);

        // 復号化モードに設定し、Keyを指定
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));

        // 復号化の実行
        byte[] decrypted = cipher.doFinal(src);
        return decrypted;
    }

    /**
     * <pre>
     * 共通鍵を生成する
     * </pre>
     *
     * @param context
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws PackageManager.NameNotFoundException
     */
    private static SecretKey generateKey(Context context)
        throws NoSuchAlgorithmException, InvalidKeySpecException,
        PackageManager.NameNotFoundException {

        // Keyを生成するためのパスワードを取得
        char[] password = generatePassword(context);

        byte[] salt = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime).getBytes();

        // Keyを生成
        KeySpec keySpec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secretKey = factory.generateSecret(keySpec);

        return secretKey;
    }

    /**
     * <pre>
     * 共通鍵を生成するためのパスワードを生成する
     * </pre>
     *
     * @param context コンテキスト
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private static char[] generatePassword(Context context)
        throws PackageManager.NameNotFoundException {

        String password1 = FIRST_PASSWORD;
        String password2 = context.getString(R.string.second_password);

        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < Math.max(password1.length(), password2.length()); i++) {
            if (i < password1.length()) {
                passwordBuilder.append(password1.charAt(i));
            }
            if (i < password2.length()) {
                passwordBuilder.append(password2.charAt(i));
            }
        }

        StringBuffer passwordBuffer = new StringBuffer(passwordBuilder.toString());
        String passwordString = passwordBuffer.reverse().toString();
        return passwordString.toCharArray();
    }


}
