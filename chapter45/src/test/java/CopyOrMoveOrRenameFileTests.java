import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * <h1>总结java中文件拷贝剪切的5种方式</h1>
 * Created by hanqf on 2020/10/30 12:51.
 * <p>
 * 文件拷贝：将文件从一个文件夹复制到另一个文件夹
 * 文件剪切：将文件从当前文件夹，移动到另一个文件夹
 * 文件重命名：将文件在当前文件夹下面改名（也可以理解为将文件剪切为当前文件夹下面的另一个文件）
 */


public class CopyOrMoveOrRenameFileTests {

    @Before
    public void createMoreFiles() throws IOException {
        Files.createDirectories(Paths.get("build/data/test1/"));
        Files.createDirectories(Paths.get("build/data/test2/"));
        Files.write(Paths.get("build/data/test1/newFile.txt"), "hello".getBytes());
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
     * 传统IO中的文件copy的方法，使用输入输出流，实际上就是重新创建并写入一个文件。
     * <p>
     * 如果目标文件已经存在，就覆盖掉它，重新创建一个文件并写入数据。
     * <p>
     * 这种方式不够友好，覆盖掉原有文件没有给出任何提示，有可能导致原有数据的丢失。
     */

    @Test
    public void testCopyFile1() throws IOException {

        File fromFile = new File("build/data/test1/newFile.txt");
        File toFile = new File("build/data/test2/copyedFile.txt");

        try (InputStream inStream = new FileInputStream(fromFile);
             OutputStream outStream = new FileOutputStream(toFile);) {

            byte[] buffer = new byte[1024];

            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
                outStream.flush();
            }

        }
    }

    /**
     * 推荐
     * <p>
     * Java NIO中文件copy的方法，使用方式简单。
     * <p>
     * 当目标文件已经存在的时候会抛出FileAlreadyExistsException ，当源文件不存在的时候抛出NoSuchFileException，针对不同的异常场景给出不同的Exception，更有利于我们写出健壮性更好的程序。
     */
    @Test
    public void testCopyFile2() throws IOException {
        Path fromFile = Paths.get("build/data/test1/newFile.txt");
        Path toFile = Paths.get("build/data/test2/copyedFile.txt");

        Files.copy(fromFile, toFile);

        //如果目标文件存在就替换它
        //Files.copy(fromFile, toFile, StandardCopyOption.REPLACE_EXISTING);

        //StandardCopyOption.COPY_ATTRIBUTES copy文件的属性，最近修改时间，最近访问时间等信息，不仅copy文件的内容，连文件附带的属性一并复制
        //CopyOption[] options = {
        //        StandardCopyOption.REPLACE_EXISTING,
        //        StandardCopyOption.COPY_ATTRIBUTES //copy文件的属性，最近修改时间，最近访问时间等
        //};
        //Files.copy(fromFile, toFile, options);
    }

    /**
     * 推荐
     * <p>
     * 文件重命名
     * <p>
     * NIO中可以使用Files.move方法在同一个文件夹内移动文件，并更换名字。
     * <p>
     * 当目标文件已经存在的时候，同样会有FileAlreadyExistsException，也同样可以使用StandardCopyOption去处理该异常。
     */
    @Test
    public void testRenameFile() throws IOException {
        Path source = Paths.get("build/data/test1/newFile.txt");
        Path target = Paths.get("build/data/test1/renameFile.txt");

        //REPLACE_EXISTING文件存在就替换它
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

        //这种写法就更加简单，兼容性更好
        //resolveSibling的功能是获取文件的父路径，与给定参数，即新的文件名称组合成一个新的文件路径
        //Files.move(source, source.resolveSibling("renameFile.txt"));
    }

    /**
     * 传统IO中使用File类的renameTo方法重命名，失败了就返回false，没有任何异常抛出
     */
    @Test
    public void testRenameFile2() throws IOException {

        File source = new File("build/data/test1/newFile.txt");
        boolean succeeded = source.renameTo(new File("build/data/test1/renameFile.txt"));
        System.out.println(succeeded);  //失败了false，没有异常
    }


    /**
     * 推荐
     * <p>
     * 文件剪切
     * <p>
     * resolve函数是解析anotherDir路径与参数文件名进行合并为一个新的文件路径。
     */
    @Test
    public void testMoveFile() throws IOException {

        Path fromFile = Paths.get("build/data/test1/newFile.txt"); //文件
        Path anotherDir = Paths.get("build/data/test2"); //目标文件夹

        Files.move(fromFile, anotherDir.resolve(fromFile.getFileName()),
                StandardCopyOption.REPLACE_EXISTING);
    }
}
