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
public class UserBindAndSplitByPosition {
    //指定读取分隔符splitOn 支持正则表达式 这里表示一个或多个分号，写入分隔符writeDelimiter
    @CsvBindAndSplitByPosition(position = 4, elementType = Date.class, splitOn = ";+", writeDelimiter = ";")
    @CsvDate(value = "yyyy-MM-dd")
    SortedSet<Date> Dates;
    @CsvBindByPosition(position = 0)
    private String Name;
    @CsvBindByPosition(position = 1)
    private String Email;
    @CsvBindByPosition(position = 2)
    private String Address;
    //分隔符，默认空白字符，如空格或Tab
    @CsvBindAndSplitByPosition(position = 3, elementType = String.class)
    private List<String> PhoneNumbers;
}

class RunMainByPosition {

    private static String FILE = "/Users/hanqf/Desktop/UserBindAndSplitByPosition.csv";

    private static void writeFile(String file) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, ParseException {
        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(file))
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER) //不包含双引号，对读取没有影响
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()) {

            //按位置匹配时是没有表头的，可以自行写入
            String[] header = {"姓名", "邮箱", "地址", "电话号码", "日期"};
            csvWriter.writeNext(header);


            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

            List<UserBindAndSplitByPosition> list = new ArrayList<>();
            list.add(new UserBindAndSplitByPosition(new TreeSet<>(Arrays.asList(dateFormat.parse("2021-01-01"), dateFormat.parse("2021-01-02"))), "张三", "aaa@123.com", "xxxxx", Arrays.asList("12222", "22222", "333334")));
            list.add(new UserBindAndSplitByPosition(new TreeSet<>(Arrays.asList(dateFormat.parse("2021-01-01"), dateFormat.parse("2021-01-02"))), "李四", "aaa@123.com", "xxxxx", Arrays.asList("12222", "22222", "333334")));
            list.add(new UserBindAndSplitByPosition(new TreeSet<>(Arrays.asList(dateFormat.parse("2021-01-01"), dateFormat.parse("2021-01-02"))), "王五", "aaa@123.com", "xxxxx", Arrays.asList("12222", "22222", "333334")));

            final StatefulBeanToCsv<UserBindAndSplitByPosition> beanToCsv = new StatefulBeanToCsvBuilder<UserBindAndSplitByPosition>(csvWriter).build();
            beanToCsv.write(list);
        }
    }

    private static void readFile(String file) throws FileNotFoundException {
        List<UserBindAndSplitByPosition> list = new CsvToBeanBuilder(new FileReader(file))
                .withType(UserBindAndSplitByPosition.class)
                .withSkipLines(1) //跳过标题行
                .build().parse();
        list.stream().forEach(System.out::println);
    }

    public static void main(String[] args) throws IOException, ParseException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        writeFile(FILE);
        readFile(FILE);

    }
}
