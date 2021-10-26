package com.example.demo.hasAnnotation;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/25 15:17.
 */

@Data
public class DemoDataHasAnnotation {
    private static final String FILE_NAME = "/Users/hanqf/Desktop/demo.xlsx";

    private static final String WRITE_FILE_NAME = "/Users/hanqf/Desktop/demo_write.xlsx";

    @ExcelProperty("姓名")
    //@ExcelProperty(index = 0) //序号从0开始，不建议和名称混用
    private String string;
    @ExcelProperty("上班时间")
    private Date date;
    @ExcelProperty("序号")
    private Double doubleData;

    //不加这个注解也不会有问题
    @ExcelIgnore
    private String noDate;


    private static void read1() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取3000条数据 然后返回过来 直接调用使用数据就行
        // 此时字段可以不加注解，每一个字段按顺序匹配
        EasyExcel.read(FILE_NAME, DemoDataHasAnnotation.class, new PageReadListener<DemoDataHasAnnotation>(dataList -> {
                    for (DemoDataHasAnnotation demoDataHasAnnotation : dataList) {
                        System.out.println("读取到一条数据:" + demoDataHasAnnotation);
                    }
                    System.out.println("本次读取行数：" + dataList.size());
                }))
                //默认0
                .sheet(0)
                //默认1
                .headRowNumber(1)
                .doRead();
    }


    /**
     * 模拟数据
     */
    private static List<DemoDataHasAnnotation> data() {
        List<DemoDataHasAnnotation> list = new ArrayList<>();
        for (int i = 0; i < 4000; i++) {
            DemoDataHasAnnotation data = new DemoDataHasAnnotation();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }

    private static void write1() {
        // 写法1 JDK8+
        // since: 3.0.0-beta1
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(WRITE_FILE_NAME, DemoDataHasAnnotation.class)
                .sheet("模板")
                .doWrite(() -> data());

        //或者
        EasyExcel.write(WRITE_FILE_NAME, DemoDataHasAnnotation.class)
                .sheet("模板")
                .doWrite(data());
    }

    private static void write2() {
        // 这里需要指定写用哪个class去写
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(WRITE_FILE_NAME, DemoDataHasAnnotation.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
            excelWriter.write(data(), writeSheet);
        } finally {
            // 千万别忘记finish 关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }


    public static void main(String[] args) {
        read1();
        //write1();
    }
}
