package com.example.support;


import com.example.utils.CP_ArrayUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p>国际化资源处理器</p>
 * Created by hanqf on 2020/6/22 16:46.
 */

public class CP_ResourceBundleMessageSource extends ResourceBundleMessageSource implements InitializingBean {
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
                logger.info(ArrayUtils.toString(all));
                super.setBasenames(all);
            }
        } catch (IOException e) {
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

        //获取项目的根路径
        //String rootpath = resolver.getClassLoader().getResource("//").toURI().toString(); // jar包下不能访问，war包不受影响
        //String rootpath = resolver.getClass().getResource("/").toURI().toString(); //这种方式jar和war都支持
        String rootpath = ResourceUtils.getURL(urlPrefix.replace("*", "")).toString(); //这种方式最方便

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
                //realName = realName.replaceAll(rootpath, "");
                realName = realName.replaceAll("/", ".");
                realName = realName.replaceAll("\\.properties", "");
                realName = realName.replaceAll(REG_EXP, "");//要放到最后一个替换，去掉名称后面的语言类型，如_zh_CN，_en_US
                if (!namesList.contains(realName)) {
                    namesList.add(realName);
                }
            }
        }
        String[] resolvedBasenames = namesList.toArray(new String[]{});

        return CP_ArrayUtils.mergeStringArrays(tempBaseNames, resolvedBasenames);
    }


}

