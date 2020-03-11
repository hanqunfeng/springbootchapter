package org.example.model;/**
 * Created by hanqf on 2020/3/2 15:13.
 */


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/2 15:13
 */
@Component
public class ELProperties {
    //${first.value}表示占位符，Spring会读取上下文的属性进行替换
    @Value("${first.value}")
    private String first;

    @Value("${second.value}")
    private String second;

    //#{}表示启用spring表达式，具有运算能力,这里T()代表引入类，java.lang下不必权限定名称，后面是其静态方法
    @Value("#{T(System).currentTimeMillis()}")
    private Long initTime;

    //注入其它对象的属性值，这里configProperties是在spring上下文中注册的bean的名称
    @Value("#{configProperties.url}")
    private String url;

    //加上一个？表示只有不为空时才会执行后面的方法
    @Value("#{configProperties.username?.toUpperCase()}")
    private String username;


    @Override
    public String toString() {
        return "ELProperties{" +
                "first='" + first + '\'' +
                ", second='" + second + '\'' +
                ", initTime=" + initTime +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

}
