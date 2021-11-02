package com.example.poi;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>excel工具类</h1>
 * Created by hanqf on 2021/8/10 22:50.
 */

@Slf4j
public class ExcelUtil {
    private final static String EXCEL2003 = "xls";
    private final static String EXCEL2007 = "xlsx";


    public static <T> List<T> readExcelByTitle(Class<T> cls, File file) {
        return readExcel(cls, file, 0);
    }

    public static <T> List<T> readExcelByCol(Class<T> cls, File file) {
        return readExcel(cls, file, 1);
    }

    /**
     * <h2>解析excel并封装为对象list</h2>
     * Created by hanqf on 2021/8/11 11:19. <br>
     *
     * @param cls  对象类型
     * @param file 文件
     * @param type 0:按title 1:按列号
     * @return java.util.List&lt;T&gt;
     * @author hanqf
     */
    private static <T> List<T> readExcel(Class<T> cls, File file, int type) {

        if (file == null) {
            throw new RuntimeException("未上传任何文件");
        }

        String fileName = file.getName();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new RuntimeException("上传文件格式不正确");
        }
        List<T> dataList = new ArrayList<>();
        Workbook workbook = null;
        try {
            InputStream is = new FileInputStream(file);
            if (fileName.endsWith(EXCEL2007)) {
                workbook = new XSSFWorkbook(is);
            }
            if (fileName.endsWith(EXCEL2003)) {
                workbook = new HSSFWorkbook(is);
            }
            if (workbook != null) {
                //类映射  注解 title-->bean columns
                Map<String, List<Field>> classMap = new HashMap<>();
                List<String> requiredFields = new ArrayList<>();
                List<Field> fields = Stream.of(cls.getDeclaredFields()).collect(Collectors.toList());
                fields.forEach(
                        field -> {
                            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                            if (annotation != null) {
                                //title
                                String title = annotation.title();
                                //列号，从0开始
                                int col = annotation.col();
                                //required
                                boolean required = annotation.required();
                                if (type == 0) {
                                    if (required) {
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
                                } else {

                                    if (col == -1) {
                                        return;
                                    }
                                    String colStr = String.valueOf(col);
                                    if (required) {
                                        requiredFields.add(colStr);
                                    }
                                    if (!classMap.containsKey(colStr)) {
                                        classMap.put(colStr, new ArrayList<>());
                                    }
                                    field.setAccessible(true);
                                    classMap.get(colStr).add(field);
                                }
                            }
                        }
                );
                //索引-->columns
                Map<Integer, List<Field>> reflectionMap = new HashMap<>(16);
                //默认读取第一个sheet
                Sheet sheet = workbook.getSheetAt(0);

                boolean firstRow = true;
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    //首行  提取注解
                    if (firstRow) {
                        List<String> firstCells = new ArrayList<>();
                        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j);
                            String cellValue = getCellValue(cell).getRight();
                            cellValue = cellValue.replaceAll("\\*", "");
                            if (type == 0) {
                                firstCells.add(cellValue);
                                if (classMap.containsKey(cellValue)) {
                                    reflectionMap.put(j, classMap.get(cellValue));
                                }
                            } else {
                                firstCells.add(String.valueOf(j));
                                if (classMap.containsKey(String.valueOf(j))) {
                                    reflectionMap.put(j, classMap.get(String.valueOf(j)));
                                }
                            }
                        }

                        //验证封装对象要求为isNeedValue=true的字段是否都有对应的列
                        if (!firstCells.containsAll(requiredFields)) {
                            throw new RuntimeException(String.format("excel中缺少必要的列，请核对excel! 必须包含的列 [%s]", StringUtils.join(requiredFields, ",")));
                        }

                        firstRow = false;
                    } else {

                        if (row == null) {
                            //忽略空白行
                            //continue;

                            //遇到空白行则停止扫描
                            break;
                        }
                        try {
                            T t = cls.newInstance();
                            //判断是否为空白行
                            boolean allBlank = true;
                            for (int j = 0; j <= row.getLastCellNum(); j++) {
                                if (reflectionMap.containsKey(j)) {
                                    Cell cell = row.getCell(j);
                                    Pair<CellType, String> pair = getCellValue(cell);
                                    CellType cellType = pair.getLeft();
                                    String cellValue = pair.getRight();

                                    if (StringUtils.isNotBlank(cellValue)) {
                                        allBlank = false;
                                    }
                                    List<Field> fieldList = reflectionMap.get(j);
                                    fieldList.forEach(
                                            x -> {
                                                try {
                                                    //if (x.getType() == String.class && cellType.equals(CellType.NUMERIC)) {
                                                    //    throw new RuntimeException("请设置excel单元格格式为文本类型");
                                                    //}
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
                                log.warn(String.format("row:%s is blank ignore!", i));
                            }
                        } catch (Exception e) {
                            log.error(String.format("parse row:%s exception!", i), e);
                            throw new RuntimeException(String.format("parse row:%s exception! " + e.getMessage(), i));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("parse excel exception!"), e);
            throw new RuntimeException("parse excel exception!");
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    log.error(String.format("parse excel exception!"), e);
                    throw new RuntimeException("parse excel exception!");
                }
            }
        }
        return dataList;
    }


    private static <T> void handleField(T t, String value, Field field) throws Exception {
        //验证必填项是否有值
        if (StringUtils.isBlank(value)) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null) {
                //title
                String title = annotation.title();
                //列号，从0开始
                int col = annotation.col();
                boolean required = annotation.required();
                if (required) {
                    if (StringUtils.isBlank(title)) {
                        throw new RuntimeException(String.format("第[%d]列，其值不能为空，请核对excel! [列号从0开始] ", col));
                    } else {
                        throw new RuntimeException(String.format("列名称:[%s]， 其值不能为空，请核对excel! ", title));
                    }
                }

            }
        }

        Class<?> type = field.getType();
        if (type == null || type == void.class || StringUtils.isBlank(value)) {
            return;
        }
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
            final Date javaDate = DateUtil.getJavaDate(Double.valueOf(value));
            field.set(t, javaDate);
        } else if (type == String.class) {
            field.set(t, value);
        } else {
            Constructor<?> constructor = type.getConstructor(String.class);
            field.set(t, constructor.newInstance(value));
        }
    }

    private static Pair<CellType, String> getCellValue(Cell cell) {
        if (cell == null) {
            return Pair.of(CellType.BLANK, "");
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                final String convertTime = String.valueOf(cell.getNumericCellValue());
                final Date javaDate = DateUtil.getJavaDate(Double.valueOf(convertTime));
                //return Pair.of(CellType.NUMERIC, DateUtil.getJavaDate(cell.getNumericCellValue()).toString());
                return Pair.of(CellType.NUMERIC, String.valueOf(cell.getNumericCellValue()));
            } else {
                final double numericCellValue = cell.getNumericCellValue();
                //去除末尾多余的0,避免输出科学计数法
                return Pair.of(CellType.NUMERIC, new BigDecimal(String.valueOf(numericCellValue)).stripTrailingZeros().toPlainString());
                //return Pair.of(CellType.NUMERIC, String.valueOf(numericCellValue));
                //return new BigDecimal(numericCellValue).toString();

            }
        } else if (cell.getCellType() == CellType.STRING) {
            return Pair.of(CellType.STRING, StringUtils.trimToEmpty(cell.getStringCellValue()));
        } else if (cell.getCellType() == CellType.FORMULA) {
            return Pair.of(CellType.FORMULA, StringUtils.trimToEmpty(cell.getCellFormula()));
        } else if (cell.getCellType() == CellType.BLANK) {
            return Pair.of(CellType.BLANK, "");
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return Pair.of(CellType.BOOLEAN, String.valueOf(cell.getBooleanCellValue()));
        } else if (cell.getCellType() == CellType.ERROR) {
            return Pair.of(CellType._NONE, "ERROR");
        } else {
            return Pair.of(CellType._NONE, cell.toString().trim());
        }

    }

    public static <T> void writeExcel(List<T> dataList, Class<T> cls, String path) {
        Field[] fields = cls.getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields)
                .filter(field -> {
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (annotation != null && StringUtils.isNotBlank(annotation.title())) {
                        field.setAccessible(true);
                        return true;
                    }
                    return false;
                }).sorted(Comparator.comparing(field -> {
                    int col = 0;
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (annotation != null) {
                        col = annotation.col();
                    }
                    return col;
                })).collect(Collectors.toList());

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        AtomicInteger ai = new AtomicInteger();
        {
            Row row = sheet.createRow(ai.getAndIncrement());
            AtomicInteger aj = new AtomicInteger();
            //写入头部
            fieldList.forEach(field -> {
                ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                String columnName = "";
                if (annotation != null) {
                    columnName = annotation.title();
                }
                Cell cell = row.createCell(aj.getAndIncrement());

                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);

                Font font = wb.createFont();
                font.setBold(true);
                cellStyle.setFont(font);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(columnName);
            });
        }
        if (CollectionUtils.isNotEmpty(dataList)) {
            dataList.forEach(t -> {
                Row row1 = sheet.createRow(ai.getAndIncrement());
                AtomicInteger aj = new AtomicInteger();
                fieldList.forEach(field -> {
                    Class<?> type = field.getType();
                    Object value = "";
                    try {
                        value = field.get(t);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                    Cell cell = row1.createCell(aj.getAndIncrement());
                    CellStyle cellStyle = wb.createCellStyle();
                    if (value != null) {
                        if (type == Date.class) {
                            cellStyle.setDataFormat(wb.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue((Date) value);
                        }
                        else if(type == double.class || type == Double.class){
                            cell.setCellValue((Double) value);
                        }
                        else if(type == int.class || type == Integer.class){
                            cell.setCellValue((Integer) value);
                        }
                        else if(type == long.class || type == Long.class){
                            cell.setCellValue((Long) value);
                        }
                        else {
                            cell.setCellValue(value.toString());
                        }

                    }
                });
            });
        }
        //冻结窗格
        wb.getSheet("Sheet1").createFreezePane(0, 1, 0, 1);

        //生成excel文件
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            wb.write(new FileOutputStream(file));
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("build excel document exception!");
        }
    }

}
