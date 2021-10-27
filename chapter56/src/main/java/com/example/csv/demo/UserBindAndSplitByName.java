package com.example.csv.demo;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/18 10:51.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBindAndSplitByName {
    @CsvOrder(order = 5)
    //指定读取分隔符splitOn 支持正则表达式 这里表示一个或多个分号，写入分隔符writeDelimiter
    @CsvBindAndSplitByName(column = "日期", elementType = Date.class, splitOn = ";+", writeDelimiter = ";")
    @CsvDate(value = "yyyy-MM-dd")
    SortedSet<Date> Dates;
    @CsvOrder(order = 1)
    @CsvBindByName(column = "姓名")
    private String Name;
    @CsvOrder(order = 3)
    @CsvBindByName(column = "email")
    private String Email;
    @CsvOrder(order = 2)
    @CsvBindByName(column = "地址")
    private String Address;
    @CsvOrder(order = 4)
    //分隔符，默认空白字符，如空格或Tab
    @CsvBindAndSplitByName(column = "电话", elementType = String.class)
    private List<String> PhoneNumbers;
}

class RunMainByName {

    private static String FILE = "/Users/hanqf/Desktop/UserBindAndSplitByName.csv";

    private static void writeFile(String file) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, ParseException {
        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(file))
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER) //不包含双引号，对读取没有影响
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()) {


            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

            List<UserBindAndSplitByName> list = new ArrayList<>();
            list.add(new UserBindAndSplitByName(new TreeSet<>(Arrays.asList(dateFormat.parse("2021-01-01"), dateFormat.parse("2021-01-02"))), "张三", "baa@123.com", "xxxxx", Arrays.asList("12222", "22222", "333334")));
            list.add(new UserBindAndSplitByName(new TreeSet<>(Arrays.asList(dateFormat.parse("2021-01-01"), dateFormat.parse("2021-01-02"))), "李四", "aaa@123.com", "xxxxx", Arrays.asList("12222", "22222", "333334")));
            list.add(new UserBindAndSplitByName(new TreeSet<>(Arrays.asList(dateFormat.parse("2021-01-01"), dateFormat.parse("2021-01-02"))), "王五", "caa@123.com", "xxxxx", Arrays.asList("12222", "22222", "333334")));

            //增加自定义排序规则
            final HeaderColumnNameMappingStrategy<UserBindAndSplitByName> strategy = new HeaderColumnNameMappingStrategyBuilder<UserBindAndSplitByName>().build();
            strategy.setType(UserBindAndSplitByName.class);
            strategy.setColumnOrderOnWrite((o1, o2) -> CsvColumnOrderOnWriteHelper.compare(o1, o2, UserBindAndSplitByName.class));

            final StatefulBeanToCsv<UserBindAndSplitByName> beanToCsv = new StatefulBeanToCsvBuilder<UserBindAndSplitByName>(csvWriter)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(list);
        }
    }

    private static void readFile(String file) throws FileNotFoundException {
        List<UserBindAndSplitByName> list = new CsvToBeanBuilder(new FileReader(file))
                .withType(UserBindAndSplitByName.class)
                //.withSkipLines(1) //跳过标题行
                .build().parse();
        list.stream().forEach(System.out::println);
    }

    public static void main(String[] args) throws IOException, ParseException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        writeFile(FILE);
        readFile(FILE);

    }
}
