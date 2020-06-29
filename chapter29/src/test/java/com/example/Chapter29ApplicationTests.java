package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class Chapter29ApplicationTests {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void sendMailSimpleText() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("email测试");
        message.setText("邮件测试内容");
        message.setTo("hanqf2008@163.com");
        message.setFrom("no-reply@changxiangzhihui.com");
        mailSender.send(message);
    }

    @Test
    void sendMailHtml() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("no-reply@changxiangzhihui.com");
        messageHelper.setTo("hanqf2008@163.com");
        messageHelper.setSubject("email测试");
        // true 为 HTML 邮件
        messageHelper.setText("<h1>测试内容</h1>", true);
        mailSender.send(message);
    }

    @Test
    void sendMailAttachment() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("no-reply@changxiangzhihui.com");
        messageHelper.setTo("hanqf2008@163.com");
        messageHelper.setSubject("email测试");
        // true 为 HTML 邮件
        messageHelper.setText("测试内容", true);
        FileSystemResource fileSystemResource = new FileSystemResource(new File("/Users/hanqf/Desktop/me.png"));
        //可以增加多个附件
        messageHelper.addAttachment(fileSystemResource.getFilename(),fileSystemResource);
        messageHelper.addAttachment(fileSystemResource.getFilename(),fileSystemResource);
        mailSender.send(message);
    }


    @Test
    void sendMailImage() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("no-reply@changxiangzhihui.com");
        messageHelper.setTo("hanqf2008@163.com");
        messageHelper.setSubject("email测试");
        // true 为 HTML 邮件
        //这里注意图片的名称需要时cid:name
        messageHelper.setText("<h2>Hi~</h2><p>第一封 Springboot HTML 图片邮件</p><br/><img src='cid:img01' /><br/><img src='cid:img02' />", true);
        FileSystemResource fileSystemResource = new FileSystemResource(new File("/Users/hanqf/Desktop/me.png"));
        //可以增加多个附件
        //这里第一个参数就是cid对应的name
        messageHelper.addInline("img01",fileSystemResource);
        messageHelper.addInline("img02",fileSystemResource);

        mailSender.send(message);
    }

    @Test
    void sendMailTemplate() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("no-reply@changxiangzhihui.com");
        messageHelper.setTo("hanqf2008@163.com");
        messageHelper.setSubject("email测试");


        //传递给模板的参数
        Map<String, Object> paramMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("张三");
        list.add("lisi");
        paramMap.put("list",list);
        paramMap.put("num",100.3456);
        Context context = new Context();
        // 设置变量的值
        context.setVariables(paramMap);
        //模板名称
        String template = "test";
        String emailContent = templateEngine.process(template, context);
        // true 为 HTML 邮件
        messageHelper.setText(emailContent, true);
        mailSender.send(message);
    }

}
