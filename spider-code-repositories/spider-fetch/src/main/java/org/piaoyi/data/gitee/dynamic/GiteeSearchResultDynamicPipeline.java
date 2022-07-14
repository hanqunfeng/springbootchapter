package org.piaoyi.data.gitee.dynamic;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import org.piaoyi.data.gitee.htmlbean.GiteeSearchResultHtmlBean;
import org.springframework.stereotype.Component;

/**
 * <h1>GiteeSearchResultDynamicPipeline</h1>
 * Created by hanqf on 2022/7/14 12:55.
 */

@Component("GiteeSearchResultDynamicPipeline")
public class GiteeSearchResultDynamicPipeline extends JsonPipeline {

    @Override
    public void process(JSONObject jsonObject) {
        System.out.println("GiteeSearchResultDynamicPipeline==" + jsonObject.toJSONString());
        final GiteeSearchResultHtmlBean giteeSearchResultHtmlBean = JSONObject.parseObject(jsonObject.toJSONString(), GiteeSearchResultHtmlBean.class);
        System.out.println("giteeSearchResultHtmlBean==" + giteeSearchResultHtmlBean);
    }
}
