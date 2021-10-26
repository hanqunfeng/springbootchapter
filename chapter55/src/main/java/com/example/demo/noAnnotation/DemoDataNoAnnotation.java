package com.example.demo.noAnnotation;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/10/25 11:22.
 */


@Data
public class DemoDataNoAnnotation {
    private static final String FILE_NAME = "/Users/hanqf/Desktop/demo.xlsx";

    private String string;
    private Date date;
    private Double doubleData;

    //没有这一列
    private String noDate;

    private static void read1() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取3000条数据 然后返回过来 直接调用使用数据就行
        // 此时字段可以不加注解，每一个字段按顺序匹配
        EasyExcel.read(FILE_NAME, DemoDataNoAnnotation.class, new PageReadListener<DemoDataNoAnnotation>(dataList -> {
                    for (DemoDataNoAnnotation demoDataNoAnnotation : dataList) {
                        System.out.println("读取到一条数据:" + demoDataNoAnnotation);
                    }
                    System.out.println("本次读取行数：" + dataList.size());
                }))
                //.doReadAll() //读取全部sheet
                //读取指定的sheet,默认0
                .sheet(0)
                //默认1
                .headRowNumber(1)
                .doRead();
    }

    private static void read2() {
        // 写法2：
        // 匿名内部类 不用额外写一个DemoDataListener
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(FILE_NAME, DemoDataNoAnnotation.class, new ReadListener<DemoDataNoAnnotation>() {
            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 3000;
            /**
             *临时存储
             */
            private List<DemoDataNoAnnotation> cachedData = new ArrayList<>(BATCH_COUNT);

            @Override
            public void invoke(DemoDataNoAnnotation data, AnalysisContext context) {
                cachedData.add(data);
                if (cachedData.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedData = new ArrayList<>(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
                System.out.println("所有数据解析完成！");
            }

            /**
             * 加上存储数据库
             */
            private void saveData() {
                cachedData.forEach(System.out::println);
                System.out.println(String.format("%d条数据，开始存储数据库！", cachedData.size()));
            }
        }).sheet(0).doRead();
    }


    private static void read3() {
        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法3：
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(FILE_NAME, DemoDataNoAnnotation.class, new DemoDataListener()).sheet(0).doRead();

    }

    private static void read4() {
        // 写法4：
        // 一个文件一个reader
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(FILE_NAME, DemoDataNoAnnotation.class, new DemoDataListener()).build();
            // 构建一个sheet 这里可以指定名字或者no
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            // 读取一个sheet
            excelReader.read(readSheet);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }

    }

    /**
     * 读取多个sheet
    */
    public static void repeatedRead() {
        // 读取全部sheet
        // 这里需要注意 DemoDataListener的doAfterAllAnalysed 会在每个sheet读取完毕后调用一次。然后所有sheet都会往同一个DemoDataListener里面写
        EasyExcel.read(FILE_NAME, DemoDataNoAnnotation.class, new DemoDataListener()).doReadAll();


        System.out.println("###################################################################");
        // 读取部分sheet
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(FILE_NAME).build();

            // 这里为了简单 所以注册了 同样的head 和Listener 自己使用功能必须不同的Listener
            ReadSheet readSheet1 =
                    EasyExcel.readSheet(0).head(DemoDataNoAnnotation.class).registerReadListener(new DemoDataListener()).build();
            ReadSheet readSheet2 =
                    EasyExcel.readSheet(1).head(DemoDataNoAnnotation.class).registerReadListener(new DemoDataListener()).build();
            // 这里注意 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
            excelReader.read(readSheet1, readSheet2);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }

    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
    private static void synchronousRead() {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<DemoDataNoAnnotation> list = EasyExcel.read(FILE_NAME).head(DemoDataNoAnnotation.class).sheet().doReadSync();
        for (DemoDataNoAnnotation data : list) {
            System.out.println("读取到数据:"+ data);
        }
        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
        List<Map<Integer, String>> listMap = EasyExcel.read(FILE_NAME).sheet().doReadSync();
        for (Map<Integer, String> data : listMap) {
            // 返回每条数据的键值对 表示所在的列 和所在列的值
            System.out.println("读取到数据:"+ data);
        }
    }




    public static void main(String[] args) {

        read1();
        //read2();
        //read3();
        //read4();

        //repeatedRead();

        //synchronousRead();

    }
}
