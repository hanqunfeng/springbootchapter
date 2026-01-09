package com.example.demo.redissug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 补全建议工具类
 * Created by hanqf on 2026/1/7 10:53.
 */

@Component
public class RedisSuggestTool {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings("unchecked")
    private <T> T executeLua(String script, List<String> keys, Object... args) {
        return (T) stringRedisTemplate.execute(
                new DefaultRedisScript<>(script, Object.class),
                keys,
                Arrays.stream(args)
                        .map(String::valueOf)
                        .toArray(String[]::new)
        );
    }

    /**
     * 添加 / 更新补全词
     * <p>
     * FT.SUGADD key string score
     * [INCR]
     * [PAYLOAD payload]
     *
     * @param key     索引名称
     * @param value   补全文本
     * @param score   分数
     * @param incr    是否递增，默认为false
     * @param payload 补全词的附加信息，默认为空
     */
    public Long sugAdd(String key,
                       String value,
                       double score,
                       boolean incr,
                       String payload) {
        String lua = """
                local args = {'FT.SUGADD', KEYS[1], ARGV[1], ARGV[2]}
                if ARGV[3] == '1' then table.insert(args, 'INCR') end
                if ARGV[4] ~= '' then table.insert(args, 'PAYLOAD'); table.insert(args, ARGV[4]) end
                return redis.call(unpack(args))
                """;

        return executeLua(
                lua,
                Collections.singletonList(key),
                value,
                score,
                incr ? "1" : "0",
                payload == null ? "" : payload
        );
    }

    public Long sugAdd(String key,
                       String value,
                       double score) {
        return sugAdd(key, value, score, false, "");
    }


    /**
     * 查询补全建议
     * <p>
     * FT.SUGGET key prefix
     * [FUZZY]
     * [WITHSCORES]
     * [WITHPAYLOADS]
     * [MAX num]
     *
     * @param key          索引名称
     * @param prefix       前缀
     * @param fuzzy        是否模糊匹配，默认为false
     * @param withScores   是否返回分数，默认为false
     * @param withPayloads 是否返回附加信息，默认为false
     * @param max           最大返回数量，默认为5
     */
    public List<Suggestion> sugGet(String key,
                                   String prefix,
                                   boolean fuzzy,
                                   boolean withScores,
                                   boolean withPayloads,
                                   int max) {
        String lua = """
                local args = {'FT.SUGGET', KEYS[1], ARGV[1]}
                if ARGV[2] == '1' then table.insert(args, 'FUZZY') end
                if ARGV[3] == '1' then table.insert(args, 'WITHSCORES') end
                if ARGV[4] == '1' then table.insert(args, 'WITHPAYLOADS') end
                if tonumber(ARGV[5]) > 0 then table.insert(args, 'MAX'); table.insert(args, ARGV[5]) end
                return redis.call(unpack(args))
                """;

        List<Object> raw = executeLua(
                lua,
                Collections.singletonList(key),
                prefix,
                fuzzy ? "1" : "0",
                withScores ? "1" : "0",
                withPayloads ? "1" : "0",
                max
        );

        return parseSuggestionResult(raw, withScores, withPayloads);
    }

    // 结果解析器
    private List<Suggestion> parseSuggestionResult(List<Object> raw,
                                                   boolean withScores,
                                                   boolean withPayloads) {

        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }

        List<Suggestion> list = new ArrayList<>();

        int step = 1
                + (withScores ? 1 : 0)
                + (withPayloads ? 1 : 0);

        for (int i = 0; i < raw.size(); i += step) {
            int idx = i;

            String value = String.valueOf(raw.get(idx++));
            Double score = withScores ? Double.valueOf(String.valueOf(raw.get(idx++))) : null;
            String payload = withPayloads ? String.valueOf(raw.get(idx++)) : null;

            list.add(new Suggestion(value, score, payload));
        }

        return list;
    }


    /**
     * 删除补全词
     * <p>
     * FT.SUGDEL key string
     *
     * @param key   索引名称
     * @param value 补全词
     */
    public Boolean sugDel(String key, String value) {
        String lua = "return redis.call('FT.SUGDEL', KEYS[1], ARGV[1])";

        Object result = executeLua(
                lua,
                Collections.singletonList(key),
                value
        );

        return Long.valueOf(1).equals(result);
    }

    /**
     * 获取补全词数量
     * <p>
     * FT.SUGLEN key
     *
     * @param key 索引名称
     */
    public Long sugLen(String key) {
        String lua = "return redis.call('FT.SUGLEN', KEYS[1])";

        return executeLua(
                lua,
                Collections.singletonList(key)
        );
    }


}
