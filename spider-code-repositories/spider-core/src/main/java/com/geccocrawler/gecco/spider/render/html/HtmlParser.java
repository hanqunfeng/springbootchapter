/*
 * ************************************************************
 * Copyright (c) 2020. Beijing CXZH-Tech Co.,Ltd.
 * ************************************************************
 * File:HtmlParser.java
 * 修改历史：(主要历史变动原因及说明)
 *  YYYY-MM-DD      |     Author    |    Change Description
 *  2020-04-11            hanqf           Created
 * ************************************************************
 */

package com.geccocrawler.gecco.spider.render.html;

import com.geccocrawler.gecco.annotation.*;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.response.HttpResponse;
import com.geccocrawler.gecco.spider.SpiderBean;
import com.geccocrawler.gecco.spider.SpiderThreadLocal;
import com.geccocrawler.gecco.spider.conversion.Conversion;
import com.geccocrawler.gecco.spider.render.Render;
import com.geccocrawler.gecco.spider.render.RenderContext;
import com.geccocrawler.gecco.spider.render.RenderType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

    private Log log;

    private Document document;

    private String baseUri;

    public HtmlParser(String baseUri, String content) {
        long beginTime = System.currentTimeMillis();
        log = LogFactory.getLog(HtmlParser.class);
        this.baseUri = baseUri;
        if (isTable(content)) {
            this.document = Jsoup.parse(content, baseUri, Parser.xmlParser());
        } else {
            this.document = Jsoup.parse(content, baseUri);
        }
        long endTime = System.currentTimeMillis();
        if (log.isTraceEnabled()) {
            log.trace("init html parser : " + (endTime - beginTime) + "ms");
        }
    }

    public String baseUri() {
        return baseUri;
    }

    public Object $basic(String selector, Field field) throws Exception {
        if (field.isAnnotationPresent(Text.class)) {// @Text
            Text text = field.getAnnotation(Text.class);
            String value = $text(selector, text.own());
            return Conversion.getValue(field.getType(), value);
        } else if (field.isAnnotationPresent(ParentNextText.class)) {// @ParentNextText
            ParentNextText parentNextText = field.getAnnotation(ParentNextText.class);
            String value = $parentNextText(selector, parentNextText.own(), parentNextText.cssPath());
            return Conversion.getValue(field.getType(), value);
        } else if (field.isAnnotationPresent(ParentPreviousText.class)) {// @ParentPreviousText
            ParentPreviousText parentPreviousText = field.getAnnotation(ParentPreviousText.class);
            String value = $parentPreviousText(selector, parentPreviousText.own(), parentPreviousText.cssPath());
            return Conversion.getValue(field.getType(), value);
        } else if (field.isAnnotationPresent(ParentText.class)) {// @ParentText
            ParentText parentText = field.getAnnotation(ParentText.class);
            String value = $parentText(selector, parentText.own(), parentText.cssPath());
            return Conversion.getValue(field.getType(), value);
        } else if (field.isAnnotationPresent(BrotherNextText.class)) {// @BrotherNextText
            BrotherNextText brotherNextText = field.getAnnotation(BrotherNextText.class);
            String value = $brotherNextText(selector, brotherNextText.own(), brotherNextText.cssPath());
            return Conversion.getValue(field.getType(), value);
        } else if (field.isAnnotationPresent(BrotherPreviousText.class)) {// @BrotherPreviousText
            BrotherPreviousText brotherPreviousText = field.getAnnotation(BrotherPreviousText.class);
            String value = $brotherPreviousText(selector, brotherPreviousText.own(), brotherPreviousText.cssPath());
            return Conversion.getValue(field.getType(), value);
        } else if (field.isAnnotationPresent(Image.class)) {// @Image
            Image image = field.getAnnotation(Image.class);
            String imageSrc = $image(selector, image.value());
            //这里不需要处理下载，ImageFieldRender会处理下载逻辑
			//String localPath = DownloadImage.download(image.download(), imageSrc);
			//if (StringUtils.isNotEmpty(localPath)) {
			//	return localPath;
			//}
            return imageSrc;
        } else if (field.isAnnotationPresent(ParentImage.class)) {// @ParentImage
            ParentImage parentImage = field.getAnnotation(ParentImage.class);
            String imageSrc = $parentImage(selector, parentImage.cssPath(), parentImage.value());
            //String localPath = DownloadImage.download(parentImage.download(), imageSrc);
            //if (StringUtils.isNotEmpty(localPath)) {
            //    return localPath;
            //}
            return imageSrc;
        } else if (field.isAnnotationPresent(ParentNextImage.class)) {// @ParentNextImage
            ParentNextImage parentNextImage = field.getAnnotation(ParentNextImage.class);
            String imageSrc = $parentNextImage(selector, parentNextImage.cssPath(), parentNextImage.value());
            //String localPath = DownloadImage.download(parentNextImage.download(), imageSrc);
            //if (StringUtils.isNotEmpty(localPath)) {
            //    return localPath;
            //}
            return imageSrc;
        } else if (field.isAnnotationPresent(ParentPreviousImage.class)) {// @ParentPreviousImage
            ParentPreviousImage parentPreviousImage = field.getAnnotation(ParentPreviousImage.class);
            String imageSrc = $parentPreviousImage(selector, parentPreviousImage.cssPath(), parentPreviousImage.value());
            //String localPath = DownloadImage.download(parentPreviousImage.download(), imageSrc);
            //if (StringUtils.isNotEmpty(localPath)) {
            //    return localPath;
            //}
            return imageSrc;
        } else if (field.isAnnotationPresent(BrotherNextImage.class)) {// @BrotherNextImage
            BrotherNextImage brotherNextImage = field.getAnnotation(BrotherNextImage.class);
            String imageSrc = $brotherNextImage(selector, brotherNextImage.cssPath(), brotherNextImage.value());
            //String localPath = DownloadImage.download(brotherNextImage.download(), imageSrc);
            //if (StringUtils.isNotEmpty(localPath)) {
            //    return localPath;
            //}
            return imageSrc;
        } else if (field.isAnnotationPresent(BrotherPreviousImage.class)) {// @BrotherPreviousImage
            BrotherPreviousImage brotherPreviousImage = field.getAnnotation(BrotherPreviousImage.class);
            String imageSrc = $brotherPreviousImage(selector, brotherPreviousImage.cssPath(), brotherPreviousImage.value());
            //String localPath = DownloadImage.download(brotherPreviousImage.download(), imageSrc);
            //if (StringUtils.isNotEmpty(localPath)) {
            //    return localPath;
            //}
            return imageSrc;
        } else if (field.isAnnotationPresent(Href.class)) {// @Href
            Href href = field.getAnnotation(Href.class);
            String url = $href(selector, href.value());
            return url;
        } else if (field.isAnnotationPresent(ParentHref.class)) {// @ParentHref
            ParentHref parentHref = field.getAnnotation(ParentHref.class);
            String url = $parentHref(selector, parentHref.cssPath(), parentHref.value());
            return url;
        } else if (field.isAnnotationPresent(ParentNextHref.class)) {// @ParentNextHref
            ParentNextHref parentNextHref = field.getAnnotation(ParentNextHref.class);
            String url = $parentNextHref(selector, parentNextHref.cssPath(), parentNextHref.value());
            return url;
        } else if (field.isAnnotationPresent(ParentPreviousHref.class)) {// @ParentPreviousHref
            ParentPreviousHref parentPreviousHref = field.getAnnotation(ParentPreviousHref.class);
            String url = $parentPreviousHref(selector, parentPreviousHref.cssPath(), parentPreviousHref.value());
            return url;
        } else if (field.isAnnotationPresent(BrotherNextHref.class)) {// @BrotherNextHref
            BrotherNextHref brotherNextHref = field.getAnnotation(BrotherNextHref.class);
            String url = $brotherNextHref(selector, brotherNextHref.cssPath(), brotherNextHref.value());
            return url;
        } else if (field.isAnnotationPresent(BrotherPreviousHref.class)) {// @BrotherPreviousHref
            BrotherPreviousHref brotherPreviousHref = field.getAnnotation(BrotherPreviousHref.class);
            String url = $brotherPreviousHref(selector, brotherPreviousHref.cssPath(), brotherPreviousHref.value());
            return url;
        } else if (field.isAnnotationPresent(Attr.class)) {// @Attr
            Attr attr = field.getAnnotation(Attr.class);
            String name = attr.value();
            return Conversion.getValue(field.getType(), $attr(selector, name));
        } else if (field.isAnnotationPresent(ParentAttr.class)) {// @ParentAttr
            ParentAttr parentAttr = field.getAnnotation(ParentAttr.class);
            return Conversion.getValue(field.getType(), $parentAttr(selector, parentAttr.value(), parentAttr.cssPath()));
        } else if (field.isAnnotationPresent(ParentNextAttr.class)) {// @ParentNextAttr
            ParentNextAttr parentNextAttr = field.getAnnotation(ParentNextAttr.class);
            return Conversion.getValue(field.getType(), $parentNextAttr(selector, parentNextAttr.value(), parentNextAttr.cssPath()));
        } else if (field.isAnnotationPresent(ParentPreviousAttr.class)) {// @ParentPreviousAttr
            ParentPreviousAttr parentPreviousAttr = field.getAnnotation(ParentPreviousAttr.class);
            return Conversion.getValue(field.getType(), $parentPreviousAttr(selector, parentPreviousAttr.value(), parentPreviousAttr.cssPath()));
        } else if (field.isAnnotationPresent(BrotherNextAttr.class)) {// @BrotherNextAttr
            BrotherNextAttr brotherNextAttr = field.getAnnotation(BrotherNextAttr.class);
            return Conversion.getValue(field.getType(), $brotherNextAttr(selector, brotherNextAttr.value(), brotherNextAttr.cssPath()));
        } else if (field.isAnnotationPresent(BrotherPreviousAttr.class)) {// @BrotherPreviousAttr
            BrotherPreviousAttr brotherPreviousAttr = field.getAnnotation(BrotherPreviousAttr.class);
            return Conversion.getValue(field.getType(), $brotherPreviousAttr(selector, brotherPreviousAttr.value(), brotherPreviousAttr.cssPath()));
        } else if (field.isAnnotationPresent(Html.class)) {// @Html
            Html html = field.getAnnotation(Html.class);
            return $html(selector, html.outer());
        } else if (field.isAnnotationPresent(ParentHtml.class)) {// @ParentHtml
            ParentHtml parentHtml = field.getAnnotation(ParentHtml.class);
            return $parentHtml(selector, parentHtml.outer(), parentHtml.cssPath());
        } else if (field.isAnnotationPresent(ParentNextHtml.class)) {// @ParentNextHtml
            ParentNextHtml parentNextHtml = field.getAnnotation(ParentNextHtml.class);
            return $parentNextHtml(selector, parentNextHtml.outer(), parentNextHtml.cssPath());
        } else if (field.isAnnotationPresent(ParentPrevHtml.class)) {// @ParentPrevHtml
            ParentPrevHtml parentPrevHtml = field.getAnnotation(ParentPrevHtml.class);
            return $parentPrevHtml(selector, parentPrevHtml.outer(), parentPrevHtml.cssPath());
        } else if (field.isAnnotationPresent(BrotherPrevHtml.class)) {// @BrotherPrevHtml
            BrotherPrevHtml brotherPrevHtml = field.getAnnotation(BrotherPrevHtml.class);
            return $brotherPrevHtml(selector, brotherPrevHtml.outer(), brotherPrevHtml.cssPath());
        } else if (field.isAnnotationPresent(BrotherNextHtml.class)) {// @BrotherNextHtml
            BrotherNextHtml brotherNextHtml = field.getAnnotation(BrotherNextHtml.class);
            return $brotherNextHtml(selector, brotherNextHtml.outer(), brotherNextHtml.cssPath());
        } else {// @Html
            return $html(selector);
        }
    }


    public List<Object> $basicList(String selector, Field field) throws Exception {
        List<Object> list = new ArrayList<Object>();
        Elements els = $(selector);
        for (Element el : els) {
            if (field.isAnnotationPresent(Text.class)) {// @Text
                Text text = field.getAnnotation(Text.class);
                list.add(Conversion.getValue(field.getType(), $text(el, text.own())));
            } else if (field.isAnnotationPresent(ParentNextText.class)) {// @ParentNextText
                ParentNextText parentNextText = field.getAnnotation(ParentNextText.class);
                list.add(Conversion.getValue(field.getType(), $parentNextText(el, parentNextText.own(), parentNextText.cssPath())));
            } else if (field.isAnnotationPresent(ParentPreviousText.class)) {// @ParentPreviousText
                ParentPreviousText parentPreviousText = field.getAnnotation(ParentPreviousText.class);
                list.add(Conversion.getValue(field.getType(), $parentPreviousText(el, parentPreviousText.own(), parentPreviousText.cssPath())));
            } else if (field.isAnnotationPresent(ParentText.class)) {// @ParentText
                ParentText parentText = field.getAnnotation(ParentText.class);
                list.add(Conversion.getValue(field.getType(), $parentText(el, parentText.own(), parentText.cssPath())));
            } else if (field.isAnnotationPresent(BrotherNextText.class)) {// @BrotherNextText
                BrotherNextText brotherNextText = field.getAnnotation(BrotherNextText.class);
                list.add(Conversion.getValue(field.getType(), $brotherNextText(el, brotherNextText.own(), brotherNextText.cssPath())));
            } else if (field.isAnnotationPresent(BrotherPreviousText.class)) {// @BrotherPreviousText
                BrotherPreviousText brotherPreviousText = field.getAnnotation(BrotherPreviousText.class);
                list.add(Conversion.getValue(field.getType(), $brotherPreviousText(el, brotherPreviousText.own(), brotherPreviousText.cssPath())));
            } else if (field.isAnnotationPresent(Image.class)) {// @Image
                Image image = field.getAnnotation(Image.class);
                String imageSrc = $image(el, image.value());
                //保存下载图片
				//String localPath = DownloadImage.download(image.download(), imageSrc);
				//if (StringUtils.isNotEmpty(localPath)) {
				//	list.add(localPath);
				//}
                list.add(imageSrc);
            }else if (field.isAnnotationPresent(ParentImage.class)) {// @ParentImage
                ParentImage parentImage = field.getAnnotation(ParentImage.class);
                String imageSrc = $parentImage(selector, parentImage.cssPath(), parentImage.value());
                //String localPath = DownloadImage.download(parentImage.download(), imageSrc);
                //if (StringUtils.isNotEmpty(localPath)) {
                //    list.add(localPath);
                //}
                list.add(imageSrc);
            } else if (field.isAnnotationPresent(ParentNextImage.class)) {// @ParentNextImage
                ParentNextImage parentNextImage = field.getAnnotation(ParentNextImage.class);
                String imageSrc = $parentNextImage(selector, parentNextImage.cssPath(), parentNextImage.value());
                //String localPath = DownloadImage.download(parentNextImage.download(), imageSrc);
                //if (StringUtils.isNotEmpty(localPath)) {
                //    list.add(localPath);
                //}
                list.add(imageSrc);
            } else if (field.isAnnotationPresent(ParentPreviousImage.class)) {// @ParentPreviousImage
                ParentPreviousImage parentPreviousImage = field.getAnnotation(ParentPreviousImage.class);
                String imageSrc = $parentPreviousImage(selector, parentPreviousImage.cssPath(), parentPreviousImage.value());
                //String localPath = DownloadImage.download(parentPreviousImage.download(), imageSrc);
                //if (StringUtils.isNotEmpty(localPath)) {
                //    list.add(localPath);
                //}
                list.add(imageSrc);
            } else if (field.isAnnotationPresent(BrotherNextImage.class)) {// @BrotherNextImage
                BrotherNextImage brotherNextImage = field.getAnnotation(BrotherNextImage.class);
                String imageSrc = $brotherNextImage(selector, brotherNextImage.cssPath(), brotherNextImage.value());
                //String localPath = DownloadImage.download(brotherNextImage.download(), imageSrc);
                //if (StringUtils.isNotEmpty(localPath)) {
                //    list.add(localPath);
                //}
                list.add(imageSrc);
            } else if (field.isAnnotationPresent(BrotherPreviousImage.class)) {// @BrotherPreviousImage
                BrotherPreviousImage brotherPreviousImage = field.getAnnotation(BrotherPreviousImage.class);
                String imageSrc = $brotherPreviousImage(selector, brotherPreviousImage.cssPath(), brotherPreviousImage.value());
                //String localPath = DownloadImage.download(brotherPreviousImage.download(), imageSrc);
                //if (StringUtils.isNotEmpty(localPath)) {
                //    list.add(localPath);
                //}
                list.add(imageSrc);
            }  else if (field.isAnnotationPresent(Href.class)) {// @Href
                Href href = field.getAnnotation(Href.class);
                String url = $href(el, href.value());
                list.add(url);
            } else if (field.isAnnotationPresent(ParentHref.class)) {// @ParentHref
                ParentHref parentHref = field.getAnnotation(ParentHref.class);
                String url = $parentHref(el, parentHref.cssPath(), parentHref.value());
                list.add(url);
            } else if (field.isAnnotationPresent(ParentNextHref.class)) {// @ParentNextHref
                ParentNextHref parentNextHref = field.getAnnotation(ParentNextHref.class);
                String url = $parentNextHref(el, parentNextHref.cssPath(), parentNextHref.value());
                list.add(url);
            } else if (field.isAnnotationPresent(ParentPreviousHref.class)) {// @ParentPreviousHref
                ParentPreviousHref parentPreviousHref = field.getAnnotation(ParentPreviousHref.class);
                String url = $parentPreviousHref(el, parentPreviousHref.cssPath(), parentPreviousHref.value());
                list.add(url);
            } else if (field.isAnnotationPresent(BrotherNextHref.class)) {// @BrotherNextHref
                BrotherNextHref brotherNextHref = field.getAnnotation(BrotherNextHref.class);
                String url = $brotherNextHref(el, brotherNextHref.cssPath(), brotherNextHref.value());
                list.add(url);
            } else if (field.isAnnotationPresent(BrotherPreviousHref.class)) {// @BrotherPreviousHref
                BrotherPreviousHref brotherPreviousHref = field.getAnnotation(BrotherPreviousHref.class);
                String url = $brotherPreviousHref(el, brotherPreviousHref.cssPath(), brotherPreviousHref.value());
                list.add(url);
            } else if (field.isAnnotationPresent(Attr.class)) {// @Attr
                Attr attr = field.getAnnotation(Attr.class);
                String name = attr.value();
                list.add(Conversion.getValue(field.getType(), $attr(el, name)));
            } else if (field.isAnnotationPresent(ParentAttr.class)) {// @ParentAttr
                ParentAttr parentAttr = field.getAnnotation(ParentAttr.class);
                list.add(Conversion.getValue(field.getType(), $parentAttr(el, parentAttr.value(), parentAttr.cssPath())));
            } else if (field.isAnnotationPresent(ParentNextAttr.class)) {// @ParentNextAttr
                ParentNextAttr parentNextAttr = field.getAnnotation(ParentNextAttr.class);
                list.add(Conversion.getValue(field.getType(), $parentNextAttr(el, parentNextAttr.value(), parentNextAttr.cssPath())));
            } else if (field.isAnnotationPresent(ParentPreviousAttr.class)) {// @ParentPreviousAttr
                ParentPreviousAttr parentPreviousAttr = field.getAnnotation(ParentPreviousAttr.class);
                list.add(Conversion.getValue(field.getType(), $parentPreviousAttr(el, parentPreviousAttr.value(), parentPreviousAttr.cssPath())));
            } else if (field.isAnnotationPresent(BrotherPreviousAttr.class)) {// @BrotherPreviousAttr
                BrotherPreviousAttr brotherPreviousAttr = field.getAnnotation(BrotherPreviousAttr.class);
                list.add(Conversion.getValue(field.getType(), $brotherPreviousAttr(el, brotherPreviousAttr.value(), brotherPreviousAttr.cssPath())));
            } else if (field.isAnnotationPresent(BrotherNextAttr.class)) {// @BrotherNextAttr
                BrotherNextAttr brotherNextAttr = field.getAnnotation(BrotherNextAttr.class);
                list.add(Conversion.getValue(field.getType(), $brotherNextAttr(el, brotherNextAttr.value(), brotherNextAttr.cssPath())));
            } else if (field.isAnnotationPresent(Html.class)) {// @Html
                Html html = field.getAnnotation(Html.class);
                list.add($html(el, html.outer()));
            } else if (field.isAnnotationPresent(ParentHtml.class)) {// @ParentHtml
                ParentHtml parentHtml = field.getAnnotation(ParentHtml.class);
                list.add($parentHtml(el, parentHtml.outer(), parentHtml.cssPath()));
            } else if (field.isAnnotationPresent(ParentNextHtml.class)) {// @ParentNextHtml
                ParentNextHtml parentNextHtml = field.getAnnotation(ParentNextHtml.class);
                list.add($parentNextHtml(el, parentNextHtml.outer(), parentNextHtml.cssPath()));
            } else if (field.isAnnotationPresent(ParentPrevHtml.class)) {// @ParentPrevHtml
                ParentPrevHtml parentPrevHtml = field.getAnnotation(ParentPrevHtml.class);
                list.add($parentPrevHtml(el, parentPrevHtml.outer(), parentPrevHtml.cssPath()));
            } else if (field.isAnnotationPresent(BrotherPrevHtml.class)) {// @BrotherPrevHtml
                BrotherPrevHtml brotherPrevHtml = field.getAnnotation(BrotherPrevHtml.class);
                list.add($brotherPrevHtml(el, brotherPrevHtml.outer(), brotherPrevHtml.cssPath()));
            } else if (field.isAnnotationPresent(BrotherNextHtml.class)) {// @BrotherNextHtml
                BrotherNextHtml brotherNextHtml = field.getAnnotation(BrotherNextHtml.class);
                list.add($brotherNextHtml(el, brotherNextHtml.outer(), brotherNextHtml.cssPath()));
            } else {// Other
                list.add(el.html());
            }
        }
        return list;
    }

    public SpiderBean $bean(String selector, HttpRequest request, Class<? extends SpiderBean> clazz) {
        String subHtml = $html(selector);
        // table
        HttpResponse subResponse = HttpResponse.createSimple(subHtml);
        Render render = RenderContext.getRender(RenderType.HTML);
        return render.inject(clazz, request, subResponse);
    }

    public List<SpiderBean> $beanList(String selector, HttpRequest request, Class<? extends SpiderBean> clazz) {
        List<SpiderBean> list = new ArrayList<SpiderBean>();
        List<String> els = $list(selector);
        for (String el : els) {
            // table
            HttpResponse subResponse = HttpResponse.createSimple(el);
            Render render = RenderContext.getRender(RenderType.HTML);
            SpiderBean subBean = render.inject(clazz, request, subResponse);
            list.add(subBean);
        }
        return list;
    }

    public Elements $(String selector) {
        Elements elements = document.select(selector);
        if (SpiderThreadLocal.get().getEngine().isDebug()) {
            if (!"script".equalsIgnoreCase(selector)) {
                // log.debug("["+selector+"]--->["+elements+"]");
                System.out.println("[" + selector + "]--->[" + elements + "]");
            }
        }
        return elements;
    }

    public Element $element(String selector) {
        Elements elements = $(selector);
        if (elements != null && elements.size() > 0) {
            return elements.first();
        }
        return null;
    }

    public List<String> $list(String selector) {
        List<String> list = new ArrayList<String>();
        Elements elements = $(selector);
        if (elements != null) {
            for (Element ele : elements) {
                list.add(ele.outerHtml());
            }
        }
        return list;
    }

    public String $html(String selector) {
        return $html(selector, false);
    }

    public String $html(String selector, boolean isOuter) {
        Elements elements = $(selector);
        if (elements != null) {
            if (isOuter) {
                return elements.outerHtml();
            }
            return elements.html();
        }
        return null;
    }

    public String $html(Element element, boolean isOuter) {
        if (element != null) {
            if (isOuter) {
                return element.outerHtml();
            }
            return element.html();
        }
        return null;
    }

    public String $parentHtml(Element element, boolean isOuter, String cssPath) {
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.parent();
            } else {
                element = element.parent().selectFirst(cssPath);
            }
            if (isOuter) {
                return element.outerHtml();
            }
            return element.html();
        }
        return null;
    }

    public String $parentNextHtml(Element element, boolean isOuter, String cssPath) {
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.parent().nextElementSibling();
            } else {
                element = element.parent().nextElementSibling().selectFirst(cssPath);
            }
            if (isOuter) {
                return element.outerHtml();
            }
            return element.html();
        }
        return null;
    }

    public String $parentPrevHtml(Element element, boolean isOuter, String cssPath) {
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.parent().previousElementSibling();
            } else {
                element = element.parent().previousElementSibling().selectFirst(cssPath);
            }
            if (isOuter) {
                return element.outerHtml();
            }
            return element.html();
        }
        return null;
    }

    public String $brotherPrevHtml(Element element, boolean isOuter, String cssPath) {
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.previousElementSibling();
            } else {
                element = element.previousElementSibling().selectFirst(cssPath);
            }
            if (isOuter) {
                return element.outerHtml();
            }
            return element.html();
        }
        return null;
    }

    public String $brotherNextHtml(Element element, boolean isOuter, String cssPath) {
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.nextElementSibling();
            } else {
                element = element.nextElementSibling().selectFirst(cssPath);
            }
            if (isOuter) {
                return element.outerHtml();
            }
            return element.html();
        }
        return null;
    }

    public String $parentHtml(String selector, boolean isOuter, String cssPath) {
        Elements elements = $(selector);
        if (elements != null) {
            if ("".equals(cssPath)) {
                elements = elements.parents();
            } else {
                elements = elements.parents().select(cssPath);
            }
            if (isOuter) {
                return elements.outerHtml();
            }
            return elements.html();
        }
        return null;
    }


    public String $parentNextHtml(String selector, boolean isOuter, String cssPath) {
        Elements elements = $(selector);
        if (elements != null) {
            if ("".equals(cssPath)) {
                elements = elements.parents().next();
            } else {
                elements = elements.parents().next().select(cssPath);
            }
            if (isOuter) {
                return elements.outerHtml();
            }
            return elements.html();
        }
        return null;
    }

    public String $parentPrevHtml(String selector, boolean isOuter, String cssPath) {
        Elements elements = $(selector);
        if (elements != null) {
            if ("".equals(cssPath)) {
                elements = elements.parents().prev();
            } else {
                elements = elements.parents().prev().select(cssPath);
            }
            if (isOuter) {
                return elements.outerHtml();
            }
            return elements.html();
        }
        return null;
    }

    public String $brotherNextHtml(String selector, boolean isOuter, String cssPath) {
        Elements elements = $(selector);
        if (elements != null) {
            if ("".equals(cssPath)) {
                elements = elements.next();
            } else {
                elements = elements.next().select(cssPath);
            }
            if (isOuter) {
                return elements.outerHtml();
            }
            return elements.html();
        }
        return null;
    }

    public String $brotherPrevHtml(String selector, boolean isOuter, String cssPath) {
        Elements elements = $(selector);
        if (elements != null) {
            if ("".equals(cssPath)) {
                elements = elements.prev();
            } else {
                elements = elements.prev().select(cssPath);
            }
            if (isOuter) {
                return elements.outerHtml();
            }
            return elements.html();
        }
        return null;
    }

    public String $text(Element element, boolean own) {
        if (element == null) {
            return null;
        }
        String text = "";
        if (own) {
            text = element.ownText();
        } else {
            text = element.text();
        }
        // 替换掉空格信息
        return StringUtils.replace(text, "\u00A0", "");
    }

    public String $text(String selector, boolean own) {
        Element element = $element(selector);
        if (element != null) {
            return $text(element, own);
        }
        return null;
    }

    public String $parentNextText(Element element, boolean own, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent().nextElementSibling();
            } else {
                element = element.parent().nextElementSibling().selectFirst(cssPath);
            }
        }

        if (element == null) {
            return null;
        }
        String text = "";
        if (own) {
            text = element.ownText();
        } else {
            text = element.text();
        }
        // 替换掉空格信息
        return StringUtils.replace(text, "\u00A0", "");
    }

    public String $parentNextText(String selector, boolean own, String cssPath) {
        Element element = $element(selector);
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.parent().nextElementSibling();
            } else {
                element = element.parent().nextElementSibling().selectFirst(cssPath);
            }
            return $text(element, own);
        }
        return null;
    }

    public String $parentPreviousText(Element element, boolean own, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent().previousElementSibling();
            } else {
                element = element.parent().previousElementSibling().selectFirst(cssPath);
            }
        }

        if (element == null) {
            return null;
        }
        String text = "";
        if (own) {
            text = element.ownText();
        } else {
            text = element.text();
        }
        // 替换掉空格信息
        return StringUtils.replace(text, "\u00A0", "");
    }

    public String $parentPreviousText(String selector, boolean own, String cssPath) {
        Element element = $element(selector);
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.parent().previousElementSibling();
            } else {
                element = element.parent().previousElementSibling().selectFirst(cssPath);
            }
            return $text(element, own);
        }
        return null;
    }

    public String $parentText(Element element, boolean own, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent();
            } else {
                element = element.parent().selectFirst(cssPath);
            }
        }

        if (element == null) {
            return null;
        }
        String text = "";
        if (own) {
            text = element.ownText();
        } else {
            text = element.text();
        }
        // 替换掉空格信息
        return StringUtils.replace(text, "\u00A0", "");
    }

    public String $parentText(String selector, boolean own, String cssPath) {
        Element element = $element(selector);
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.parent();
            } else {
                element = element.parent().selectFirst(cssPath);
            }
            return $text(element, own);
        }
        return null;
    }

    public String $brotherPreviousText(Element element, boolean own, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.previousElementSibling();
            } else {
                element = element.previousElementSibling().selectFirst(cssPath);
            }
        }

        if (element == null) {
            return null;
        }
        String text = "";
        if (own) {
            text = element.ownText();
        } else {
            text = element.text();
        }
        // 替换掉空格信息
        return StringUtils.replace(text, "\u00A0", "");
    }

    public String $brotherPreviousText(String selector, boolean own, String cssPath) {
        Element element = $element(selector);
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.previousElementSibling();
            } else {
                element = element.previousElementSibling().selectFirst(cssPath);
            }
            return $text(element, own);
        }
        return null;
    }


    public String $brotherNextText(Element element, boolean own, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.nextElementSibling();
            } else {
                element = element.nextElementSibling().selectFirst(cssPath);
            }
        }

        if (element == null) {
            return null;
        }
        String text = "";
        if (own) {
            text = element.ownText();
        } else {
            text = element.text();
        }
        // 替换掉空格信息
        return StringUtils.replace(text, "\u00A0", "");
    }

    public String $brotherNextText(String selector, boolean own, String cssPath) {
        Element element = $element(selector);
        if (element != null) {
            if ("".equals(cssPath)) {
                element = element.nextElementSibling();
            } else {
                element = element.nextElementSibling().selectFirst(cssPath);
            }
            return $text(element, own);
        }
        return null;
    }

    public String $attr(Element element, String attr) {
        if (element == null) {
            return null;
        }
        return element.attr(attr);
    }

    public String $attr(String selector, String attr) {
        Element element = $element(selector);
        if (element == null) {
            return null;
        }
        return element.attr(attr);
    }

    public String $parentAttr(Element element, String attr, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent();
            } else {
                element = element.parent().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $parentAttr(String selector, String attr, String cssPath) {
        Element element = $element(selector);
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent();
            } else {
                element = element.parent().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $parentNextAttr(Element element, String attr, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent().nextElementSibling();
            } else {
                element = element.parent().nextElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $parentNextAttr(String selector, String attr, String cssPath) {
        Element element = $element(selector);
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent().nextElementSibling();
            } else {
                element = element.parent().nextElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $parentPreviousAttr(Element element, String attr, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent().previousElementSibling();
            } else {
                element = element.parent().previousElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $parentPreviousAttr(String selector, String attr, String cssPath) {
        Element element = $element(selector);
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.parent().previousElementSibling();
            } else {
                element = element.parent().previousElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }


    public String $brotherPreviousAttr(Element element, String attr, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.previousElementSibling();
            } else {
                element = element.previousElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $brotherPreviousAttr(String selector, String attr, String cssPath) {
        Element element = $element(selector);
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.previousElementSibling();
            } else {
                element = element.previousElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $brotherNextAttr(Element element, String attr, String cssPath) {
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.nextElementSibling();
            } else {
                element = element.nextElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $brotherNextAttr(String selector, String attr, String cssPath) {
        Element element = $element(selector);
        if (element == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                element = element.nextElementSibling();
            } else {
                element = element.nextElementSibling().selectFirst(cssPath);
            }
        }
        return element.attr(attr);
    }

    public String $href(Element href, String attr) {
        if (href == null) {
            return null;
        }
        return href.absUrl(attr);
    }

    public String $href(Element href, String... attrs) {
        if (href == null) {
            return null;
        }
        for (String attr : attrs) {
            String value = $href(href, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $href(href, "href");
    }

    public String $href(String selector, String attr) {
        return $href($element(selector), attr);
    }

    public String $href(String selector, String... attrs) {
        return $href($element(selector), attrs);
    }


    public String $parentHref(Element href, String cssPath, String... attrs) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.parent();
            } else {
                href = href.parent().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $href(href, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $href(href, "href");
    }

    public String $parentHref(String selector, String cssPath, String... attrs) {
        return $parentHref($element(selector), cssPath, attrs);
    }

    public String $parentNextHref(Element href, String cssPath, String... attrs) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.parent().nextElementSibling();
            } else {
                href = href.parent().nextElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $href(href, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $href(href, "href");
    }

    public String $parentNextHref(String selector, String cssPath, String... attrs) {
        return $parentNextHref($element(selector), cssPath, attrs);
    }

    public String $parentPreviousHref(Element href, String cssPath, String... attrs) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.parent().previousElementSibling();
            } else {
                href = href.parent().previousElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $href(href, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $href(href, "href");
    }

    public String $parentPreviousHref(String selector, String cssPath, String... attrs) {
        return $parentPreviousHref($element(selector), cssPath, attrs);
    }

    public String $brotherPreviousHref(Element href, String cssPath, String... attrs) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.previousElementSibling();
            } else {
                href = href.previousElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $href(href, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $href(href, "href");
    }


    public String $brotherPreviousHref(String selector, String cssPath, String... attrs) {
        return $brotherPreviousHref($element(selector), cssPath, attrs);
    }

    public String $brotherNextHref(Element href, String cssPath, String... attrs) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.nextElementSibling();
            } else {
                href = href.nextElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $href(href, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $href(href, "href");
    }


    public String $brotherNextHref(String selector, String cssPath, String... attrs) {
        return $brotherNextHref($element(selector), cssPath, attrs);
    }

    public String $parentHref(Element href, String cssPath, String attr) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.parent();
            } else {
                href = href.parent().selectFirst(cssPath);
            }
        }
        return href.absUrl(attr);
    }

    public String $parentHref(String selector, String cssPath, String attr) {
        return $parentHref($element(selector), cssPath, attr);
    }

    public String $parentNextHref(Element href, String cssPath, String attr) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.parent().nextElementSibling();
            } else {
                href = href.parent().nextElementSibling().selectFirst(cssPath);
            }
        }
        return href.absUrl(attr);
    }

    public String $parentNextHref(String selector, String cssPath, String attr) {
        return $parentNextHref($element(selector), cssPath, attr);
    }

    public String $parentPreviousHref(Element href, String cssPath, String attr) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.parent().previousElementSibling();
            } else {
                href = href.parent().previousElementSibling().selectFirst(cssPath);
            }
        }
        return href.absUrl(attr);
    }

    public String $parentPreviousHref(String selector, String cssPath, String attr) {
        return $parentPreviousHref($element(selector), cssPath, attr);
    }

    public String $brotherNextHref(Element href, String cssPath, String attr) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.nextElementSibling();
            } else {
                href = href.nextElementSibling().selectFirst(cssPath);
            }
        }
        return href.absUrl(attr);
    }

    public String $brotherNextHref(String selector, String cssPath, String attr) {
        return $brotherNextHref($element(selector), cssPath, attr);
    }


    public String $brotherPreviousHref(Element href, String cssPath, String attr) {
        if (href == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                href = href.previousElementSibling();
            } else {
                href = href.previousElementSibling().selectFirst(cssPath);
            }
        }
        return href.absUrl(attr);
    }

    public String $brotherPreviousHref(String selector, String cssPath, String attr) {
        return $brotherPreviousHref($element(selector), cssPath, attr);
    }

    public String $image(Element img, String attr) {
        if (img == null) {
            return null;
        }
        return img.absUrl(attr);
    }

    public String $image(Element img, String... attrs) {
        if (img == null) {
            return null;
        }
        for (String attr : attrs) {
            String value = $image(img, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $image(img, "src");
    }

    public String $image(String selector, String attr) {
        return $image($element(selector), attr);
    }

    public String $image(String selector, String... attrs) {
        return $image($element(selector), attrs);
    }

    //===========
    public String $parentImage(Element img, String cssPath, String attr) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.parent();
            } else {
                img = img.parent().selectFirst(cssPath);
            }
        }
        return img.absUrl(attr);
    }

    public String $parentImage(Element img, String cssPath, String... attrs) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.parent();
            } else {
                img = img.parent().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $image(img, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $image(img, "src");
    }

    public String $parentImage(String selector, String cssPath, String attr) {
        return $parentImage($element(selector), cssPath, attr);
    }

    public String $parentImage(String selector, String cssPath, String... attrs) {
        return $parentImage($element(selector), cssPath, attrs);
    }
    //===========

    public String $parentNextImage(Element img, String cssPath, String attr) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.parent().nextElementSibling();
            } else {
                img = img.parent().nextElementSibling().selectFirst(cssPath);
            }
        }
        return img.absUrl(attr);
    }

    public String $parentNextImage(Element img, String cssPath, String... attrs) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.parent().nextElementSibling();
            } else {
                img = img.parent().nextElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $image(img, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $image(img, "src");
    }

    public String $parentNextImage(String selector, String cssPath, String attr) {
        return $parentNextImage($element(selector), cssPath, attr);
    }

    public String $parentNextImage(String selector, String cssPath, String... attrs) {
        return $parentNextImage($element(selector), cssPath, attrs);
    }

    //===========


    public String $parentPreviousImage(Element img, String cssPath, String attr) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.parent().previousElementSibling();
            } else {
                img = img.parent().previousElementSibling().selectFirst(cssPath);
            }
        }
        return img.absUrl(attr);
    }

    public String $parentPreviousImage(Element img, String cssPath, String... attrs) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.parent().previousElementSibling();
            } else {
                img = img.parent().previousElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $image(img, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $image(img, "src");
    }

    public String $parentPreviousImage(String selector, String cssPath, String attr) {
        return $parentPreviousImage($element(selector), cssPath, attr);
    }

    public String $parentPreviousImage(String selector, String cssPath, String... attrs) {
        return $parentPreviousImage($element(selector), cssPath, attrs);
    }

    //==========

    public String $brotherNextImage(Element img, String cssPath, String attr) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.nextElementSibling();
            } else {
                img = img.nextElementSibling().selectFirst(cssPath);
            }
        }
        return img.absUrl(attr);
    }

    public String $brotherNextImage(Element img, String cssPath, String... attrs) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.nextElementSibling();
            } else {
                img = img.nextElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $image(img, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $image(img, "src");
    }

    public String $brotherNextImage(String selector, String cssPath, String attr) {
        return $brotherNextImage($element(selector), cssPath, attr);
    }

    public String $brotherNextImage(String selector, String cssPath, String... attrs) {
        return $brotherNextImage($element(selector), cssPath, attrs);
    }

    //========

    public String $brotherPreviousImage(Element img, String cssPath, String attr) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.previousElementSibling();
            } else {
                img = img.previousElementSibling().selectFirst(cssPath);
            }
        }
        return img.absUrl(attr);
    }

    public String $brotherPreviousImage(Element img, String cssPath, String... attrs) {
        if (img == null) {
            return null;
        } else {
            if ("".equals(cssPath)) {
                img = img.previousElementSibling();
            } else {
                img = img.previousElementSibling().selectFirst(cssPath);
            }
        }
        for (String attr : attrs) {
            String value = $image(img, attr);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return $image(img, "src");
    }

    public String $brotherPreviousImage(String selector, String cssPath, String attr) {
        return $brotherPreviousImage($element(selector), cssPath, attr);
    }

    public String $brotherPreviousImage(String selector, String cssPath, String... attrs) {
        return $brotherPreviousImage($element(selector), cssPath, attrs);
    }


    //======

    public void setLogClass(Class<? extends SpiderBean> spiderBeanClass) {
        log = LogFactory.getLog(spiderBeanClass);
    }

    private boolean isTable(String content) {
        if (!StringUtils.contains(content, "</html>")) {
            String rege = "<\\s*(thead|tbody|tr|td|th)[\\s\\S]+";
            Pattern pattern = Pattern.compile(rege);
            Matcher matcher = pattern.matcher(content);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
}
