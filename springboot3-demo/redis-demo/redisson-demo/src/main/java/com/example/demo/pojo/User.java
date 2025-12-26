package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 *
 * Created by hanqf on 2025/12/15 16:46.
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User implements Serializable{
    private static final long serialVersionUID = 8296397356420867068L;


    /**
     * profile : {"name":"Alice","age":28,"vip":true}
     * tags : ["vip","electronics","promotion"]
     * addresses : [{"city":"Beijing","zip":"100000"},{"city":"Shanghai","zip":"200000"}]
     * orders : [{"orderId":"O1001","amount":199.99,"status":"PAID","items":[{"sku":"SKU-1","price":99.99,"qty":1},{"sku":"SKU-2","price":100,"qty":1}]},{"orderId":"O1002","amount":59.9,"status":"CREATED","items":[{"sku":"SKU-3","price":59.9,"qty":1}]}]
     * stats : {"loginCount":10,"balance":300.5}
     */

    private ProfileBean profile;
    private StatsBean stats;
    private List<String> tags;
    private List<AddressesBean> addresses;
    private List<OrdersBean> orders;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class ProfileBean {
        /**
         * name : Alice
         * age : 28
         * vip : true
         */

        private String name;
        private int age;
        private boolean vip;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class StatsBean {
        /**
         * loginCount : 10
         * balance : 300.5
         */

        private int loginCount;
        private double balance;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class AddressesBean {
        /**
         * city : Beijing
         * zip : 100000
         */

        private String city;
        private String zip;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class OrdersBean {
        /**
         * orderId : O1001
         * amount : 199.99
         * status : PAID
         * items : [{"sku":"SKU-1","price":99.99,"qty":1},{"sku":"SKU-2","price":100,"qty":1}]
         */

        private String orderId;
        private double amount;
        private String status;
        private List<ItemsBean> items;

        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        @Builder
        public static class ItemsBean {
            /**
             * sku : SKU-1
             * price : 99.99
             * qty : 1
             */

            private String sku;
            private double price;
            private int qty;
        }
    }
}
