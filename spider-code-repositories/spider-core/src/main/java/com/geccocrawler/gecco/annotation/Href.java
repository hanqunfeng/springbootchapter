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
public @interface Href {

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
	 * 设置跳过哪个链接的匹配正则
	*/
	String[] discardPattern() default "";

}
