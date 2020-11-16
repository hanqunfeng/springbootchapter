import lambda.LambdaExceptionUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <h1>文件和目录工具类</h1>
 * Created by hanqf on 2020/10/30 14:53.
 * <p>
 * 核心工具类就是java.nio.file.Files
 */


public class FileAndDirUtil {

    /**
     * <h2>读取文件到字符串</h2>
     * Created by hanqf on 2020/10/30 14:56. <br>
     * <p>
     * 一次性读取到内存，注意文件不能太大
     *
     * @param filePath 文件路径
     * @return java.lang.String
     * @author hanqf
     */
    public static String readFileToString(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * <h2>读取文件到字节流</h2>
     * Created by hanqf on 2020/10/30 15:09. <br>
     * <p>
     * 一次性读取到内存，注意文件不能太大
     *
     * @param filePath 文件路径
     * @return byte[]
     * @author hanqf
     */
    public static byte[] readFileToBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }


    /**
     * <h2>读取文件内容到Stream流中，按行读取</h2>
     * Created by hanqf on 2020/10/30 14:59. <br>
     * lines.forEach(System.out::println); :随机行顺序进行数据处理 <br>
     * lines.forEachOrdered(System.out::println); :按文件行顺序进行处理,但处理效率会下降<br>
     * lines.parallel().forEachOrdered(System.out::println); :利用CPU多核并行处理，适合比较大的文件<br>
     * <p>
     * List<String> collect = lines.collect(Collectors.toList()); :转换成List<String>, 要注意OutOfMemoryError
     *
     * @param filePath 文件路径
     * @return java.util.stream.Stream&lt;java.lang.String&gt; 调用后需要关闭流
     * @author hanqf
     */
    public static Stream<String> readFileToStream(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath));
    }

    /**
     * <h2>删除指定目录及其子目录</h2>
     * Created by hanqf on 2020/10/30 15:27. <br>
     *
     * @param dirPath 目录路径
     * @author hanqf
     */
    public static void deleteDirAndSub(String dirPath) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(dirPath))) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(LambdaExceptionUtil.wrapConsumer(file -> {
                        Files.delete(file);
                        System.out.printf("删除文件成功：%s%n", file.toString());
                    }));
        }
    }


    /**
     * <h2>创建目录及其子目录</h2>
     * Created by hanqf on 2020/10/30 15:32. <br>
     * <p>
     * 如果被创建文件夹的父文件夹不存在，就创建它 <br>
     * 如果被创建的文件夹已经存在，就是用已经存在的文件夹，不会重复创建，没有异常抛出 <br>
     * 如果因为磁盘IO出现异常，则抛出IOException. <br>
     *
     * @param dirPath
     * @return java.nio.file.Path
     * @author hanqf
     */
    public static Path createDirs(String dirPath) throws IOException {
        return Files.createDirectories(Paths.get(dirPath));
    }


    /**
     * <h2>复制文件</h2>
     * Created by hanqf on 2020/10/30 15:38. <br>
     * <p>
     * StandardCopyOption.REPLACE_EXISTING :目标文件存在就替换它 <br>
     * StandardCopyOption.COPY_ATTRIBUTES  :copy文件的属性，最近修改时间，最近访问时间等 <br>
     *
     * @param sourceFilePath 源文件路径
     * @param destFilePath   目标文件路径
     * @param options        文件属性
     * @return java.nio.file.Path 目标文件路径
     * @author hanqf
     */
    public static Path copyFile(String sourceFilePath, String destFilePath, CopyOption... options) throws IOException {
        Path fromFile = Paths.get(sourceFilePath);
        Path toFile = Paths.get(destFilePath);

        //如果源文件存在，但是目标文件路径不存在，则先创建目标文件路径
        if (Files.exists(fromFile) && Files.notExists(toFile.getParent())) {
            Files.createDirectories(toFile.getParent());
        }

        return Files.copy(fromFile, toFile, options);
    }

    /**
     * <h2>剪切文件到指定文件夹</h2>
     * Created by hanqf on 2020/10/30 15:49. <br>
     * <p>
     * StandardCopyOption.REPLACE_EXISTING :目标文件存在就替换它 <br>
     * StandardCopyOption.COPY_ATTRIBUTES  :copy文件的属性，最近修改时间，最近访问时间等 <br>
     *
     * @param sourceFilePath 源文件路径
     * @param destDirPath    目标目录路径
     * @param options        文件属性
     * @return java.nio.file.Path 目标文件路径
     * @author hanqf
     */
    public static Path moveFile(String sourceFilePath, String destDirPath, CopyOption... options) throws IOException {
        Path fromFile = Paths.get(sourceFilePath); //文件
        Path toDir = Paths.get(destDirPath); //目标文件夹
        //如果源文件存在，但是目标路径不存在，则先创建目标文件路径
        if (Files.exists(fromFile) && Files.notExists(toDir)) {
            Files.createDirectories(toDir);
        }
        //resolve函数是解析toDir路径与参数文件名进行合并为一个新的文件路径
        return Files.move(fromFile, toDir.resolve(fromFile.getFileName()), options);
    }

    /**
     * <h2>文件重命名</h2>
     * Created by hanqf on 2020/10/30 15:56. <br>
     * <p>
     * StandardCopyOption.REPLACE_EXISTING :目标文件存在就替换它 <br>
     * StandardCopyOption.COPY_ATTRIBUTES  :copy文件的属性，最近修改时间，最近访问时间等 <br>
     *
     * @param sourceFilePath 源文件路径
     * @param newName        新文件名称
     * @param options        文件属性
     * @return java.nio.file.Path 新文件路径
     * @author hanqf
     */
    public static Path renameFile(String sourceFilePath, String newName, CopyOption... options) throws IOException {
        Path source = Paths.get(sourceFilePath);

        //resolveSibling的功能是获取文件的父路径，与给定参数，即新的文件名称组合成一个新的文件路径
        return Files.move(source, source.resolveSibling(newName), options);
    }

    /**
     * <h2>将字符串写入文件</h2>
     * Created by hanqf on 2020/10/30 16:15. <br>
     * <p>
     * StandardOpenOption.APPEND :追加
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param options  写入属性
     * @return java.nio.file.Path 新文件路径
     * @author hanqf
     */
    public static Path writerFileByString(String filePath, String content, OpenOption... options) throws IOException {
        Path path = Paths.get(filePath);

        //如果文件路径不存在，则先创建文件路径
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        // 从JDK1.7开始提供的方法
        return Files.write(path, content.getBytes(StandardCharsets.UTF_8), options);
    }

    /**
     * <h2>将字符串list写入文件</h2>
     * Created by hanqf on 2020/10/30 16:24. <br>
     *
     * @param filePath 文件路径
     * @param lines    写入内容--行数据
     * @param options  写入属性
     * @return java.nio.file.Path 新文件路径
     * @author hanqf
     */
    public static Path writerFileByListString(String filePath, List<String> lines, OpenOption... options) throws IOException {
        Path path = Paths.get(filePath);

        //如果文件路径不存在，则先创建文件路径
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        // 从JDK1.8开始提供的方法
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }

        }

        return path;
    }

    /**
     * <h2>将StreamString写入文件</h2>
     * Created by hanqf on 2020/10/30 16:43. <br>
     *
     * @param filePath 文件路径
     * @param lines    写入内容--行数据
     * @param options  写入属性
     * @return java.nio.file.Path 新文件路径
     * @author hanqf
     */
    public static Path writerFileByStreamString(String filePath, Stream<String> lines, OpenOption... options) throws IOException {
        Path path = Paths.get(filePath);

        //如果文件路径不存在，则先创建文件路径
        if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        // 从JDK1.8开始提供的方法
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options)) {
            //并行写入，适合大文件
            lines.parallel().forEachOrdered(LambdaExceptionUtil.wrapConsumer(line -> {
                writer.write(line);
                writer.newLine();
            }));


            //lines.parallel().forEachOrdered(LambdaConsumer.wrapperWithExceptionToDo(line -> {
            //    writer.write((String) line);
            //    writer.newLine();
            //}, new Class[]{IOException.class}, (line, e) -> {
            //    System.err.println(e.getMessage());
            //}));

            //关闭流
            lines.close();

        }

        return path;
    }

    public static void main(String[] args) throws IOException {
        //try-with-resources语法,不用手动的编码关闭流
        try (Stream<String> stringStream = FileAndDirUtil.readFileToStream("/Users/hanqf/Desktop/1.html")) {
            FileAndDirUtil.writerFileByStreamString("/Users/hanqf/Desktop/2.html", stringStream);
        }
        Supplier<FileAndDirUtil> aNew = FileAndDirUtil::new;


    }

}
