package com.example.chapter50.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

/**
 * <p>国际化资源处理器</p>
 * Created by hanqf on 2021/4/13 10:10.
 *
 * 使用说明：@Configuration 注解的类中创建如下bean
 *
 *     @Bean("messageSource")
 *     public MessageSource messaeSource() {
 *         MyResourceBundleMessageSource messageSource = new MyResourceBundleMessageSource();
 *         messageSource.setPackagename("config.i18n");
 *         messageSource.setDefaultEncoding("utf-8");
 *         return messageSource;
 *     }
 *
 *     1. config.i18n，类路径下查找config/i18n文件夹及其子文件夹下的所有properties结尾的文件，如config/i18n/system/auth_messages.properties
 *     2. 支持多语言配置，即auth_messages_en_US.properties,auth_messages_zh_CN.properties，等等
 *     3. 查找范围包含当前项目及其依赖的所有jar
 */

public class MyResourceBundleMessageSource extends ResourceBundleMessageSource implements InitializingBean {
    /**
     * @Fields REG_EXP : TODO(匹配正则)
     */
    public static final String REG_EXP = "(_)("
            + StringUtils.join(Locale.getAvailableLocales(), '|') + ")$";

    /**
     * @Fields packagename : TODO(查找路径)
     */
    private String packagename = "";
    /**
     * @Fields resourcePattern : TODO(资源查找的匹配正则)
     */
    private String resourcePattern;
    /**
     * @Fields urlPrefix : TODO(类路径下查找)
     * 注意这里classpath后要增加*，表示查找范围包含所有jar，否则只会在当前项目的类路径下查找
     */
    private String urlPrefix = "classpath*:";
    /**
     * @Fields tempBaseNames : TODO(临时接收资源路径列表，用于合并自定义路径和原始路径)
     */
    private String[] tempBaseNames;

    public void setPackagename(String packagename) {
        packagename = StringUtils.trimToEmpty(packagename);
        if (!"".equals(packagename)) {
            this.packagename = packagename.replaceAll("\\.", "/");
        }
        if (!this.packagename.endsWith("/")) {
            this.packagename += "/";
        }
    }

    @Override
    public void afterPropertiesSet() {
        StringBuilder sb = new StringBuilder();
        sb.append(urlPrefix).append(packagename).append("**/")
                .append("*.properties");
        resourcePattern = sb.toString();

        try {
            String[] all = resolveBaseNames();
            if (!ArrayUtils.isEmpty(all)) {
                System.out.println(ArrayUtils.toString(all));
                super.setBasenames(all);
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setBasenames(String... basenames) {
        tempBaseNames = basenames;
    }

    public String[] resolveBaseNames() throws IOException {
        List<String> namesList = new ArrayList<String>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                new DefaultResourceLoader());
        Resource[] res = null;

        logger.info("starting to find resource with pattern ["
                + resourcePattern + "]");

        try {
            res = resolver.getResources(resourcePattern);
        } catch (Exception e) {
            logger.error("Find resource by pattern [" + resourcePattern
                    + "] failed!!", e);
        }

        if (res != null) {
            for (Resource item : res) {
                String realName = item.getURI().toString();
                //这种替换方式可以跨越多个项目，否则不同项目的根路径是不相同的
                realName = realName.substring(realName.indexOf(this.packagename));
                realName = realName.replaceAll("/", ".");
                realName = realName.replaceAll("\\.properties", "");
                realName = realName.replaceAll(REG_EXP, "");//要放到最后一个替换，去掉名称后面的语言类型，如_zh_CN，_en_US
                if (!namesList.contains(realName)) {
                    namesList.add(realName);
                }
            }
        }

        String[] resolvedBasenames = namesList.toArray(new String[]{});
        String[] all = mergeStringArrays(tempBaseNames, resolvedBasenames);

        return all;
    }


    /**
     * <h2>合并两个数组并去重</h2>
     * Created by hanqf on 2021/4/13 10:32. <br>
     *
     * @param array1
     * @param array2
     * @return java.lang.String[]
     * @author hanqf
     */
    public static String[] mergeStringArrays(String[] array1, String[] array2){
        if (ObjectUtils.isEmpty(array1)) {
            return array2;
        } else if (ObjectUtils.isEmpty(array2)) {
            return array1;
        } else {
            LinkedHashSet set = new LinkedHashSet();
            set.addAll(Arrays.asList(array1));
            set.addAll(Arrays.asList(array2));

            String[] c = (String[]) set.toArray(new String[0]);
            return c;
        }
    }

}

