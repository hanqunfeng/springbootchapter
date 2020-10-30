import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * <h1>总结java从文件中读取数据的6种方法</h1>
 * Created by hanqf on 2020/10/30 11:20.
 * <p>
 * <p>
 * Scanner(Java 1.5) 按行读数据及String、Int类型等按分隔符读数据。
 * <p>
 * Files.lines, 返回Stream(Java 8) 流式数据处理，按行读取
 * <p>
 * Files.readAllLines, 返回List<String>(Java 8)
 * <p>
 * Files.readString, 读取String(Java 11), 文件最大 2G.
 * <p>
 * Files.readAllBytes, 读取byte[](Java 7), 文件最大 2G.
 * <p>
 * BufferedReader, 经典方式 (Java 1.1 -> forever)
 */
public class ReadFileTests {


    @Before
    public void createMoreFiles() throws IOException {
        Files.createDirectories(Paths.get("build/data/"));
        Files.write(Paths.get("build/data/newFile.txt"), "hello\nworld\n天天快了\n".getBytes());
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
     * Scanner，从JDK1.5开始提供的API
     * <p>
     * 特点是可以按行读取、按分割符去读取文件数据，既可以读取String类型，也可以读取Int类型、Long类型等基础数据类型的数据。
     */

    @Test
    public void testReadFile1() throws IOException {

        String fileName = "build/data/newFile.txt";

        try (Scanner sc = new Scanner(new FileReader(fileName))) {
            while (sc.hasNextLine()) {  //按行读取字符串
                String line = sc.nextLine();
                System.out.println(line);
            }
        }

        ////文件内容：Hello World|Hello Zimug
        //try (Scanner sc = new Scanner(new FileReader(fileName))) {
        //    sc.useDelimiter("\\|");  //分隔符
        //    while (sc.hasNext()) {   //按分隔符读取字符串
        //        String str = sc.next();
        //        System.out.println(str);
        //    }
        //}
        //
        ////sc.hasNextInt() 、hasNextFloat() 、基础数据类型等等等等。
        ////文件内容：1|2
        //fileName = "build/newFile5.txt";
        //try (Scanner sc = new Scanner(new FileReader(fileName))) {
        //    sc.useDelimiter("\\|");  //分隔符
        //    while (sc.hasNextInt()) {   //按分隔符读取Int
        //        int intValue = sc.nextInt();
        //        System.out.println(intValue);
        //    }
        //}
    }

    /**
     * 推荐--按行读取
     * <p>
     * Files.lines Java8的Stream
     * <p>
     * 适合按行读取
     */
    @Test
    public void testReadFile2() throws IOException {
        String fileName = "build/data/newFile.txt";

        // 读取文件内容到Stream流中，按行读取
        //try-with-resources语法,不用手动的编码关闭流
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            // 随机行顺序进行数据处理
            lines.forEach(ele -> {
                System.out.println(ele);
            });


            // 按文件行顺序进行处理,但处理效率会下降
            //lines.forEachOrdered(System.out::println);

            //或者利用CPU多核的能力，进行数据的并行处理parallel()，适合比较大的文件。
            // 按文件行顺序进行处理
            //lines.parallel().forEachOrdered(System.out::println);


            // 转换成List<String>, 这意味着你要将所有的数据一次性加载到内存，要注意java.lang.OutOfMemoryError: Java heap space
            //List<String> collect = lines.collect(Collectors.toList());
        }


    }

    /**
     * 推荐--读取全部
     * <p>
     * Files.readAllLines Java8
     * <p>
     * 适合一次性读取全部内容到内存
     */
    @Test
    public void testReadFile3() throws IOException {
        String fileName = "build/data/newFile.txt";

        // 转换成List<String>, 要注意java.lang.OutOfMemoryError: Java heap space
        List<String> lines = Files.readAllLines(Paths.get(fileName),
                StandardCharsets.UTF_8);
        lines.forEach(System.out::println);

    }


    /**
     * Files.readString(JDK 11)
     * <p>
     * 一次性读取一个文件的方法。文件不能超过2G，同时要注意你的服务器及JVM内存。这种方法适合快速读取小文本文件。
     * <p>
     * 适合一次性读取全部内容到内存
     */
    @Test
    public void testReadFile4() throws IOException {
        String fileName = "build/data/newFile.txt";

        // java 11 开始提供的方法，读取文件不能超过2G，与你的内存息息相关
        //String s = Files.readString(Paths.get(fileName));
    }

    /**
     * Files.readAllBytes() JDK7
     * <p>
     * 适合一次性读取全部内容到内存
     */
    @Test
    public void testReadFile5() throws IOException {
        String fileName = "build/data/newFile.txt";

        //如果是JDK11用上面的方法，如果不是用这个方法也很容易
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));

        String content = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(content);
    }


    /**
     * 管道流
     */
    @Test
    public void testReadFile6() throws IOException {
        String fileName = "build/data/newFile.txt";

        // 带缓冲的流读取，默认缓冲区8k
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }

        //java 8中这样写也可以
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }


        //想从文件中读取java Object就可以使用下面的代码，前提是文件中的数据是ObjectOutputStream写入的数据，才可以用ObjectInputStream来读取。
        //try (FileInputStream fis = new FileInputStream(fileName);
        //     ObjectInputStream ois = new ObjectInputStream(fis)){
        //    ois.readObject();
        //}

    }
}
