package org.piaoyi.data.gitlab.pipeline;

import com.geccocrawler.gecco.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.piaoyi.data.gitlab.htmlbean.GitlabMainResultHtmlBean;
import org.springframework.stereotype.Component;

/**
 * <h1>github查询结果处理</h1>
 * Created by hanqf on 2022/7/6 15:16.
 */

@Slf4j
@Component("GitlabResultMainPipelineBean")
public class GitlabResultMainPipelineBean implements Pipeline<GitlabMainResultHtmlBean> {


    @Override
    public void process(GitlabMainResultHtmlBean bean) {
        System.out.println(bean);
    }
}
