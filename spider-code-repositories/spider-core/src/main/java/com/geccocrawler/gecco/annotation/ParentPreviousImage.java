/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:ParentPreviousImage.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package com.geccocrawler.gecco.annotation;

import java.lang.annotation.*;

/**
 * 表示该字段是一个图片类型的元素，jsoup会默认获取元素的src属性值。属性必须是String类型。
 * 
 * @author huchengyi
 *
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentPreviousImage {

	/**
	 * 默认获取src属性值，可以多选，按顺序查找
	 * 
	 * @return 属性名称
	 */
	String[] value() default "src";
	
	/**
	 * 如果填写本地路径将会自动下载到本地
	 * 
	 * @return 本地路径
	 */
	String download() default "";

	/**
	 * jquery风格的元素选择器，使用jsoup实现。jsoup在分析html方面提供了极大的便利
	 *
	 * 从当前节点向后查找
	 * @return 元素选择器
	 */
	String cssPath() default "";
}
