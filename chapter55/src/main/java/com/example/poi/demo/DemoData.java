package com.example.poi.demo;

import com.example.poi.ExcelColumn;
import com.example.poi.ExcelUtil;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/25 11:22.
 */


@Data
public class DemoData {
    private static final String FILE_NAME = "/Users/hanqf/Desktop/demo.xlsx";
    private static final String WRITE_FILE_NAME = "/Users/hanqf/Desktop/demo_write.xlsx";

    @ExcelColumn(title = "姓名",required = true)
    private String string;
    @ExcelColumn(title = "上班时间",required = true)
    private Date date;
    @ExcelColumn(title = "序号",required = true)
    private Double doubleData;

    //没有这一列
    private String noDate;

    private static void read() {
        final List<DemoData> demoData = ExcelUtil.readExcelByTitle(DemoData.class, new File(FILE_NAME));
        demoData.forEach(System.out::println);
    }

    private static void write(){
        ExcelUtil.writeExcel(data(),DemoData.class,WRITE_FILE_NAME);
    }

    /**
     * 模拟数据
     */
    private static List<DemoData> data() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 4000; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }








    public static void main(String[] args) {

        //read();

        write();
    }
}
