package org.hxz.service.frameworks.utils;

import com.google.common.base.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.config.ConfigLoader;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * <pre>
 * Created by someone on 2017-03-24.
 *
 * </pre>
 */
@SuppressWarnings("unused")
public enum EmailUtil {
    INSTANCE;
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(EmailUtil.class);

    public static final String EMAIL_CONFIG = "mail.default.properties";

    // ===========================================================
    // Fields
    // ===========================================================
    private AtomicBoolean mInitialed = new AtomicBoolean(false);
    private Mailer        mMailer;

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    @SuppressWarnings("SameParameterValue")
    public void init(String pPropertiesName){
        if(!StringUtil.isEmpty(pPropertiesName) && mInitialed.compareAndSet(false, true)){
            try {
                final InputStream input = EmailUtil.class.getClassLoader().getResourceAsStream(
                        StringUtil.isEmpty(pPropertiesName) ? EMAIL_CONFIG : pPropertiesName);
                Properties prop = new Properties();
                assert input != null;
                prop.load(new InputStreamReader(input, Charsets.UTF_8));

                ConfigLoader.loadProperties(prop, false);
            } catch (IOException e) {
                LOG.error("EmailUtil initial failed", e);
            }

            mMailer = MailerBuilder.withDebugLogging(false).buildMailer();
        }
    }

    public void send(Email email){
        if(mInitialed.get()) {
            if (mMailer.validate(email)) {
                mMailer.sendMail(email);
            }
        }
    }

    public static void main(String[] args){
        String content = "" +
                "<style>" +
                "html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, blockquote, pre, a, abbr, acronym, address, big, cite, code," +
                "del, dfn, em, font, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, p, tt, var, b, u, i, center, dl, dt, dd, ol, ul, li," +
                "fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td, textarea{margin:0; padding:0; border:0; outline:0; }" +
                "html{overflow-x:hidden;}" +
                "table {border-collapse: collapse; border-spacing: 0;}" +
                "body {font-family:\"Microsoft YaHei\",Helvetica,Arial,sans-serif; font-size:14px;color:#6e726e; line-height:170%; width:100%; background: #f7f7f7;font-weight: normal;}" +
                "ul, li{ list-style-type:none;}" +
                "em, i{ font-style:normal;}" +
                "img{zoom:1; vertical-align: middle;}" +
                "a{outline: 0; text-decoration: none; color:#6e726e;}" +
                "a:hover {color:red;}" +
                ".clear{clear:both;height:0px; font-size:0px; line-height:0px;display: block;visibility:hidden;}" +
                ".main{max-width: 750px;margin: 0 auto;background: #fff;}" +
                ".header{border-bottom: 1px solid #f7f7f7;height: 132px;line-height: 132px;}" +
                ".header .logo img{height: 132px;}" +
                ".text{padding: 30px 50px 20px 50px;border-bottom: 1px solid #f7f7f7;}" +
                ".text p{text-indent: 28px;margin-bottom: 20px;line-height:180%;}" +
                ".con{text-align: center;padding: 1px 0; border-bottom: 1px solid #f7f7f7}" +
                ".con img{width: 750px;}" +
                ".footer{text-align: center;background: #f7f7f7;padding: 40px 0 60px 0;}" +
                ".footer .code{margin-bottom: 20px;}" +
                ".footer .code img{width:172px; ;}" +
                ".footer .copyright{font-size: 12px;color: #999999}" +
                "</style>" +
                "<div class=\"main\">" +
                "<div class=\"header\">" +
                "<div class=\"logo\"><img src=\"http://www.haokeshuyu.com/images/logo.png\"></div>" +
                "</div>" +
                "<div class=\"text\">" +
                "<p>收到这个邮件说明邮件发送成功了</p>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<div class=\"code\"><img src=\"http://www.haokeshuyu.com/images/pic_code.png\"></div>" +
                "<div class=\"copyright\">Copyright © 2016 Common Tech.(Shenzhen)Co.,Ltd<br/><a href=\"http://www.miitbeian.gov.cn\" target=\"_blank\">粤ICP备xxx号</a></div>" +
                "</div>" +
                "<div class=\"clear\"></div>" +
                "</div>";

        Email email = EmailBuilder.startingBlank()
                .from("AA", "xi.qin@test.com")
                .to("BB", "xi.qin@test.com")
                .withSubject("hey")
                .withHTMLText(content)
                .buildEmail();

        EmailUtil.INSTANCE.init(null);
        EmailUtil.INSTANCE.send(email);
    }



    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
