/*
 * COPYRIGHT Beijing NetQin-Tech Co.,Ltd.                                   *
 ****************************************************************************
 * 源文件名:  CP_SimpleBaseController.java
 * 功能: cpframework框架													   
 * 版本:	@version 1.0	                                                                   
 * 编制日期: 2014年1月6日 下午2:15:31 						    						                                        
 * 修改历史: (主要历史变动原因及说明)		
 * YYYY-MM-DD |    Author      |	 Change Description		      
 * 2014年1月6日    |    hanqunfeng     |     Created 
 */
package com.cas.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Description: <Controller基类>. <br>
 * <p>
 * <使用说明>
 * </p>
 */
public abstract class BaseController implements ApplicationContextAware {
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext ;

	}

	/**
	 * 描述 : <获得当前环境的applicationContext>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @return
	 */
	protected ApplicationContext getApplicationContext() {
		return ctx;
	}

	/**
	 * 描述 : <获得多语言的资源内容>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @param code
	 * @param args
	 * @return
	 */
	protected String getMessage(String code, Object[] args) {
		return ctx.getMessage(code, args, LocaleContextHolder.getLocale());
	}

	protected String getMessage(String code) {
		return getMessage(code, new Object[]{});
	}

	/**
	 * 描述 : <获得多语言的资源内容>. <br>
	 * <p>
	 * <使用方法说明>
	 * </p>
	 * 
	 * @param code
	 * @param args
	 * @param defaultMessage
	 * @return
	 */
	protected String getMessage(String code, Object[] args,
                                String defaultMessage) {
		return ctx.getMessage(code, args, defaultMessage,
				LocaleContextHolder.getLocale());
	}

	/**                                                          
	* 描述 : <获得applicationContext中的对象>. <br> 
	*<p> 
		<使用方法说明>  
	 </p>                                                                                                                                                                                                                                                
	* @param beanName
	* @return                                                                                                      
	*/  
	protected Object getBean(String beanName) {
		Object obj = null;
		try {
			obj = this.getApplicationContext().getBean(beanName);
		} catch (Exception e) {
			// TODO: handle exception
			obj = null;
		}

		return obj;
	}

}
