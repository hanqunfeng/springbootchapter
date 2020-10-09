package com.cas.controller;

import com.cas.utils.AesUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <p>验证码</p>
 * Created by hanqf on 2020/9/11 11:54.
 */
@Api(tags = {"验证码控制器"})
//@Tag(name = "验证码控制器")
@Controller
public class CaptchaController extends BaseController {

    public static final int WIDTH = 120;//生成图片的宽度
    public static final int HEIGHT = 55;//生成图片的高度
    public static final int WORDS_NUMBER = 4;//验证码中字符的个数
    public static final String CHECK_CODE_NAME = "CHECK_CODE";//验证码session中的名称
    //CaptchaConfiguration中注入进来的
    @Autowired
    private CasConfigurationProperties casConfigurationProperties;

    //@ApiOperation(value = "生成验证码",notes = "4位长度，字母或数字")
    @Operation(summary = "生成验证码",description = "4位长度，字母或数字")
    @RequestMapping("/checkcode/bimage")
    public void creatImageCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TODO Auto-generated method stub
        String createTypeFlag = req.getParameter("createTypeFlag");//接收客户端传递的createTypeFlag标识
        //在内存中创建一张图片
        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        //得到图片
        Graphics g = bi.getGraphics();
        //设置图片的背景色
        setBackGround(g);
        //设置图片的边框
        setBorder(g);
        //在图片上画干扰线
        drawRandomLine(g);
        //在图片上放上随机字符
        String randomString = this.drawRandomNum(g, createTypeFlag);

        //将随机数存在session中
        //req.getSession(true).setAttribute(CHECK_CODE_NAME, randomString);
        WebUtils.setSessionAttribute(req, CHECK_CODE_NAME, randomString);

        //http访问时获取session的值为null，所以这里使用cookie再存储一份，取的时候，先判断session是否存在，不存在就读取cookie，cookie的值最好加密处理
        Cookie cookie = new Cookie(CHECK_CODE_NAME, AesUtil.encrypt(randomString));
        //当maxAge属性为负数，则表示该Cookie只是一个临时Cookie，不会被持久化，仅在本浏览器窗口或者本窗口打开的子窗口中有效，关闭浏览器后该Cookie立即失效。
        cookie.setMaxAge(-1);
        resp.addCookie(cookie);

        //设置响应头通知浏览器以图片的形式打开
        resp.setContentType("image/jpeg");

        //设置响应头控制浏览器不要缓存
        resp.setDateHeader("expries", -1);
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");

        //将图片传给浏览器
        ImageIO.write(bi, "jpg", resp.getOutputStream());
    }

    //@ApiOperation(value = "比对验证码",notes = "判断验证码是否有效")
    @Operation(summary = "比对验证码",description = "判断验证码是否有效")
    @RequestMapping("/checkcode/check")
    @ResponseBody
    public Map<String, Object> checkCode(@Nullable String code, HttpServletRequest req) {
        //返回值
        Map<String, Object> map = new HashMap<String, Object>();

        if (!StringUtils.hasText(code)) {
            map.put("error", true);
            map.put("msg", getMessage("login.checkcode.check.null"));
        } else {
            //String checkcode = (String) req.getSession(true).getAttribute(CHECK_CODE_NAME);
            String checkcode = (String) WebUtils.getSessionAttribute(req, CHECK_CODE_NAME);
            if (checkcode == null) {
                Cookie cookie = WebUtils.getCookie(req, CHECK_CODE_NAME);
                if (cookie != null) {
                    checkcode = AesUtil.decrypt(cookie.getValue());
                }
            }
            if (code.trim().equalsIgnoreCase(checkcode)) {
                map.put("error", false);
                map.put("msg", getMessage("login.checkcode.check.success"));
            } else {
                map.put("error", true);
                map.put("msg", getMessage("login.checkcode.check.fail") + checkcode);
            }
        }
        return map;
    }


    //设置图片背景色
    //@param g
    private void setBackGround(Graphics g) {
        //设置颜色
        g.setColor(Color.WHITE);
        //填充区域
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    /*
     * 设置图片的边框
     * @param g
     * */

    private void setBorder(Graphics g) {
        //设置边框颜色
        g.setColor(Color.BLUE);
        //边框区域
        g.drawRect(1, 1, WIDTH - 2, HEIGHT - 2);
    }

    /*
     * 在图片上画随机线条
     * @param g
     * */
    private void drawRandomLine(Graphics g) {
        //设置颜色
        g.setColor(Color.GREEN);
        //设置线条个数并画线
        for (int i = 0; i < 3; i++) {
            int x1 = new Random().nextInt(WIDTH);
            int y1 = new Random().nextInt(HEIGHT);
            int x2 = new Random().nextInt(WIDTH);
            int y2 = new Random().nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /*
     * 在图片上画随机字符
     * @param g
     * @param createTypeFlag
     * @return String
     * */
    private String drawRandomNum(Graphics g, String createTypeFlag) {
        //设置颜色
        g.setColor(Color.RED);
        g.setFont(new Font("宋体", Font.BOLD, 30));

        //数字字母的组合，为了避免数字0和字母O混淆，所以去掉了
        String baseNumLetter = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        String baseNum = "123456789";
        String baseLetter = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        if (createTypeFlag != null && createTypeFlag.length() > 0) {
            if ("nl".equals(createTypeFlag)) {
                //截取数字和字母的组合
                return createRandomChar((Graphics2D) g, baseNumLetter);
            } else if ("n".equals(createTypeFlag)) {
                //截取数字的组合
                return createRandomChar((Graphics2D) g, baseNum);
            } else if ("l".equals(createTypeFlag)) {
                //截取字母的组合
                return createRandomChar((Graphics2D) g, baseLetter);
            }
        } else {
            //截取数字和字母的组合
            return createRandomChar((Graphics2D) g, baseNumLetter);
        }
        return "";
    }

    /*
     * 创建随机字符
     * @param g
     * @param baseChar
     * @return String
     * */
    private String createRandomChar(Graphics2D g, String baseChar) {
        StringBuffer b = new StringBuffer();
        int x = 5;
        String ch = "";
        for (int i = 0; i < WORDS_NUMBER; i++) {
            //设置字体的旋转角度
            int degree = new Random().nextInt() % 30;
            ch = baseChar.charAt(new Random().nextInt(baseChar.length())) + "";
            b.append(ch);

            //正向角度
            g.rotate(degree * Math.PI / 180, x, 40);
            g.drawString(ch, x, 40);
            //反向角度
            g.rotate(-degree * Math.PI / 180, x, 40);
            x += 30;
        }
        return b.toString();
    }
}
