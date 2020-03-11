package com.example.model;

/**
 * Created by hanqf on 2020/3/8 17:45.
 */


public enum DelEnum {
    one("1","已删除"),
    zero("0","未删除");

    private String id;
    private String name;
    private DelEnum(String id,String name) {
        this.id=id;
        this.name=name;
    }
    public String getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }

    public static DelEnum getDelEnum(String s){
        for(DelEnum delEnum :DelEnum.values()){
            if(delEnum.getId().equals(s)){
                return delEnum;
            }
        }
        return null;
    }
}
