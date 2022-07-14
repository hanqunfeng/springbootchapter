package org.piaoyi.data.gitlab.htmlbean;

import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.RequestParameter;
import com.geccocrawler.gecco.annotation.Text;
import lombok.Data;
import org.piaoyi.common.BaseHtmlBean;
import org.springframework.stereotype.Component;

/**
 * <h1>github搜索结果</h1>
 * Created by hanqf on 2022/7/6 15:09.
 */

@Component("GitlabMainResultHtmlBean")
@Gecco(downloader = "cp_httpClientDownloader", matchUrl = {"https://gitlab.xxxx.com{path:[^\\?]*}{qm:[\\?]?}{param:.*}"}, pipelines = {"GitlabResultMainPipelineBean"}, timeout = 3000)
@Data
public class GitlabMainResultHtmlBean extends BaseHtmlBean {


    private static final long serialVersionUID = -5741703927403198440L;

    /**
     * path
     */
    @RequestParameter("path")
    private String path;

    /**
     * param
     */
    @RequestParameter("param")
    private String param;
    /**
     * 帐号名称
     */
    @Text
    @HtmlField(cssPath = "div.page-sidebar-expanded.page-with-sidebar > div.sidebar-wrapper.nicescroll > a > div")
    private String username;


}
