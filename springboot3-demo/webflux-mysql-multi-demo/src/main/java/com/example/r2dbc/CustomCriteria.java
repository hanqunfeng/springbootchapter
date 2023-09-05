package com.example.r2dbc;

import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.query.Criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <h1>CustomCriteria</h1>
 * Criteria 构造器
 * Created by hanqf on 2023/9/4 11:47.
 *
 *
 * 示例：
 * 1.and 关系: select * from tbl_table where (username like '%admin%' or username like 'lisi%') and enable = 1
 * Criteria criteria = CustomCriteria.and()
 *                 .like(true, "username", "%admin%", "lisi%")
 *                 .eq(true, "enable", 1)
 *                 .build();
 *
 * 2.or 关系: select * from tbl_table where (username like '%admin%' or username like 'lisi%') or enable = 1
 * Criteria criteria = CustomCriteria.or()
 *                 .like(true, "username", "%admin%", "lisi%")
 *                 .eq(true, "enable", 1)
 *                 .build();
 *
 * 3.复合关系：
 * Criteria criteria1 = CustomCriteria.and()
 *                 .like(true, "username", "%admin%", "lisi%")
 *                 .eq(true, "enable", 1)
 *                 .build();

 * Criteria criteria2 = CustomCriteria.or()
 *                 .like(true, "username", "%admin%", "lisi%")
 *                 .eq(true, "enable", 1)
 *                 .build();
 *
 * Criteria criteria = criteria1.and(criteria2);  // or： criteria1.or(criteria2);
 *
 * 4.复杂查询建议直接使用 sql 进行查询，可以使用  BaseR2dbcRepository 中的 execSqlToMono 和 execSqlToFlux
 * 示例：
 *  public Mono<TestOrder> getOne(String orderId) {
 *         String sql = "select id, order_id from test_order where order_id = :orderId";
 *
 *         return testOrderRepository.execSqlToMono(sql, Map.of("orderId", orderId), (row, rowMetadata) -> {
 *             final TestOrder testOrder = new TestOrder();
 *             testOrder.setId(row.get("id", Long.class));
 *             testOrder.setOrderId(row.get("order_id", String.class));
 *             return testOrder;
 *         });
 *     }
 *
 * 说明：
 * 1.eq(true, "enable", 1)：第一个参数为真时当前条件加入查询，默认为真
 * 2.支持条件方法详见代码
 */

public class CustomCriteria {

    public enum CriteriaOperator {
        AND, OR
    }

    public static CustomCriteriaBuilder and() {
        return new CustomCriteriaBuilder(CriteriaOperator.AND);
    }

    public static CustomCriteriaBuilder or() {
        return new CustomCriteriaBuilder(CriteriaOperator.OR);
    }

    interface BaseCriteria {
        Criteria toCriteria();
    }

    @AllArgsConstructor
    static class LikeCriteria implements BaseCriteria {
        String property;
        Object[] value;

        @Override
        public Criteria toCriteria() {
            Criteria criteria = Criteria.empty();
            for (Object v : value) {
                criteria = criteria.or(property).like(v);
            }
            return criteria;
        }
    }

    @AllArgsConstructor
    static class NotLikeCriteria implements BaseCriteria {
        String property;
        Object[] value;

        @Override
        public Criteria toCriteria() {
            Criteria criteria = Criteria.empty();
            for (Object v : value) {
                criteria = criteria.or(property).notLike(v);
            }

            return criteria;
        }
    }

    @AllArgsConstructor
    static class EqCriteria implements BaseCriteria {
        String property;
        Object[] value;

        @Override
        public Criteria toCriteria() {
            Criteria criteria = Criteria.empty();
            for (Object v : value) {
                criteria = criteria.or(property).is(v);
            }

            return criteria;
        }
    }

    @AllArgsConstructor
    static class IgnoreCaseEqCriteria implements BaseCriteria {
        String property;
        Object[] value;

        @Override
        public Criteria toCriteria() {
            Criteria criteria = Criteria.empty().ignoreCase(true);
            for (Object v : value) {
                criteria = criteria.or(property).is(v);
            }

            return criteria;
        }
    }

    @AllArgsConstructor
    static class NotEqCriteria implements BaseCriteria {
        String property;
        Object[] value;

        @Override
        public Criteria toCriteria() {
            Criteria criteria = Criteria.empty();
            for (Object v : value) {
                criteria = criteria.or(property).not(v);
            }

            return criteria;
        }
    }

    @AllArgsConstructor
    static class IgnoreCaseNotEqCriteria implements BaseCriteria {
        String property;
        Object[] value;

        @Override
        public Criteria toCriteria() {
            Criteria criteria = Criteria.empty().ignoreCase(true);
            for (Object v : value) {
                criteria = criteria.or(property).not(v);
            }

            return criteria;
        }
    }

    @AllArgsConstructor
    static class LeCriteria implements BaseCriteria {
        String property;
        Object value;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).lessThanOrEquals(value);
        }
    }

    @AllArgsConstructor
    static class LtCriteria implements BaseCriteria {
        String property;
        Object value;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).lessThan(value);
        }
    }

    @AllArgsConstructor
    static class GeCriteria implements BaseCriteria {
        String property;
        Object value;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).greaterThanOrEquals(value);
        }
    }

    @AllArgsConstructor
    static class GtCriteria implements BaseCriteria {
        String property;
        Object value;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).greaterThan(value);
        }
    }

    @AllArgsConstructor
    static class InCriteria implements BaseCriteria {
        String property;
        Collection<?> value;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).in(value);
        }
    }

    @AllArgsConstructor
    static class NotInCriteria implements BaseCriteria {
        String property;
        Collection<?> value;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).notIn(value);
        }
    }

    @AllArgsConstructor
    static class IsNullCriteria implements BaseCriteria {
        String property;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).isNull();
        }
    }

    @AllArgsConstructor
    static class IsNotNullCriteria implements BaseCriteria {
        String property;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).isNotNull();
        }
    }

    @AllArgsConstructor
    static class BetweenCriteria implements BaseCriteria {
        String property;
        Object begin;
        Object end;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).between(begin, end);
        }
    }

    @AllArgsConstructor
    static class NotBetweenCriteria implements BaseCriteria {
        String property;
        Object begin;
        Object end;

        @Override
        public Criteria toCriteria() {
            return Criteria.empty().and(property).notBetween(begin, end);
        }
    }

    public static class CustomCriteriaBuilder {
        CriteriaOperator criteriaOperator;
        List<BaseCriteria> baseCriteriaList;

        public CustomCriteriaBuilder(CriteriaOperator criteriaOperator) {
            this.criteriaOperator = criteriaOperator;
            this.baseCriteriaList = new ArrayList<>();
        }

        private Criteria criteria = Criteria.empty();

        public Criteria build() {
            for (BaseCriteria baseCriteria : baseCriteriaList) {
                criteria = CriteriaOperator.AND.equals(criteriaOperator) ? criteria.and(baseCriteria.toCriteria()) : criteria.or(baseCriteria.toCriteria());
            }
            return criteria;
        }


        public CustomCriteriaBuilder like(boolean condition, String property, Object... value) {
            if (condition) {
                baseCriteriaList.add(new LikeCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder like(String property, Object... value) {
            return like(true, property, value);
        }

        public CustomCriteriaBuilder notLike(boolean condition, String property, Object... value) {
            if (condition) {
                baseCriteriaList.add(new NotLikeCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder notLike(String property, Object... value) {
            return notLike(true, property, value);
        }

        public CustomCriteriaBuilder eq(boolean condition, String property, Object... value) {
            if (condition) {
                baseCriteriaList.add(new EqCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder eq(String property, Object... value) {
            return eq(true, property, value);
        }

        public CustomCriteriaBuilder ignoreCaseEq(boolean condition, String property, Object... value) {
            if (condition) {
                baseCriteriaList.add(new IgnoreCaseEqCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder ignoreCaseEq(String property, Object... value) {
            return ignoreCaseEq(true, property, value);
        }

        public CustomCriteriaBuilder notEq(boolean condition, String property, Object... value) {
            if (condition) {
                baseCriteriaList.add(new NotEqCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder notEq(String property, Object... value) {
            return notEq(true, property, value);
        }

        public CustomCriteriaBuilder ignoreCaseNotEq(boolean condition, String property, Object... value) {
            if (condition) {
                baseCriteriaList.add(new IgnoreCaseNotEqCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder ignoreCaseNotEq(String property, Object... value) {
            return ignoreCaseNotEq(true, property, value);
        }

        public CustomCriteriaBuilder le(boolean condition, String property, Object value) {
            if (condition) {
                baseCriteriaList.add(new LeCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder le(String property, Object value) {
            return le(true, property, value);
        }

        public CustomCriteriaBuilder lt(boolean condition, String property, Object value) {
            if (condition) {
                baseCriteriaList.add(new LtCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder lt(String property, Object value) {
            return lt(true, property, value);
        }

        public CustomCriteriaBuilder ge(boolean condition, String property, Object value) {
            if (condition) {
                baseCriteriaList.add(new GeCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder ge(String property, Object value) {
            return ge(true, property, value);
        }

        public CustomCriteriaBuilder gt(boolean condition, String property, Object value) {
            if (condition) {
                baseCriteriaList.add(new GtCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder gt(String property, Object value) {
            return gt(true, property, value);
        }

        public CustomCriteriaBuilder in(boolean condition, String property, Collection<?> value) {
            if (condition) {
                baseCriteriaList.add(new InCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder in(String property, Collection<?> value) {
            return in(true, property, value);
        }

        public CustomCriteriaBuilder in(boolean condition, String property, Object... value) {
            return in(condition, property, Arrays.stream(value).toList());
        }

        public CustomCriteriaBuilder in(String property, Object... value) {
            return in(true, property, value);
        }

        public CustomCriteriaBuilder notIn(boolean condition, String property, Collection<?> value) {
            if (condition) {
                baseCriteriaList.add(new NotInCriteria(property, value));
            }
            return this;
        }

        public CustomCriteriaBuilder notIn(String property, Collection<?> value) {
            return notIn(true, property, value);
        }

        public CustomCriteriaBuilder notIn(boolean condition, String property, Object... value) {
            return notIn(condition, property, Arrays.stream(value).toList());
        }

        public CustomCriteriaBuilder notIn(String property, Object... value) {
            return notIn(true, property, value);
        }

        public CustomCriteriaBuilder between(boolean condition, String property, Object start, Object end) {
            if (condition) {
                baseCriteriaList.add(new BetweenCriteria(property, start, end));
            }
            return this;
        }

        public CustomCriteriaBuilder between(String property, Object start, Object end) {
            return between(true, property, start, end);
        }

        public CustomCriteriaBuilder notBetween(boolean condition, String property, Object start, Object end) {
            if (condition) {
                baseCriteriaList.add(new NotBetweenCriteria(property, start, end));
            }
            return this;
        }

        public CustomCriteriaBuilder notBetween(String property, Object start, Object end) {
            return notBetween(true, property, start, end);
        }

        public CustomCriteriaBuilder isNull(boolean condition, String property) {
            if (condition) {
                baseCriteriaList.add(new IsNullCriteria(property));
            }
            return this;
        }

        public CustomCriteriaBuilder isNull(String property) {
            return isNull(true, property);
        }

        public CustomCriteriaBuilder isNotNull(boolean condition, String property) {
            if (condition) {
                baseCriteriaList.add(new IsNotNullCriteria(property));
            }
            return this;
        }

        public CustomCriteriaBuilder isNotNull(String property) {
            return isNotNull(true, property);
        }
    }
}
