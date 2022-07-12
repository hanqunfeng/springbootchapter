package org.piaoyi.data.gitee.htmlbean;

import com.geccocrawler.gecco.annotation.*;
import lombok.Data;
import org.piaoyi.common.BaseHtmlBean;

import java.util.List;

/**
 * <h1>github仓库简介</h1>
 * Created by hanqf on 2022/7/7 10:34.
 */

@Data
public class GiteeRepositorySummaryHtmlBean extends BaseHtmlBean {

    /**
     * 本页的仓库url
    */
    @Href
    @HtmlField(cssPath = "div.item > div.header > div > a")
    private String repositoryUrl;



    /**
     * 简介
     */
    @Text
    @HtmlField(cssPath = "div.item > div.desc")
    private String repositoryDesc;


    /**
     * 最后更新时间，不是代码最后更新时间，仓库中任何属性发生变化都会更新这个时间
     */
    @BrotherPreviousText
    @HtmlField(cssPath = "div.item > div > a.tag.explain.right")
    private String repositoryUpdateDateTime;

    /**
     * star数量
     */
    @Text
    @HtmlField(cssPath = "div.item > div > a.tag.stars.theme-hover > em")
    private String repositoryStars;

    /**
     * 语言
     */
    @Text
    @HtmlField(cssPath = "div.item > div > span.tag.lang")
    private String repositoryLanguage;

    /**
     * topic列表
     */
    @Text
    @HtmlField(cssPath = "div.item > div.tags > a")
    private List<String> topicList;

    public String getRepositoryUrl() {
        if(repositoryUrl!=null) {
            repositoryUrl = repositoryUrl.replace("?_from=gitee_search", "");
        }
        return repositoryUrl;
    }
}
