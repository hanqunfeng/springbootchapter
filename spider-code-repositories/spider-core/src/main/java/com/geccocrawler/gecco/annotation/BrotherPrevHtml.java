/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:BrotherPrevHtml.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package com.geccocrawler.gecco.annotation;/**
 * Created by hanqf on 2020/4/7 16:54.
 */


import java.lang.annotation.*;

/**
 * @author hanqf
 * @date 2020/4/7 16:54
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BrotherPrevHtml{
    /**
     * 是否取外部Html，默认为false
     *
     * <pre>
     *  true  - 取外部HTML内容
     *  false - 取内容HTML内容（默认）
     * </pre>
     *
     * @author LiuJunGuang
     * @return 是否取外部Html
     */
    public boolean outer() default false;

    /**
     * jquery风格的元素选择器，使用jsoup实现。jsoup在分析html方面提供了极大的便利
     *
     * 从当前节点向后查找
     * @return 元素选择器
     */
    String cssPath() default "";
}
