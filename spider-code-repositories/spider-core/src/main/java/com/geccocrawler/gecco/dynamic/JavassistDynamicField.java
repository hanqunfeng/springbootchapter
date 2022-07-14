/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:JavassistDynamicField.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package com.geccocrawler.gecco.dynamic;

import com.geccocrawler.gecco.annotation.*;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 动态生成属性的注解
 *
 * @author huchengyi
 */
public class JavassistDynamicField implements DynamicField {

    private static Log log = LogFactory.getLog(JavassistDynamicField.class);

    private DynamicBean dynamicBean;

    private CtField cfield;

    private ConstPool cpool;

    private AnnotationsAttribute attr;

    public JavassistDynamicField(DynamicBean dynamicBean, CtClass clazz, ConstPool cpool, String fieldName) {
        try {
            this.dynamicBean = dynamicBean;
            this.cpool = cpool;
            this.cfield = clazz.getField(fieldName);
            attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        } catch (NotFoundException e) {
            log.error(fieldName + " not found");
        }
    }

    @Override
    public DynamicBean build() {
        FieldInfo finfo = cfield.getFieldInfo();
        finfo.addAttribute(attr);
        return dynamicBean;
    }

    //@SuppressWarnings("AliDeprecation")
    //@Deprecated
    //@Override
    //public DynamicField htmlField(String cssPath) {
    //	return csspath(cssPath);
    //}

    @Override
    public DynamicField csspath(String cssPath) {
        Annotation annot = new Annotation(HtmlField.class.getName(), cpool);
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField text(boolean own) {
        Annotation annot = new Annotation(Text.class.getName(), cpool);
        annot.addMemberValue("own", new BooleanMemberValue(own, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField text() {
        return text(true);
    }

    @Override
    public DynamicField parentText() {
        return parentText(true, "");
    }

    @Override
    public DynamicField parentNextText() {
        return parentNextText(true, "");
    }

    @Override
    public DynamicField parentPreviousText() {
        return parentPreviousText(true, "");
    }

    @Override
    public DynamicField brotherPreviousText() {
        return brotherPreviousText(true, "");
    }

    @Override
    public DynamicField brotherNextText() {
        return brotherNextText(true, "");
    }

    @Override
    public DynamicField parentText(boolean own, String cssPath) {
        Annotation annot = new Annotation(ParentText.class.getName(), cpool);
        annot.addMemberValue("own", new BooleanMemberValue(own, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentNextText(boolean own, String cssPath) {
        Annotation annot = new Annotation(ParentNextText.class.getName(), cpool);
        annot.addMemberValue("own", new BooleanMemberValue(own, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentPreviousText(boolean own, String cssPath) {
        Annotation annot = new Annotation(ParentPreviousText.class.getName(), cpool);
        annot.addMemberValue("own", new BooleanMemberValue(own, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherPreviousText(boolean own, String cssPath) {
        Annotation annot = new Annotation(BrotherPreviousText.class.getName(), cpool);
        annot.addMemberValue("own", new BooleanMemberValue(own, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherNextText(boolean own, String cssPath) {
        Annotation annot = new Annotation(BrotherNextText.class.getName(), cpool);
        annot.addMemberValue("own", new BooleanMemberValue(own, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentHtml() {
        return parentHtml(false, "");
    }

    @Override
    public DynamicField parentNextHtml() {
        return parentNextHtml(false, "");
    }

    @Override
    public DynamicField parentPrevHtml() {
        return parentPrevHtml(false, "");
    }

    @Override
    public DynamicField brotherPrevHtml() {
        return brotherPrevHtml(false, "");
    }

    @Override
    public DynamicField brotherNextHtml() {
        return brotherNextHtml(false, "");
    }

    @Override
    public DynamicField parentHtml(boolean outer, String cssPath) {
        Annotation annot = new Annotation(ParentHtml.class.getName(), cpool);
        annot.addMemberValue("outer", new BooleanMemberValue(outer, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentNextHtml(boolean outer, String cssPath) {
        Annotation annot = new Annotation(ParentNextHtml.class.getName(), cpool);
        annot.addMemberValue("outer", new BooleanMemberValue(outer, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentPrevHtml(boolean outer, String cssPath) {
        Annotation annot = new Annotation(ParentPrevHtml.class.getName(), cpool);
        annot.addMemberValue("outer", new BooleanMemberValue(outer, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherPrevHtml(boolean outer, String cssPath) {
        Annotation annot = new Annotation(BrotherPrevHtml.class.getName(), cpool);
        annot.addMemberValue("outer", new BooleanMemberValue(outer, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherNextHtml(boolean outer, String cssPath) {
        Annotation annot = new Annotation(BrotherNextHtml.class.getName(), cpool);
        annot.addMemberValue("outer", new BooleanMemberValue(outer, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField html() {
        return html(false);
    }

    @Override
    public DynamicField html(boolean outer) {
        Annotation annot = new Annotation(Html.class.getName(), cpool);
        annot.addMemberValue("outer", new BooleanMemberValue(outer, cpool));
        attr.addAnnotation(annot);
        return this;
    }


    @Override
    public DynamicField href(String discardPattern, boolean click, String... value) {
        Annotation annot = new Annotation(Href.class.getName(), cpool);
        annot.addMemberValue("click", new BooleanMemberValue(click, cpool));
        annot.addMemberValue("discardPattern", new StringMemberValue(discardPattern, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField href(boolean click, String... value) {
        return href("", click, value);
    }

    @Override
    public DynamicField href(String... value) {
        return href(false, value);
    }

    @Override
    public DynamicField parentHref(String... value) {
        return parentHref("", false, value);
    }

    @Override
    public DynamicField parentNextHref(String... value) {
        return parentNextHref("", false, value);
    }

    @Override
    public DynamicField parentPrevHref(String... value) {
        return parentPrevHref("", false, value);
    }

    @Override
    public DynamicField brotherPrevHref(String... value) {
        return brotherPrevHref("", false, value);
    }

    @Override
    public DynamicField brotherNextHref(String... value) {
        return brotherNextHref("", false, value);
    }


    @Override
    public DynamicField parentHref(String discardPattern, String cssPath, boolean click, String... value) {
        Annotation annot = new Annotation(ParentHref.class.getName(), cpool);
        annot.addMemberValue("click", new BooleanMemberValue(click, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        annot.addMemberValue("discardPattern", new StringMemberValue(discardPattern, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentHref(String cssPath, boolean click, String... value) {
        return parentHref("", "", click, value);
    }


    @Override
    public DynamicField parentNextHref(String discardPattern, String cssPath, boolean click, String... value) {
        Annotation annot = new Annotation(ParentNextHref.class.getName(), cpool);
        annot.addMemberValue("click", new BooleanMemberValue(click, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        annot.addMemberValue("discardPattern", new StringMemberValue(discardPattern, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentNextHref(String cssPath, boolean click, String... value) {
        return parentNextHref("", cssPath, click, value);
    }


    @Override
    public DynamicField parentPrevHref(String discardPattern, String cssPath, boolean click, String... value) {
        Annotation annot = new Annotation(ParentPreviousHref.class.getName(), cpool);
        annot.addMemberValue("click", new BooleanMemberValue(click, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        annot.addMemberValue("discardPattern", new StringMemberValue(discardPattern, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentPrevHref(String cssPath, boolean click, String... value) {
        return parentPrevHref("", cssPath, click, value);
    }


    @Override
    public DynamicField brotherPrevHref(String discardPattern, String cssPath, boolean click, String... value) {
        Annotation annot = new Annotation(BrotherPreviousHref.class.getName(), cpool);
        annot.addMemberValue("click", new BooleanMemberValue(click, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        annot.addMemberValue("discardPattern", new StringMemberValue(discardPattern, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }


    @Override
    public DynamicField brotherPrevHref(String cssPath, boolean click, String... value) {
        return brotherPrevHref("", cssPath, click, value);
    }

    @Override
    public DynamicField brotherNextHref(String discardPattern, String cssPath, boolean click, String... value) {
        Annotation annot = new Annotation(BrotherNextHref.class.getName(), cpool);
        annot.addMemberValue("click", new BooleanMemberValue(click, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        annot.addMemberValue("discardPattern", new StringMemberValue(discardPattern, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherNextHref(String cssPath, boolean click, String... value) {
        return brotherNextHref("", cssPath, click, value);
    }

    @Override
    public DynamicField image(String download, String... value) {
        Annotation annot = new Annotation(Image.class.getName(), cpool);
        annot.addMemberValue("download", new StringMemberValue(download, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField image() {
        return image("");
    }

    @Override
    public DynamicField parentImage() {
        return parentImage("", "");
    }

    @Override
    public DynamicField parentNextImage() {
        return parentNextImage("", "");
    }

    @Override
    public DynamicField parentPrevImage() {
        return parentPrevImage("", "");
    }

    @Override
    public DynamicField brotherPrevImage() {
        return brotherPrevImage("", "");
    }

    @Override
    public DynamicField brotherNextImage() {
        return brotherNextImage("", "");
    }

    @Override
    public DynamicField parentImage(String download, String cssPath, String... value) {
        Annotation annot = new Annotation(ParentImage.class.getName(), cpool);
        annot.addMemberValue("download", new StringMemberValue(download, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentNextImage(String download, String cssPath, String... value) {
        Annotation annot = new Annotation(ParentNextImage.class.getName(), cpool);
        annot.addMemberValue("download", new StringMemberValue(download, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentPrevImage(String download, String cssPath, String... value) {
        Annotation annot = new Annotation(ParentPreviousImage.class.getName(), cpool);
        annot.addMemberValue("download", new StringMemberValue(download, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherPrevImage(String download, String cssPath, String... value) {
        Annotation annot = new Annotation(BrotherPreviousImage.class.getName(), cpool);
        annot.addMemberValue("download", new StringMemberValue(download, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherNextImage(String download, String cssPath, String... value) {
        Annotation annot = new Annotation(BrotherNextImage.class.getName(), cpool);
        annot.addMemberValue("download", new StringMemberValue(download, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));

        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cpool);
        MemberValue[] memberValues = new StringMemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = new StringMemberValue(value[i], cpool);
        }
        arrayMemberValue.setValue(memberValues);
        annot.addMemberValue("value", arrayMemberValue);

        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField attr(String value) {
        Annotation annot = new Annotation(Attr.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(value, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentAttr(String value, String cssPath) {
        Annotation annot = new Annotation(ParentAttr.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(value, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentNextAttr(String value, String cssPath) {
        Annotation annot = new Annotation(ParentNextAttr.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(value, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField parentPrevAttr(String value, String cssPath) {
        Annotation annot = new Annotation(ParentPreviousAttr.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(value, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherPrevAttr(String value, String cssPath) {
        Annotation annot = new Annotation(BrotherPreviousAttr.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(value, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField brotherNextAttr(String value, String cssPath) {
        Annotation annot = new Annotation(BrotherNextAttr.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(value, cpool));
        annot.addMemberValue("cssPath", new StringMemberValue(cssPath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField ajax(String url) {
        Annotation annot = new Annotation(Ajax.class.getName(), cpool);
        annot.addMemberValue("url", new StringMemberValue(url, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField request() {
        Annotation annot = new Annotation(Request.class.getName(), cpool);
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField requestParameter(String param) {
        Annotation annot = new Annotation(RequestParameter.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(param, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField requestParameter() {
        return requestParameter("");
    }

    @Override
    public DynamicField jsvar(String var, String jsonpath) {
        Annotation annot = new Annotation(RequestParameter.class.getName(), cpool);
        annot.addMemberValue("var", new StringMemberValue(var, cpool));
        annot.addMemberValue("jsonpath", new StringMemberValue(jsonpath, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField jsvar(String var) {
        return jsvar(var, "");
    }

    @Override
    public DynamicField jsonpath(String value) {
        Annotation annot = new Annotation(JSONPath.class.getName(), cpool);
        annot.addMemberValue("value", new StringMemberValue(value, cpool));
        attr.addAnnotation(annot);
        return this;
    }

    @Override
    public DynamicField renderName(String value) {
        Annotation renderName = new Annotation(FieldRenderName.class.getName(), cpool);
        renderName.addMemberValue("value", new StringMemberValue(value, cpool));
        attr.addAnnotation(renderName);
        return this;
    }

    @Override
    public DynamicField customAnnotation(Annotation annotation) {
        attr.addAnnotation(annotation);
        return this;
    }

    @Override
    public ConstPool getConstPool() {
        return this.cpool;
    }
}
