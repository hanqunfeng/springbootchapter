/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:ParentHref.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package com.geccocrawler.gecco.annotation;

import java.lang.annotation.*;

/**
 * 表示该字段是一个链接类型的元素，jsoup会默认获取元素的href属性值。
 *
 * @author huchengyi
 *
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentHref {

	/**
	 * 默认获取href属性值，可以多选，按顺序查找
	 *
	 * @return 属性名
	 */
	String[] value() default "href";

	/**
	 * 表示是否点击打开，继续让爬虫抓取
	 *
	 * @return 是否继续抓取
	 */
	boolean click() default false;

	/**
	 * jquery风格的元素选择器，使用jsoup实现。jsoup在分析html方面提供了极大的便利
	 *
	 * 从当前节点向后查找
	 * @return 元素选择器
	 */
	String cssPath() default "";

	/**
	 * 设置跳过哪个链接的匹配正则
	 */
	String[] discardPattern() default "";

}
