package org.piaoyi.data.github.pipeline;

import com.geccocrawler.gecco.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.piaoyi.data.github.htmlbean.GithubSearchResultHtmlBean;
import org.springframework.stereotype.Component;

/**
 * <h1>github查询结果处理</h1>
 * Created by hanqf on 2022/7/6 15:16.
 */

@Slf4j
@Component("GithubSearchResultPipelineBean")
public class GithubSearchResultPipelineBean implements Pipeline<GithubSearchResultHtmlBean> {


    @Override
    public void process(GithubSearchResultHtmlBean bean) {
        System.out.println(bean);
    }
}
