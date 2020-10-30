import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * <h1>总结java中删除文件或文件夹的7种方法</h1>
 * Created by hanqf on 2020/10/30 12:22.
 */


public class DeleteFileOrDirTests {

    @Before
    public void createMoreFiles() throws IOException {
        Files.createDirectories(Paths.get("build/data/test1/test2/test3/test4/test5/"));
        Files.write(Paths.get("build/data/test1/test2/test2.log"), "hello".getBytes());
        Files.write(Paths.get("build/data/test1/test2/test3/test3.log"), "hello".getBytes());
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
     * 基本删除方法：<p>
     * 只能删除最后的目录，如果目录中有文件或目录，则不能删除<p>
     * File类的delete()<p>
     * File类的deleteOnExit()<p>
     * Files.delete(Path path)<p>
     * Files.deleteIfExists(Path path);<p>
     * <p>
     * 它们之间的差异：<p>
     * <p>
     * 成功的返回值	 是否能判别文件夹不存在导致失败	是否能判别文件夹不为空导致失败	备注<p>
     * File类的delete()	                true	     不能(返回false)	            不能(返回false)	            传统IO<p>
     * File类的deleteOnExit()	        void	     不能，但不存在就不会去执行删除	不能(返回void)	            传统IO，这是个坑，避免使用<p>
     * Files.delete(Path path)	        void	     NoSuchFileException	    DirectoryNotEmptyException	NIO，笔者推荐使用<p>
     * Files.deleteIfExists(Path path)	true	     false	                    DirectoryNotEmptyException	NIO<p>
     * <p>
     * 建议使用java NIO的Files.delete(Path path)和Files.deleteIfExists(Path path)进行文件或文件夹的删除。<p>
     */

    //false只能告诉你失败了 ，但是没有给出任何失败的原因
    @Test
    public void testDeleteFileDir1() {
        File file = new File("build/data/dir2");
        boolean deleted = file.delete();
        System.out.println(deleted);
    }

    //void ,删除失败没有任何提示，应避免使用这个方法，就是个坑
    @Test
    public void testDeleteFileDir2() {
        File file = new File("build/data/dir2");
        file.deleteOnExit();
    }

    //如果文件不存在，抛出NoSuchFileException
    //如果文件夹里面包含文件，抛出DirectoryNotEmptyException
    @Test
    public void testDeleteFileDir3() throws IOException {
        Path path = Paths.get("build/data/dir2");
        Files.delete(path);   //返回值void
    }

    //如果文件不存在，返回false，表示删除失败(文件不存在)
    //如果文件夹里面包含文件，抛出DirectoryNotEmptyException
    @Test
    public void testDeleteFileDir4() throws IOException {
        Path path = Paths.get("build/data/dir2");
        boolean result = Files.deleteIfExists(path);
        System.out.println(result);
    }


    /**
     * 遍历文件删除
     * <p>
     * walkFileTree与FileVisitor
     * <p>
     * 使用walkFileTree方法遍历整个文件目录树，使用FileVisitor处理遍历出来的每一项文件或文件夹
     * <p>
     * FileVisitor的visitFile方法用来处理遍历结果中的“文件”，所以我们可以在这个方法里面删除文件
     * <p>
     * FileVisitor的postVisitDirectory方法，注意方法中的“post”表示“后去做……”的意思，所以用来文件都处理完成之后再去处理文件夹，
     * 所以使用这个方法删除文件夹就可以有效避免文件夹内容不为空的异常，因为在去删除文件夹之前，该文件夹里面的文件已经被删除了。
     * <p>
     * <p>
     * 我们既然可以遍历出文件夹或者文件，我们就可以在处理的过程中进行过滤。比如：
     * <p>
     * 按文件名删除文件或文件夹，参数Path里面含有文件或文件夹名称
     * <p>
     * 按文件创建时间、修改时间、文件大小等信息去删除文件，参数BasicFileAttributes 里面包含了这些文件信息。
     */
    @Test
    public void testDeleteFileDir5() throws IOException {
        Path path = Paths.get("build/data/test1/test2");

        Files.walkFileTree(path,
                new SimpleFileVisitor<Path>() {
                    // 先去遍历删除文件
                    @Override
                    public FileVisitResult visitFile(Path file,
                                                     BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        System.out.printf("文件被删除 : %s%n", file);
                        return FileVisitResult.CONTINUE;
                    }

                    // 再去遍历删除目录
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir,
                                                              IOException exc) throws IOException {
                        Files.delete(dir);
                        System.out.printf("文件夹被删除: %s%n", dir);
                        return FileVisitResult.CONTINUE;
                    }

                }
        );
    }

    /**
     * 推荐--子目录及文件
     * <p>
     * 遍历文件删除
     * <p>
     * 利用的是字符串的排序规则，从字符串排序规则上讲，“data\test1\test2”一定排在“data\test1\test2\test2.log”的前面。
     * <p>
     * 所以我们使用“sorted(Comparator.reverseOrder())”把Stream顺序颠倒一下，就达到了先删除文件，再删除文件夹的目的。
     */
    @Test
    public void testDeleteFileDir6() throws IOException {
        Path path = Paths.get("build/data/test1/test2");

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
     * 传统IO递归删除
     */
    @Test
    public void testDeleteFileDir7() throws IOException {
        File file = new File("build/data/test1/test2");
        deleteDirectoryLegacyIO(file);

    }

    private void deleteDirectoryLegacyIO(File file) {

        File[] list = file.listFiles();  //只能列出文件夹下面的一层文件或文件夹，不能列出子文件夹及其子文件
        if (list != null) {
            for (File temp : list) {     //先去递归删除子文件夹及子文件
                deleteDirectoryLegacyIO(temp);   //注意这里是递归调用
            }
        }

        if (file.delete()) {     //再删除自己本身的文件夹
            System.out.printf("删除成功 : %s%n", file);
        } else {
            System.err.printf("删除失败 : %s%n", file);
        }
    }
}
