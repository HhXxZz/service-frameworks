package org.hxz.service.frameworks.utils;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * Created by someone on 2017-02-09.
 *
 */
@SuppressWarnings("unused")
public enum VerificationCode {
    INSTANCE;
    // ===========================================================
    // Constants
    // ===========================================================
    // 验证码范围,去掉0(数字)和O(拼音)容易混淆的(小写的1和L也可以去掉,大写不用了)
    private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W','X', 'Y', 'Z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    // ===========================================================
    // Fields
    // ===========================================================
    public DefaultKaptcha mKaptcha = new DefaultKaptcha();

    // ===========================================================
    // Constructors
    // ===========================================================
    VerificationCode() {
        this.mKaptcha.setConfig(new Config(new Properties()));
    }


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================

    public BufferedImage createVerificationImage(int pLength, int width, int height){
        StringBuilder randomCode = new StringBuilder();
        for (int i = 0; i < pLength ; i++) {
            String strRand = String.valueOf(CHARS[MathUtils.random(CHARS.length)]);
            randomCode.append(strRand);
        }
        return mKaptcha.createImage(randomCode.toString());
    }

    public static void main(String[] args) {

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
