package base.service.frameworks.processor.annotation;

import base.service.frameworks.base.ApiFactory;

import java.lang.annotation.*;

/**
 * Created by someone on 2020/12/1 21:58.
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface API {
    String uri();
    String name() default "unnamed";

    boolean enableToken() default true;

    boolean enableMember() default false;

    boolean enableAuthentication() default false;

    boolean enableEncryption() default false;

    String method() default ApiFactory.GET;
    String[] allow() default {};
    String version() default "1";
}
