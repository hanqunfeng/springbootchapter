package com.hanqf.demo.support.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.*;

/**
 *
 * Created by hanqf on 2025/8/29 11:33.
 */


public class OrderItemComplexAlgorithm implements ComplexKeysShardingAlgorithm<Long> {
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Long> complexKeysShardingValue) {

        // 获取分片键
        Collection<Long> userIds = getShardingValue(complexKeysShardingValue, "user_id");
        Collection<Long> orderIds = getShardingValue(complexKeysShardingValue, "order_id");

        Set<String> result = new LinkedHashSet<>();
        for (Long userId : userIds) {
            for (Long orderId : orderIds) {
                // 这里定义表的选择逻辑，例如：根据 (user_id + order_id + 1) % 2
                String tbl_name = "t_order_item_complex_" + ((userId + orderId + 1) % 2);
                if (availableTargetNames.contains(tbl_name)) {
                    result.add(tbl_name);
                }
            }
        }
        return result;
    }

    private Collection<Long> getShardingValue(ComplexKeysShardingValue<Long> shardingValue, String key) {
        return Optional.ofNullable(shardingValue.getColumnNameAndShardingValuesMap().get(key))
                .orElse(Collections.emptyList());
    }

    /**
     * 获取分片算法名称
     * 通过 SPI 注册自定义分片算法，这样你在 sharding.yml 里就只需要写 type: T_ORDER_ITEM_COMPLEX，而不用再写 algorithm-class-name。
     * @return 分片算法名称
     */
    @Override
    public String getType() {
        return "T_ORDER_ITEM_COMPLEX";
    }
}
