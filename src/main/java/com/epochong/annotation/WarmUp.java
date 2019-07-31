package com.epochong.annotation;

/**
 * @author epochong
 * @date 2019/7/13 9:28
 * @email epochong@163.com
 * @blog epochong.github.io
 * @describe 预热
 */

public @interface WarmUp {
    int iterations() default 0;
}
