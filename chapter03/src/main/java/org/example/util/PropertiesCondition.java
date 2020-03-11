package org.example.util;/**
 * Created by hanqf on 2020/3/2 16:16.
 */


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author hanqf
 * @date 2020/3/2 16:16
 */
public class PropertiesCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment env = conditionContext.getEnvironment();
        System.out.println("PropertiesCondition 判断配置文件中是否含有对应的属性名称");
        return env.containsProperty("first.value")
                && env.containsProperty("second.value");
    }
}
