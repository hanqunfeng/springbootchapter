package com.example.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * <h1>csv转换对象</h1>
 * Created by hanqf on 2021/10/15 10:10.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PojoDemoCsvColumn {

    private static final String CSV_READ_FiLE = "/Users/hanqf/Desktop/read.csv";
    @CSVColumn(title = "姓名", required = true)
    private String name;
    @CSVColumn(title = "年龄", required = true)
    private Integer age;
    @CSVColumn(title = "薪水")
    private Double salary;
    @CSVColumn(title = "生日", format = "yyyy-MM-dd")
    private LocalDate birthday;

    public static void main(String[] args) {
        final List<PojoDemoCsvColumn> list = CSVUtil.readCSV(PojoDemoCsvColumn.class, new File(CSV_READ_FiLE));

        list.forEach(System.out::println);
    }
}
