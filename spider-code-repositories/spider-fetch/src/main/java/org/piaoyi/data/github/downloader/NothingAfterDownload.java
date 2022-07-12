package org.piaoyi.data.github.downloader;

import com.geccocrawler.gecco.annotation.GeccoClass;
import com.geccocrawler.gecco.downloader.AfterDownload;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.response.HttpResponse;
import org.piaoyi.data.github.htmlbean.GithubSearchResultHtmlBean;

/**
 * <h1>下载后处理器</h1>
 * 这里需要说明的是，针对同一个抓取器，前置或后置下载器都只能定义一个
 * Created by hanqf on 2022/7/7 18:52.
 */

//指定对哪些抓取器起作用
@GeccoClass(value = {GithubSearchResultHtmlBean.class})
public class NothingAfterDownload implements AfterDownload {
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        System.out.println("NothingAfterDownload");
    }
}
