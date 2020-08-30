package com.example.noweb.thirdParty;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>AssertJ 测试用例</p>
 * Created by hanqf on 2020/8/28 17:55.
 * <p>
 * 使用第三方测试包，好处就是测试方法更多一些，逻辑更清晰
 * springboot test类库同时提供AssertJ 和 Hamcrest的支持，实际上AssertJ依赖于Hamcrest
 * <p>
 * AssertJ 和 Hamcrest 使用上的区别就是AssertJ会根据参数类型来给出响应的断言方法，使用上更加直观，方便
 */


class AssertJAssertionTests {

    @Test
    public void test() {
        assertThat("zhangsan").startsWith("zhang");
        assertThat("zhangsan").contains("zhang");

        assertThat(50).isEqualTo(50);
        assertThat(100).isGreaterThan(50);
        assertThat(50).isLessThanOrEqualTo(50);

        List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection).hasSize(3);
        assertThat(collection).isInstanceOf(List.class);
        assertThat(collection).containsExactly("ab", "cd", "ef"); //完全匹配，顺序和内容都必须一样
        assertThat(collection).contains("ef","ab"); //包含，顺序随意

        String strEmpty = "";
        assertThat(strEmpty).isEmpty();

        String strNull = null;
        assertThat(strNull).isNull();

    }

}
