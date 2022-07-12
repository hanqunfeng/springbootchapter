/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:ParentPreviousAttr.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package com.geccocrawler.gecco.annotation;

import java.lang.annotation.*;

/**
 * 获取html元素的attribute。属性支持java基本类型的自动转换。
 * 
 * @author huchengyi
 *
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentPreviousAttr {
	
	/**
	 * 表示属性名称
	 * @return 属性名称
	 */
	String value();

	/**
	 * jquery风格的元素选择器，使用jsoup实现。jsoup在分析html方面提供了极大的便利
	 *
	 * 从当前节点向后查找
	 * @return 元素选择器
	 */
	String cssPath() default "";
	
}
