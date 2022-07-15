package org.piaoyi.data.gitee.htmlbean;

import com.geccocrawler.gecco.annotation.*;
import com.geccocrawler.gecco.request.HttpRequest;
import lombok.Data;
import org.piaoyi.common.BaseHtmlBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h1>github搜索结果</h1>
 * Created by hanqf on 2022/7/6 15:09.
 */

@Component("GiteeSearchResultHtmlBean")
@Gecco(matchUrl = "https://search.gitee.com/?q={query}&type={type}&lang={language}&pageno={page}", pipelines = {"GiteeSearchResultPipelineBean"}, timeout = 3000)
@Data
public class GiteeSearchResultHtmlBean extends BaseHtmlBean {


    private static final long serialVersionUID = -5741703927403198440L;

    @Request
    private HttpRequest request;

    public String getCurrentUrl() {
        return request.getUrl();
    }

    /**
     * 当前访问的url
    */
    private String currentUrl;
    /**
     * 页码，从1开始
     */
    @RequestParameter("page")
    private Integer page;

    /**
     * 查询关键字，多个用+号分隔
     */
    @RequestParameter("query")
    private String query;

    /**
     * 查询类型，如repository
     */
    @RequestParameter("type")
    private String type;

    /**
     * 语言类型，如java
     */
    @RequestParameter("language")
    private String language;

    @Image(download = "/Users/hanqf/idea_workspaces2/spider-code-repositories/imgDownload")
    @HtmlField(cssPath = "div.header-box > div > a.logo-box > img")
    private String logo;

    /**
     * 结果数量
     */
    @Text
    @HtmlField(cssPath = "div.main > div.col_01.three_cols > div > div.hits-header > div.stats > b")
    private String resultNum;

    /**
     * 下一页url,discardPattern指定哪种url不进行调用的正则表达式
     */
    @Href(value = "href", click = true, discardPattern = "^.*q=.*&type=.*&lang=.*&pageno=21$")
    //@Href(value = "href", click = true, discardPattern = "https://search.gitee.com/?q={query}&type={type}&lang={language}&pageno=21")
    @HtmlField(cssPath = "ul.pagination > li.next > a")
    private String nextPage;


    @Html(outer = true)
    @HtmlField(cssPath = "#hits-list > div")
    private List<GiteeRepositorySummaryHtmlBean> repositorySummaryList;


}
