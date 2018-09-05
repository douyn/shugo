package com.dou.shugo.shugo_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: dou
 * Time: 18-9-3  上午10:23
 * Decription:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface SHugo {
}
