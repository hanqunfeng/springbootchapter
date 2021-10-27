package com.example.csv.demo;

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
public class PojoDemo {

    private String name;

    private Integer age;

    private Double salary;

    //日期类型要指定格式，读和写格式可以分别指定
    @CsvDate(value = "yyyy-MM-dd", writeFormat = "yyyy/MM/dd", writeFormatEqualsReadFormat = false)
    private LocalDate birthday;
}
