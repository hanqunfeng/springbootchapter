package org.piaoyi.data.gitlab;

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

@Component("GitlabMainResultRunner")
public class GitlabMainResultRunner extends BaseBeanRunner {

    private String STARTURL = "https://gitlab.xxxx.com";


    @Override
    public List<HttpRequest> makeHttpUrls(Map<String, Object> map) {
        List<HttpRequest> requestList = new ArrayList<>();
        requestList.add(new HttpGetRequest(STARTURL));
        requestList.add(new HttpGetRequest("https://gitlab.xxxx.com/dashboard"));
        return requestList;
    }

    @Override
    public void init(Map<String, Object> map) {
        super.init(map);

        List<String> cookies = new ArrayList<>();
        //String cookie = "_gitlab_session=xxxxxx; expires=Wed, 20 Jul 2021 11:17:28; path=/; domain=gitlab.xxxx.com;secure;";
        //String cookie = "_gitlab_session=xxxxxx; path=/; domain=gitlab.xxxx.com;";
        String cookie = "_gitlab_session=xxxxx;";
        cookies.add(cookie);
        setCookies(cookies.toArray(new String[cookies.size()]));

    }
}
