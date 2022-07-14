/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:DynamicField.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package com.geccocrawler.gecco.dynamic;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

public interface DynamicField {

    /**
     * 动态生成属性注解
     *
     * @return DynamicField
     */
    public DynamicField request();

    public DynamicField requestParameter(String param);

    public DynamicField requestParameter();

    ///**
    // * replace by csspath
    // * @param cssPath cssPath
    // * @return DynamicField
    // */
    //@Deprecated
    //public DynamicField htmlField(String cssPath);

    public DynamicField csspath(String cssPath);

    public DynamicField text(boolean own);

    public DynamicField text();

    //自定义注解
    public DynamicField parentText();

    public DynamicField parentNextText();

    public DynamicField parentPreviousText();

    public DynamicField brotherPreviousText();

    public DynamicField brotherNextText();

    public DynamicField parentText(boolean own, String cssPath);

    public DynamicField parentNextText(boolean own, String cssPath);

    public DynamicField parentPreviousText(boolean own, String cssPath);

    public DynamicField brotherPreviousText(boolean own, String cssPath);

    public DynamicField brotherNextText(boolean own, String cssPath);


    public DynamicField html();

    public DynamicField html(boolean outer);

    public DynamicField parentHtml();

    public DynamicField parentNextHtml();

    public DynamicField parentPrevHtml();

    public DynamicField brotherPrevHtml();

    public DynamicField brotherNextHtml();

    public DynamicField parentHtml(boolean outer, String cssPath);

    public DynamicField parentNextHtml(boolean outer, String cssPath);

    public DynamicField parentPrevHtml(boolean outer, String cssPath);

    public DynamicField brotherPrevHtml(boolean outer, String cssPath);

    public DynamicField brotherNextHtml(boolean outer, String cssPath);

    public DynamicField href(boolean click, String... value);

    public DynamicField href(String discardPattern, boolean click, String... value);

    public DynamicField href(String... value);

    public DynamicField parentHref(String... value);

    public DynamicField parentNextHref(String... value);

    public DynamicField parentPrevHref(String... value);

    public DynamicField brotherPrevHref(String... value);

    public DynamicField brotherNextHref(String... value);

    public DynamicField parentHref(String discardPattern, String cssPath, boolean click, String... value);

    public DynamicField parentHref(String cssPath, boolean click, String... value);

    public DynamicField parentNextHref(String discardPattern, String cssPath, boolean click, String... value);

    public DynamicField parentNextHref(String cssPath, boolean click, String... value);

    public DynamicField parentPrevHref(String discardPattern, String cssPath, boolean click, String... value);

    public DynamicField parentPrevHref(String cssPath, boolean click, String... value);

    public DynamicField brotherPrevHref(String discardPattern, String cssPath, boolean click, String... value);

    public DynamicField brotherPrevHref(String cssPath, boolean click, String... value);

    public DynamicField brotherNextHref(String discardPattern, String cssPath, boolean click, String... value);

    public DynamicField brotherNextHref(String cssPath, boolean click, String... value);

    public DynamicField image(String download, String... value);

    public DynamicField image();

    public DynamicField parentImage();

    public DynamicField parentNextImage();

    public DynamicField parentPrevImage();

    public DynamicField brotherPrevImage();

    public DynamicField brotherNextImage();

    public DynamicField parentImage(String download, String cssPath, String... value);

    public DynamicField parentNextImage(String download, String cssPath, String... value);

    public DynamicField parentPrevImage(String download, String cssPath, String... value);

    public DynamicField brotherPrevImage(String download, String cssPath, String... value);

    public DynamicField brotherNextImage(String download, String cssPath, String... value);

    public DynamicField attr(String value);

    public DynamicField parentAttr(String value, String cssPath);

    public DynamicField parentNextAttr(String value, String cssPath);

    public DynamicField parentPrevAttr(String value, String cssPath);

    public DynamicField brotherPrevAttr(String value, String cssPath);

    public DynamicField brotherNextAttr(String value, String cssPath);


    public DynamicField ajax(String url);

    public DynamicField jsvar(String var, String jsonpath);

    public DynamicField jsvar(String var);

    public DynamicField jsonpath(String value);

    public DynamicField renderName(String value);

    public DynamicBean build();

    public DynamicField customAnnotation(Annotation annotation);

    public ConstPool getConstPool();
}
