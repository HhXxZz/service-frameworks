package org.hxz.service.frameworks.dao.builder.annotation;

import java.lang.annotation.*;

/**
 * User: IceRao
 * Date: 2010-3-2
 * Time: 10:17:58
 * Description:用于标注某个field是否为对应vo的主键
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    boolean autoIncrement() default true;
}
