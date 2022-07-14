package org.piaoyi.data.github;

import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import org.piaoyi.common.BaseBeanRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h1>github查询启动</h1>
 * Created by hanqf on 2022/7/6 15:33.
 */

@Component("GithubSearchResultRunner")
public class GithubSearchResultRunner extends BaseBeanRunner {

    /**
     * github支持查询前100页，第100页时，没有nextpage的连接，所以会自动停止抓取
    */
    private String STARTURL = "https://github.com/search?l=&p=1&q={query}&ref=advsearch&type=Repositories";

    /**
     * 多个关键字使用+号分隔
    */
    private String query = "spring+xx";

    private String language = "Java";

    @Override
    public List<HttpRequest> makeHttpUrls(Map<String, Object> map) {
        List<HttpRequest> requestList = new ArrayList<>();
        STARTURL = STARTURL.replace("{query}", query + "+language%3A" + language);
        requestList.add(new HttpGetRequest(STARTURL));
        return requestList;
    }
}
