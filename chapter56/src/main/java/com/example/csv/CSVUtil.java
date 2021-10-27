package com.example.csv;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/13 22:56.
 */

@Slf4j
public class CSVUtil {

    private final static String CSV = "csv";

    public static <T> List<T> readCSV(Class<T> cls, InputStream inputStream) {
        List<T> dataList = new ArrayList<>();

        //类映射  注解 title-->bean columns
        Map<String, List<Field>> classMap = new HashMap<>();
        List<String> requiredFields = new ArrayList<>();
        List<Field> fields = Stream.of(cls.getDeclaredFields()).collect(Collectors.toList());
        fields.forEach(
                field -> {
                    CSVColumn annotation = field.getAnnotation(CSVColumn.class);
                    if (annotation != null) {
                        //title
                        String title = annotation.title();
                        //required
                        boolean isNeedValue = annotation.required();

                        if (isNeedValue) {
                            requiredFields.add(title);
                        }
                        if (StringUtils.isBlank(title)) {
                            return;//return起到的作用和continue是相同的 语法
                        }
                        if (!classMap.containsKey(title)) {
                            classMap.put(title, new ArrayList<>());
                        }
                        field.setAccessible(true);
                        classMap.get(title).add(field);

                    }
                }
        );

        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            CSVReader reader = new CSVReader(inputStreamReader);
            //标题行
            String[] header = reader.readNext();
            for (int i = 0; i < header.length; i++) {

                //如遇特殊字符，可以在此处打印assii码
                //char[] chars =  header[i].toCharArray();
                //for(char c:chars){
                //    int value = c;
                //    System.out.println("vaule="+value);
                //}

                String titleValue = header[i].replaceAll("\\*", "").replaceAll("\\uFEFF", "").trim();
                header[i] = titleValue;
            }

            List<String> firstCells = Arrays.asList(header);
            //验证封装对象要求为isNeedValue=true的字段是否都有对应的列
            if (!firstCells.containsAll(requiredFields)) {
                throw new RuntimeException(String.format("csv中缺少必要的列，请核对csv文件! 必须包含的列 [%s]", StringUtils.join(requiredFields, ",")));
            }

            //索引-->columns
            Map<Integer, List<Field>> reflectionMap = new HashMap<>(16);
            for (int i = 0; i < header.length; i++) {
                if (classMap.containsKey(header[i])) {
                    reflectionMap.put(i, classMap.get(header[i]));
                }
            }
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                T t = cls.newInstance();
                //判断是否为空白行
                boolean allBlank = true;
                for (int i = 0; i < header.length; i++) {
                    if (nextLine.length < header.length) {
                        continue;
                    }
                    if (reflectionMap.containsKey(i)) {
                        String cellValue = nextLine[i].trim();
                        if (StringUtils.isNotBlank(cellValue)) {
                            allBlank = false;
                        }
                        List<Field> fieldList = reflectionMap.get(i);
                        fieldList.forEach(
                                x -> {
                                    try {
                                        handleField(t, cellValue, x);
                                    } catch (Exception e) {
                                        log.error(String.format("reflect field:%s value:%s exception!", x.getName(), cellValue), e);
                                        throw new RuntimeException(String.format("reflect field:%s title:%s exception! ", x.getName(), cellValue) + e.getMessage());
                                    }
                                }
                        );
                    }
                }
                if (!allBlank) {
                    dataList.add(t);
                } else {
                    //遇到空白行则停止扫描
                    break;
                }
            }
        } catch (Exception e) {
            log.error(String.format("parse csv exception!"), e);
            throw new RuntimeException("parse csv exception!" + e.getMessage());
        }

        return dataList;
    }


    /**
     * <h2>解析csv并封装为对象list</h2>
     *
     * @param cls  对象类型
     * @param file 文件
     * @return java.util.List&lt;T&gt;
     * @author hanqf
     */
    public static <T> List<T> readCSV(Class<T> cls, File file) {

        if (file == null) {
            throw new RuntimeException("未上传任何文件");
        }

        String fileName = file.getName();
        if (!fileName.matches("^.+\\.(?i)(csv)$")) {
            throw new RuntimeException("上传文件格式不正确");
        }
        List<T> list = null;
        try (InputStream inputStream = new FileInputStream(file)) {
            list = readCSV(cls, inputStream);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException("parse csv exception!" + e.getMessage());
        }

        return list;
    }

    private static <T> void handleField(T t, String value, Field field) throws Exception {
        CSVColumn annotation = field.getAnnotation(CSVColumn.class);
        String title = "";
        boolean required = false;
        String format = "";
        Class<? extends AbstractConvertCsvBase> convert = null;
        if (annotation != null) {
            title = annotation.title();
            required = annotation.required();
            convert = annotation.convert();
            format = annotation.format();
        }
        //验证必填项是否有值
        if (StringUtils.isBlank(value)) {
            if (required) {
                throw new RuntimeException(String.format("列名称:[%s]，其值不能为空，请核对csv! ", title));
            }
        }

        Class<?> type = field.getType();
        if (type == null || type == void.class || StringUtils.isBlank(value)) {
            return;
        }

        if (convert == null || convert.isAssignableFrom(AbstractConvertCsvBase.Converter.class)) {
            if (type == Object.class) {
                field.set(t, value);
                //数字类型
            } else if (type.getSuperclass() == null || type.getSuperclass() == Number.class) {
                if (type == int.class || type == Integer.class) {
                    if (value.contains(".")) {
                        value = value.substring(0, value.indexOf("."));
                    }
                    field.set(t, NumberUtils.toInt(value));
                } else if (type == long.class || type == Long.class) {
                    if (value.contains(".")) {
                        value = value.substring(0, value.indexOf("."));
                    }
                    field.set(t, NumberUtils.toLong(value));
                } else if (type == byte.class || type == Byte.class) {
                    field.set(t, NumberUtils.toByte(value));
                } else if (type == short.class || type == Short.class) {
                    field.set(t, NumberUtils.toShort(value));
                } else if (type == double.class || type == Double.class) {
                    field.set(t, NumberUtils.toDouble(value));
                } else if (type == float.class || type == Float.class) {
                    field.set(t, NumberUtils.toFloat(value));
                } else if (type == char.class || type == Character.class) {
                    field.set(t, CharUtils.toChar(value));
                } else if (type == boolean.class) {
                    field.set(t, BooleanUtils.toBoolean(value));
                } else if (type == BigDecimal.class) {
                    field.set(t, new BigDecimal(value));
                }
            } else if (type == Boolean.class) {
                field.set(t, BooleanUtils.toBoolean(value));
            } else if (type == Date.class) {
                String[] patterns = {"yyyy-MM-dd HH:mm:dd", "yyyy/MM/dd HH:mm:dd", "yyyy-MM-dd", "yyyy/MM/dd"};
                if (StringUtils.isNotBlank(format)) {
                    patterns = new String[]{format};
                }
                Date date = DateUtils.parseDate(value, patterns);
                field.set(t, date);
            } else if (type == LocalDate.class) {
                String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd"};
                if (StringUtils.isNotBlank(format)) {
                    patterns = new String[]{format};
                }
                Date date = DateUtils.parseDate(value, patterns);
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                field.set(t, localDate);
            } else if (type == LocalDateTime.class) {
                String[] patterns = {"yyyy-MM-dd HH:mm:dd", "yyyy/MM/dd HH:mm:dd"};
                if (StringUtils.isNotBlank(format)) {
                    patterns = new String[]{format};
                }
                Date date = DateUtils.parseDate(value, patterns);
                LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                field.set(t, localDateTime);
            } else if (type == String.class) {
                field.set(t, value);
            } else {
                Constructor<?> constructor = type.getConstructor(String.class);
                field.set(t, constructor.newInstance(value));
            }
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("value", value);
            Object obj = convert.newInstance().startConvert(params);
            field.set(t, obj);
        }

    }
}
