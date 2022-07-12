package org.piaoyi.data.github.htmlbean;

import com.geccocrawler.gecco.annotation.*;
import lombok.Data;
import org.piaoyi.common.BaseHtmlBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h1>github搜索结果</h1>
 * Created by hanqf on 2022/7/6 15:09.
 */

@Component("GithubSearchResultHtmlBean")
@Gecco(matchUrl = "https://github.com/search?l=&p={page}&q={query}&ref=advsearch&type={type}", pipelines = {"GithubSearchResultPipelineBean"}, timeout = 3000)
@Data
public class GithubSearchResultHtmlBean extends BaseHtmlBean {


    private static final long serialVersionUID = -5741703927403198440L;

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
     * 查询类型，如Repositories
     */
    @RequestParameter("type")
    private String type;

    /**
     * 结果数量
    */
    @Text
    @HtmlField(cssPath = "#js-pjax-container > div > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results > div > div.d-flex.flex-column.flex-md-row.flex-justify-between.border-bottom.pb-3.position-relative > h3")
    private String resultNum;

    /**
     * 下一页url
    */
    @Href(value = "href",click = true)
    @HtmlField(cssPath = "#js-pjax-container > div > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results > div > div.paginate-container.codesearch-pagination-container > div > a.next_page")
    private String nextPage;


    ///**
    // * 本页的仓库url
    //*/
    //@Href
    //@HtmlField(cssPath = "#js-pjax-container > div > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results > div > ul > li > div.mt-n1.flex-auto > div.d-flex > div > a")
    //private List<String> repositoryUrl;

    @Html(outer = true)
    @HtmlField(cssPath = "#js-pjax-container > div > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results > div > ul > li > div.mt-n1.flex-auto")
    private List<GithubRepositorySummaryHtmlBean> repositorySummaryList;


}
