package com.bitcoin.juwan.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: liumengqiang
 * Date : 2019/10/26
 * Description :
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface onClick {
    int[] value();
}
