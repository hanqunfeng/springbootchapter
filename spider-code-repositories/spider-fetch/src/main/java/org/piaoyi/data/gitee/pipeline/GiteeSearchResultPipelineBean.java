package org.piaoyi.data.gitee.pipeline;

import com.geccocrawler.gecco.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.piaoyi.data.gitee.htmlbean.GiteeSearchResultHtmlBean;
import org.springframework.stereotype.Component;

/**
 * <h1>github查询结果处理</h1>
 * Created by hanqf on 2022/7/6 15:16.
 */

@Slf4j
@Component("GiteeSearchResultPipelineBean")
public class GiteeSearchResultPipelineBean implements Pipeline<GiteeSearchResultHtmlBean> {


    @Override
    public void process(GiteeSearchResultHtmlBean bean) {
        System.out.println(bean);
    }
}
