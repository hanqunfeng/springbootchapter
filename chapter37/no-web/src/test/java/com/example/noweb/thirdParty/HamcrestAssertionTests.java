package com.example.noweb.thirdParty;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * <p>Hamcrest第三方测试包</p>
 *
 * 使用第三方测试包，好处就是测试方法更多一些，逻辑更清晰
 * springboot test类库就是使用的AssertJ 和 Hamcrest，实际上AssertJ依赖于Hamcrest
 *
 * Created by hanqf on 2020/8/28 16:30.
 * <p>
 * is : 是否匹配单一规则，方法参数是规则时可以不加is
 * anyOf : 是否匹配任意规则
 * allOf : 是否匹配全部规则
 *
 * 参考：https://www.jianshu.com/p/e7d4c3bdac6e
 */


public class HamcrestAssertionTests {

    @Test
    void assertWithHamcrestMatcher() {
        //下面三种方法是等价的，可以用于简单类型的比较
        assertThat(2 + 1, is(equalTo(3)));
        assertThat(2 + 1, equalTo(3)); //不加is，直接使用规则
        assertThat(2 + 1, is(3)); //参数不是规则，需要使用is

        //不匹配
        assertThat(2 + 1, not(4));

        String str = "firstName";
        assertThat("测试失败，没有以特定字符串开头", str, is(startsWith("first")));
        assertThat("测试失败，没有以特定字符串结尾", str, is(endsWith("me")));
        assertThat(str, is(containsString("Na")));

        //类似OR的效果，匹配任意验证都测试通过
        assertThat(str, anyOf(startsWith("first"), containsString("Na"), endsWith("me")));

        //类似AND的效果，匹配全部验证则测试通过
        assertThat(str, allOf(startsWith("first"), containsString("Na"), endsWith("me")));

        //判断两个对象是否为同一个实体
        assertThat(str, sameInstance(str));

        String str1 = "text";
        String str2 = " text ";
        //去除两边空格后比较是否相当，中间的空格不能去掉的
        assertThat(str1, is(equalToCompressingWhiteSpace(str2)));
        assertThat("test", equalToIgnoringCase("Test")); //忽略大小写

        String strEmpty = "";
        assertThat(strEmpty, emptyString()); // 空字符串
        assertThat(strEmpty, emptyOrNullString()); // 空字符串或者null


        assertThat(1, greaterThan(0)); // 大于
        assertThat(5, greaterThanOrEqualTo(5)); //大于等于
        assertThat(-1, lessThan(0)); // 小于
        assertThat(-1, lessThanOrEqualTo(5)); // 小于等于


        //集合
        List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        assertThat(collection, hasSize(3));

        assertThat(collection, hasItem("cd"));
        assertThat(collection, not(hasItem("zz")));

        assertThat(collection, hasItems("cd", "ab")); // 检查多个元素是否在集合中，不区分顺序
        assertThat(collection, containsInAnyOrder("cd", "ab", "ef")); // 检查多个元素是否在集合中，不区分顺序

        List<String> collectionEmpty = Lists.newArrayList();
        assertThat(collectionEmpty, empty()); // 用于检查集合是否为空，注意这里集合对象不能是null


        String[] array = new String[]{};
        assertThat(array, emptyArray()); // 用于检查数组是否为空，注意这里数组对象不能是null

        Map<String, String> maps = new HashMap<>();
        assertThat(maps, equalTo(Collections.EMPTY_MAP));

        Iterable<String> iterable = Lists.newArrayList();
        assertThat(iterable, emptyIterable());

        Iterable<String> list = Lists.newArrayList("ab", "cd", "ef");
        assertThat(list, iterableWithSize(3));
        //检查每一项是否为空，这里对集合中的每一个元素执行验证方法
        assertThat(list, everyItem(notNullValue()));


        City city = new City("shenzhen", "CA");
        assertThat(city, hasProperty("state"));
        assertThat(city, hasProperty("state", equalTo("CA"))); // 判断是否存在某个属性，并且是否存在某个特性值

        City city1 = new City("San Francisco", "CA");
        City city2 = new City("San Francisco", "CA");
        assertThat(city1, samePropertyValuesAs(city2));
    }

    public class City {
        String name;
        String state;

        public City(String name, String state) {
            this.name = name;
            this.state = state;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }


}
