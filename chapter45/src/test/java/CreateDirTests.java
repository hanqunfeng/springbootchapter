import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * <h1>总结java创建文件夹的4种方法及其优缺点</h1>
 * Created by hanqf on 2020/10/30 11:47.
 */

public class CreateDirTests {

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
     * 传统API创建文件夹方式
     * <p>
     * file.mkdir()创建文件夹成功返回true，失败返回false。如果被创建文件夹的父文件夹不存在也返回false.没有异常抛出。
     * <p>
     * file.mkdirs()创建文件夹连同该文件夹的父文件夹，如果创建成功返回true，创建失败返回false。创建失败同样没有异常抛出。
     */
    @Test
    public void testCreateDir1And2() {
        String dirStr = "build/data/dir";
        File directory = new File(dirStr);

        //1.mkdir
        boolean hasSucceeded = directory.mkdir();
        System.out.println("创建文件夹结果（不含父文件夹）：" + hasSucceeded);

        //2.mkdirs
        hasSucceeded = directory.mkdirs();
        System.out.println("创建文件夹结果（包含父文件夹）：" + hasSucceeded);

    }

    /**
     * 推荐
     * <p>
     * Java NIO创建文件夹
     */
    @Test
    public void testCreateDir3And4() {
        String dirStr = "build/data/dir2";

        Path path = Paths.get(dirStr);

        //3.mkdir
        //如果被创建文件夹的父文件夹不存在，则抛出NoSuchFileException.
        //如果被创建的文件夹已经存在，则抛出FileAlreadyExistsException.
        //如果因为磁盘IO出现异常，则抛出IOException.
        try {
            Path pathCreate = Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4.mkdirs
        //如果被创建文件夹的父文件夹不存在，就创建它
        //如果被创建的文件夹已经存在，就是用已经存在的文件夹，不会重复创建，没有异常抛出
        //如果因为磁盘IO出现异常，则抛出IOException.
        try {
            Path pathCreate = Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
