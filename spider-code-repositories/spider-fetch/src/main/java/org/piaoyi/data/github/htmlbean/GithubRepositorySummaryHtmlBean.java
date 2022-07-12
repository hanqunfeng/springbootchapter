package org.piaoyi.data.github.htmlbean;

import com.geccocrawler.gecco.annotation.Attr;
import com.geccocrawler.gecco.annotation.Href;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.Text;
import lombok.Data;
import org.piaoyi.common.BaseHtmlBean;

import java.util.List;

/**
 * <h1>github仓库简介</h1>
 * Created by hanqf on 2022/7/7 10:34.
 */

@Data
public class GithubRepositorySummaryHtmlBean extends BaseHtmlBean {

    /**
     * 本页的仓库url
    */
    @Href
    @HtmlField(cssPath = "div.mt-n1.flex-auto > div.d-flex > div > a")
    private String repositoryUrl;



    /**
     * 简介
     */
    @Text
    @HtmlField(cssPath = "div.mt-n1.flex-auto > p.mb-1")
    private String repositoryDesc;


    /**
     * 最后更新时间，不是代码最后更新时间，仓库中任何属性发生变化都会更新这个时间
     */
    @Attr(value = "datetime")
    @HtmlField(cssPath = "relative-time")
    private String repositoryUpdateDateTime;

    /**
     * star数量
     */
    @Text
    @HtmlField(cssPath = "a.Link--muted")
    private Integer repositoryStars;

    /**
     * 语言
     */
    @Text
    @HtmlField(cssPath = "span[itemprop=\"programmingLanguage\"]")
    private String repositoryLanguage;

    /**
     * topic列表
     */
    @Text
    @HtmlField(cssPath = "a[data-ga-click=\"Topic, search results\"]")
    private List<String> topicList;

}
