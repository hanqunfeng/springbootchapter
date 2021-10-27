package com.example.csv.demo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * <h1>csv转换对象</h1>
 * Created by hanqf on 2021/10/15 10:10.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PojoDemoBindByName {

    @CsvBindByName(column = "姓名",required = true)
    private String name;

    @CsvBindByName(column = "年龄",required = true)
    private Integer age;

    @CsvBindByName(column = "薪水")
    private Double salary;

    //日期类型要指定格式
    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "生日")
    private LocalDate birthday;
}
