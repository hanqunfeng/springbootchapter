package com.example.demo.hasAnnotation;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.Data;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/25 16:50.
 */

@Data
public class ConverterData {
    private static final String FILE_NAME = "/Users/hanqf/Desktop/demo.xlsx";

    /**
     * 自定义转换器，不管数据库传过来什么 。我给他加上“自定义：”
     */
    @ExcelProperty(converter = CustomStringConverter.class)
    private String string;
    /**
     * 这里用string去接日期才能格式化。我想接收年月日格式
     */
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    private String date;
    /**
     * 我想接收百分比的数字
     */
    @NumberFormat("#.##%")
    private String doubleData;


    private static void read1() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取3000条数据 然后返回过来 直接调用使用数据就行
        // 此时字段可以不加注解，每一个字段按顺序匹配
        EasyExcel.read(FILE_NAME, ConverterData.class, new PageReadListener<ConverterData>(dataList -> {
                    for (ConverterData converterData : dataList) {
                        System.out.println("读取到一条数据:" + converterData);
                    }
                    System.out.println("本次读取行数：" + dataList.size());
                }))
                //注册全局转换器,所有java为string,excel为string的都会用这个转换器。
                //.registerConverter(new CustomStringConverter())
                //默认0
                .sheet(0)
                //默认1
                .headRowNumber(1)
                .doRead();
    }


    public static void main(String[] args) {
        read1();
    }

}
