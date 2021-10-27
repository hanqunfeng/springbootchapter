package com.example.csv.demo;

import com.opencsv.*;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/15 10:16.
 * <p>
 * 姓名,年龄,薪水,生日
 * 张三,10,1000.12,2010-01-03
 * 李四,20,2000.12,2020-01-03
 * 王五,30,3000.12,2030-01-03
 */


public class MainDemo {

    private static final String CSV_READ_FiLE = "/Users/hanqf/Desktop/read.csv";
    private static String CSV_WRITE_FiLE = "/Users/hanqf/Desktop/write.csv";

    public static void printContent(String filePath) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new FileReader(filePath));

        String[] nextLine;
        String[] header = reader.readNext();

        while ((nextLine = reader.readNext()) != null) {
            for (int i = 0; i < header.length; i++) {
                System.out.print(nextLine[i] + " ");
            }

            System.out.println();

        }
    }

    public static void readContentToPojoByName(String filePath) throws FileNotFoundException {
        List<PojoDemoBindByName> pojoDemoBindByNameList = new CsvToBeanBuilder(new FileReader(filePath))
                .withType(PojoDemoBindByName.class).build().parse();
        pojoDemoBindByNameList.stream().forEach(System.out::println);
    }

    public static void readContentToPojoByPosition(String filePath) throws FileNotFoundException {
        List<PojoDemoBindByPosition> pojoDemoBindByPositionList = new CsvToBeanBuilder(new FileReader(filePath))
                .withType(PojoDemoBindByPosition.class)
                .withSkipLines(1) //跳过标题行
                .build().parse();
        pojoDemoBindByPositionList.stream().forEach(System.out::println);
    }

    public static void writeCsvByLine(String filePath) throws IOException {
        ///**
        // * Default line terminator.
        // */
        //String DEFAULT_LINE_END = "\n";
        ///**
        // * The character used for escaping quotes.
        // */
        //char DEFAULT_ESCAPE_CHARACTER = '"';
        ///**
        // * The default separator to use if none is supplied to the constructor.
        // */
        //char DEFAULT_SEPARATOR = ',';
        ///**
        // * The default quote character to use if none is supplied to the
        // * constructor.
        // */
        //char DEFAULT_QUOTE_CHARACTER = '"';

        //两种创建csvWriter的方式
        //CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(filePath))
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVParser.DEFAULT_QUOTE_CHARACTER) //包含双引号，对读取没有影响
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()) {
            String[] header = {"姓名", "年龄", "薪水", "生日"};
            csvWriter.writeNext(header);

            String[][] contentLine = {
                    {"张三", "10", "1000.12", "2010-01-03"},
                    {"李四", "20", "2000.12", "2020-01-03"},
                    {"王五", "30", "3000.12", "2030-01-03"}
            };

            for (String[] line : contentLine) {
                csvWriter.writeNext(line);
            }
        }


    }

    public static void writeCsvByPojoByName(String filePath) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        List<PojoDemoBindByName> list = new ArrayList<>();
        list.add(new PojoDemoBindByName("张三1", 10, 1000.12, LocalDate.parse("2010-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemoBindByName("李四1", 20, 2000.12, LocalDate.parse("2020-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemoBindByName("王五1", 30, 3000.12, LocalDate.parse("2030-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));


        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(filePath))
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER) //不包含双引号，对读取没有影响
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()) {
            //写入表头是不需要的，会基于注解自动写入表头
            //String[] header = {"姓名", "年龄", "薪水", "生日"};
            //csvWriter.writeNext(header);
            //写入内容，基于注解同时写入表头，但是会根据字母顺序写入表头，不能设置显示顺序,指定顺序要用ColumnPositionMappingStrategy
            final StatefulBeanToCsv<PojoDemoBindByName> beanToCsv = new StatefulBeanToCsvBuilder<PojoDemoBindByName>(csvWriter).build();
            beanToCsv.write(list);
        }


    }


    public static void writeCsvByPojoByPosition(String filePath) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        List<PojoDemoBindByPosition> list = new ArrayList<>();
        list.add(new PojoDemoBindByPosition("张三1", 10, 1000.12, LocalDate.parse("2010-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemoBindByPosition("李四1", 20, 2000.12, LocalDate.parse("2020-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemoBindByPosition("王五1", 30, 3000.12, LocalDate.parse("2030-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));


        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(filePath))
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER) //不包含双引号，对读取没有影响
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()) {

            //按位置匹配时是没有表头的，可以自行写入
            String[] header = {"姓名", "年龄", "薪水", "生日"};
            csvWriter.writeNext(header);

            final StatefulBeanToCsv<PojoDemoBindByPosition> beanToCsv = new StatefulBeanToCsvBuilder<PojoDemoBindByPosition>(csvWriter).build();
            beanToCsv.write(list);

        }
    }



    public static void writeCsvByPojo(String filePath) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        List<PojoDemo> list = new ArrayList<>();
        list.add(new PojoDemo("张三", 10, 1000.12, LocalDate.parse("2010-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemo("李四", 20, 2000.12, LocalDate.parse("2020-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemo("王五", 30, 3000.12, LocalDate.parse("2030-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));


        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(filePath))
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER) //不包含双引号，对读取没有影响
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()) {

            //按位置匹配时是没有表头的，可以自行写入
            String[] header = {"姓名", "年龄", "薪水", "生日"};
            csvWriter.writeNext(header);

            // 设置字段的显示的顺序
            String[] columnMapping = {"name", "age", "salary", "birthday"};

            ColumnPositionMappingStrategy<PojoDemo> mapper =
                    new ColumnPositionMappingStrategy<>();
            mapper.setType(PojoDemo.class);
            mapper.setColumnMapping(columnMapping);

            final StatefulBeanToCsv<PojoDemo> beanToCsv = new StatefulBeanToCsvBuilder<PojoDemo>(csvWriter)
                    .withMappingStrategy(mapper)
                    .build();
            beanToCsv.write(list);

        }
    }

    public static void readCsvByPojo(String filePath) throws FileNotFoundException {
        CSVReader reader = new CSVReader(new FileReader(filePath));

        // 列名的映射
        HeaderColumnNameTranslateMappingStrategy<PojoDemo> strategy =
                new HeaderColumnNameTranslateMappingStrategy<>();
        strategy.setType(PojoDemo.class);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("姓名", "name");
        columnMapping.put("年龄", "age");
        columnMapping.put("薪水", "salary");
        columnMapping.put("生日", "birthday");
        strategy.setColumnMapping(columnMapping);

        CsvToBean<PojoDemo> csvToBean = new CsvToBeanBuilder(reader)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .withMappingStrategy(strategy)
                .build();

        List<PojoDemo> list = csvToBean.parse();
        list.stream().forEach(System.out::println);
    }


    public static void writeCsvByPojo2(String filePath) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        List<PojoDemo> list = new ArrayList<>();
        list.add(new PojoDemo("张三", 10, 1000.12, LocalDate.parse("2010-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemo("李四", 20, 2000.12, LocalDate.parse("2020-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        list.add(new PojoDemo("王五", 30, 3000.12, LocalDate.parse("2030-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))));


        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(filePath))
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER) //不包含双引号，对读取没有影响
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build()) {

            Map<String, String> columnMapping = new HashMap<>();
            //第一个代表列名称，第二个代表字段名称，但是此时两个值必须一致，都为字段名称，否则不能写入值
            columnMapping.put("name","name");
            columnMapping.put("age","age");
            columnMapping.put("salary","salary");
            columnMapping.put("birthday","birthday");

            HeaderColumnNameTranslateMappingStrategy<PojoDemo> mapper = new HeaderColumnNameTranslateMappingStrategy<>();
            mapper.setType(PojoDemo.class);
            mapper.setColumnMapping(columnMapping);


            final StatefulBeanToCsv<PojoDemo> beanToCsv = new StatefulBeanToCsvBuilder<PojoDemo>(csvWriter)
                    .withMappingStrategy(mapper)
                    .build();
            beanToCsv.write(list);

        }
    }



    public static void main(String[] args) throws IOException, CsvValidationException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        //基于字符串，按行读写
        writeCsvByLine(CSV_WRITE_FiLE);
        //printContent(CSV_READ_FiLE);
        //
        ////基于Pojo和CSV注解，按名称读写
        //writeCsvByPojoByName(CSV_WRITE_FiLE);
        //readContentToPojoByName(CSV_READ_FiLE);
        //
        ////基于Pojo和CSV注解，按位置读写
        //writeCsvByPojoByPosition(CSV_WRITE_FiLE);
        //readContentToPojoByPosition(CSV_READ_FiLE);
        //
        //
        ////基于Pojo，按指定顺序读写
        //writeCsvByPojo(CSV_WRITE_FiLE);
        //readCsvByPojo(CSV_READ_FiLE);

        //writeCsvByPojo2(CSV_WRITE_FiLE);


    }
}
