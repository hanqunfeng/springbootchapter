package com.example.csv.demo;

import com.opencsv.bean.CsvBindByPosition;
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
public class PojoDemoBindByPosition {

    @CsvBindByPosition(position = 0,required = true)
    private String name;

    @CsvBindByPosition(position = 1,required = true)
    private Integer age;

    @CsvBindByPosition(position = 2)
    private Double salary;

    //日期类型要指定格式
    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByPosition(position = 3)
    private LocalDate birthday;
}
