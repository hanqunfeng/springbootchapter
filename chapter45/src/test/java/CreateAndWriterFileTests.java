import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * <h1>总结java中创建并写文件的五种方式</h1>
 * Created by hanqf on 2020/10/30 10:11.
 * <p>
 * <p>
 * Files.newBufferedWriter(Java 8)
 * <p>
 * Files.write(Java 7 推荐)
 * <p>
 * PrintWriter
 * <p>
 * File.createNewFile
 * <p>s
 * FileOutputStream.write(byte[] b) 管道流
 */
public class CreateAndWriterFileTests {

    @Before
    public void createMoreFiles() throws IOException {
        Files.createDirectories(Paths.get("build/data/"));
    }

    @After
    public void deleteDir() throws IOException {
        Path path = Paths.get("build/data/");

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            System.out.printf("删除文件成功：%s%n", file.toString());
                        } catch (IOException e) {
                            System.err.printf("无法删除的路径 %s%n%s", file, e);
                        }
                    });
        }
    }

    /**
     * 推荐
     * <p>
     * Java 8 Files.newBufferedWriter
     * <p>
     * 用于写入文本内容
     * <p>
     * 适合分行写入
     */
    @Test
    public void testCreateFile1() throws IOException {
        String fileName = "build/data/newFile.txt";

        //Path即可以表示文件，也可以表示目录，Path提供了很多获取文件或目录属性的方法
        Path path = Paths.get(fileName);

        // 使用newBufferedWriter创建文件并写文件
        // 这里使用了try-with-resources方法来关闭流，不用手动关闭
        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            //可以根据需要写入多行数据
            writer.write("Hello World -创建文件!!\n");
            writer.write("Hello World -创建文件!!\n");
            writer.newLine();
        }

        //追加写模式
        try (BufferedWriter writer =
                     Files.newBufferedWriter(path,
                             StandardCharsets.UTF_8,
                             StandardOpenOption.APPEND)) {
            writer.write("你好，张三!!");
            //作用等同于 \n
            writer.newLine();
            writer.write("你好，张三!!");
        }
    }


    /**
     * 推荐
     * <p>
     * Java 7 Files.write
     * <p>
     * 适合一次性写入全部内容
     * <p>
     * 用于写入byte[] 字节流
     */
    @Test
    public void testCreateFile2() throws IOException {
        String fileName = "build/data/newFile2.txt";

        Path path = Paths.get(fileName);
        // 从JDK1.7开始提供的方法
        // 使用Files.write创建一个文件并写入
        Files.write(path,
                "Hello World -创建文件!!".getBytes(StandardCharsets.UTF_8));

        // 追加写模式
        Files.write(path,
                "你好，张三!!".getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND);

    }

    /**
     * JDK1.5 PrintWriter
     * <p>
     * 适合写入文本和对象，不适合文件追加
     */

    @Test
    public void testCreateFile3() throws IOException {
        String fileName = "build/data/newFile3.txt";

        // JSD 1.5开始就已经存在的方法
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            writer.println("Hello World -创建文件!!");
            writer.println("你好，张三!!");

            //写入对象
            //writer.println(object);
        }

        // Java 10进行了改进，支持使用StandardCharsets指定字符集
        /*try (PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8)) {

          writer.println("first line!");
          writer.println("second line!");

       } */
    }

    /**
     * File.createNewFile()
     * <p>
     * createNewFile()方法的功能相对就比较纯粹，只是创建文件不做文件写入操作。 返回true表示文件成功，返回 false表示文件已经存在.
     * <p>
     * 可以配合FileWriter 来完成文件的写操作。
     */
    @Test
    public void testCreateFile4() throws IOException {
        String fileName = "build/data/newFile4.txt";

        File file = new File(fileName);

        // 返回true表示文件成功
        // false 表示文件已经存在
        if (file.createNewFile()) {
            System.out.println("创建文件成功！");
        } else {
            System.out.println("文件已经存在不需要重复创建");
        }

        // 使用FileWriter写文件
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Hello World -创建文件!!\n");
            writer.write("你好，张三!!\n");
        }

        // 使用FileWriter 追加写文件
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write("Hello World -创建文件!!\n");
            writer.write("你好，张三!!\n");
        }



    }


    /**
     * 管道流嵌套
     * <p>
     * 这种方式更加灵活，适合写入任意类型数据
     */
    @Test
    public void testCreateFile5() throws IOException {
        String fileName = "build/data/newFile5.txt";
        try (FileOutputStream fos = new FileOutputStream(fileName);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write("Hello World -创建文件!!\n");
            bw.write("你好，张三!!\n");
            bw.flush();
        }

        //追加写入
        try (FileOutputStream fos = new FileOutputStream(fileName, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write("Hello World -创建文件!!\n");
            bw.write("你好，张三!!\n");
            bw.flush();
        }

        //写入对象
        //try (FileOutputStream fos = new FileOutputStream(fileName,true);
        //     ObjectOutputStream oos = new ObjectOutputStream(fos)) {
        //   oos.writeObject(object);
        //   oos.flush();
        //}
    }
}
