package org.piaoyi.data.github.downloader;

import com.geccocrawler.gecco.annotation.GeccoClass;
import com.geccocrawler.gecco.downloader.BeforeDownload;
import com.geccocrawler.gecco.request.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.piaoyi.data.github.htmlbean.GithubSearchResultHtmlBean;

/**
 * <h1>下载前处理请求</h1>
 * 这里需要说明的是，针对同一个抓取器，前置或后置下载器都只能定义一个
 * Created by hanqf on 2022/7/7 18:20.
 */

@Slf4j
//指定对哪些抓取器起作用
@GeccoClass(value = {GithubSearchResultHtmlBean.class})
public class NothingBeforeDownload implements BeforeDownload {
    @Override
    public void process(HttpRequest request) {
        System.out.println("NothingBeforeDownload");
    }
}
