package org.piaoyi.data.gitee;

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

@Component("GiteeSearchResultRunner")
public class GiteeSearchResultRunner extends BaseBeanRunner {

    /**
     * gitee只能支持查询前20页的数据，第20页时，nextpage依然可以获取到值，但是返回页面都是第20页的内容，所以不会自动停止抓取，
     * 再抓取也没有意义，所以需要想办法关闭
     */
    private String STARTURL = "https://search.gitee.com/?q={query}&type=repository&lang={language}&pageno=1";

    private String query = "spring+xx";

    private String language = "java";

    @Override
    public List<HttpRequest> makeHttpUrls(Map<String, Object> map) {
        List<HttpRequest> requestList = new ArrayList<>();
        STARTURL = STARTURL.replace("{query}", query);
        STARTURL = STARTURL.replace("{language}", language);
        HttpRequest httpRequest = new HttpGetRequest(STARTURL);
        requestList.add(httpRequest);
        return requestList;
    }
}
