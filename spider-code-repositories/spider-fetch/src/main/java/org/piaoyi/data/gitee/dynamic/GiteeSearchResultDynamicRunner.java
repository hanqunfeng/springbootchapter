package org.piaoyi.data.gitee.dynamic;

import com.geccocrawler.gecco.dynamic.DynamicGecco;
import org.piaoyi.common.BaseDynamicRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>动态启动器</h1>
 * Created by hanqf on 2022/7/14 11:17.
 */

@Component("GiteeSearchResultDynamicRunner")
public class GiteeSearchResultDynamicRunner extends BaseDynamicRunner {

    /**
     * gitee只能支持查询前20页的数据，第20页时，nextpage依然可以获取到值，但是返回页面都是第20页的内容，所以不会自动停止抓取，
     * 再抓取也没有意义，所以需要想办法关闭
     */
    private String STARTURL = "https://search.gitee.com/?q={query}&type=repository&lang={language}&pageno=1";

    private String query = "spring+xx";

    private String language = "java";

    private String MATCH_URL = "https://search.gitee.com/?q={query}&type={type}&lang={language}&pageno={page}";

    @Override
    public String[] makeHttpUrls() {
        List<String> urls = new ArrayList<>();
        STARTURL = STARTURL.replace("{query}", query);
        STARTURL = STARTURL.replace("{language}", language);
        urls.add(STARTURL);
        return urls.toArray(new String[]{});
    }

    @Override
    public void makeDynamicGecco() {
        final Class<?> repositorySummaryList = DynamicGecco.html()
                .stringField("repositoryUrl").csspath("div.item > div.header > div > a").href().build()
                .stringField("repositoryDesc").csspath("div.item > div.desc").text().build()
                .stringField("repositoryUpdateDateTime").csspath("div.item > div > a.tag.explain.right").brotherPreviousText().build()
                .stringField("repositoryStars").csspath("div.item > div > a.tag.stars.theme-hover > em").text().build()
                .stringField("repositoryLanguage").csspath("div.item > div > span.tag.lang").text().build()
                //注意这里只能返回Object,其它类型会报告类型转换异常
                .listField("topicList", Object.class).csspath("div.item > div.tags > a").build()
                .register();


        DynamicGecco.html()
                .gecco(MATCH_URL, "consolePipeline","GiteeSearchResultDynamicPipeline")
                .intField("page").requestParameter("page").build()
                .stringField("query").requestParameter("query").build()
                .stringField("type").requestParameter("type").build()
                .stringField("language").requestParameter("language").build()
                .stringField("logo").csspath("div.header-box > div > a.logo-box > img").image("/Users/hanqf/idea_workspaces2/spider-code-repositories/imgDownload").build()
                .stringField("resultNum").csspath("div.main > div.col_01.three_cols > div > div.hits-header > div.stats > b").text().build()
                .stringField("nextPage").csspath("ul.pagination > li.next > a").href("^.*q=.*&type=.*&lang=.*&pageno=21$",true).build()
                .requestField("request").request().build()
                .listField("repositorySummaryList", repositorySummaryList).csspath("#hits-list > div").html(true).build()
                .register();
    }
}
