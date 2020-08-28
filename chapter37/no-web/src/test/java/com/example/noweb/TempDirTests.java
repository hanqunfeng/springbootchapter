package com.example.noweb;

import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>临时目录测试用例</p>
 * Created by hanqf on 2020/8/28 10:22.
 *
 * 测试方法或类执行完毕，将删除在测试执行过程中创建的目录及其内容。
 *
 * 注意：同一次测试过程中，@TempDir不能创建相同的文件
 */


public class TempDirTests {

    /**
     * 声明为static是为了共享变量
     */
    @TempDir
    static Path TEMP_DIR;
    /**
     * must not be private
     */
    @TempDir
    Path tempDir;

    /**
     * 当前类中所有测试方法执行前执行一次,相当于JUnit4中的 @BeforeClass
     */
    @BeforeAll
    static void beforeAll() {
        System.out.println("beforeAll...");
        assertTrue(Files.isDirectory(TEMP_DIR));
    }

    /**
     * 当前类中所有测试方法执行前执行一次,相当于JUnit4中的 @AfterClass
     */
    @AfterAll
    static void afterAll() {
        System.out.println("afterAll...");
    }

    /**
     * <p>讲内容写入指定文件</p>
     *
     * @param path    文件路径
     * @param content 写入内容
     * @author hanqf
     * 2020/8/28 10:32
     */
    public void writeTo(String path, String content) throws IOException {
        Path target = Paths.get(path);
        //文件存在则抛出异常
        if (Files.exists(target)) {
            throw new IOException("file already exists");
        }
        //将内容写入文件
        Files.copy(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), target);
    }

    /**
     * 每个测试方法执行前执行,相当于JUnit4中的 @Before
     */
    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach...");
        assertTrue(Files.isDirectory(this.tempDir));
    }

    @Test
    void writesContentToFile(@TempDir Path tempDir) throws IOException {

        System.out.println("writesContentToFile");
        // 创建临时文件路径，此时并没有创建真正的文件，注意：同一次测试过程中，tempDir不能创建相同的文件
        Path output = tempDir.resolve("output.txt");
        // 将内容写入文件
        writeTo(output.toString(), "test");
        // assert
        assertAll(
                () -> assertTrue(Files.exists(output)), //验证文件是否存在
                () -> assertLinesMatch(List.of("test"), Files.readAllLines(output)) //验证文件内容是否与给定内容匹配
        );

        File file = output.toFile();
        System.out.println(file.getAbsolutePath());
    }

    @Test
    void throwsErrorWhenTargetFileExists() throws IOException {
        System.out.println("throwsErrorWhenTargetFileExists");
        // 创建临时文，此时已经创建好文件了，所以下面调用写入文件方法时会抛出异常
        Path output = Files.createFile(
                tempDir.resolve("output.txt")
        );
        //assert
        //验证是否抛出指定异常
        IOException expectedException = assertThrows(IOException.class, () -> writeTo(output.toString(), "test"));
        //验证异常内容与给定内容是否一致
        assertEquals("file already exists", expectedException.getMessage());
    }

    @RepeatedTest(3)
    void throwsErrorWhenTargetFileExists(RepetitionInfo repetitionInfo) throws IOException {
        // 创建临时文，此时已经创建好文件了，所以下面调用写入文件方法时会抛出异常
        // 注意：共享变量TEMP_DIR 的文件名必须全局唯一，否则会报FileAlreadyExistsException，所以这里前面加了一个计数器
        Path output = Files.createFile(
                TEMP_DIR.resolve(repetitionInfo.getCurrentRepetition() + "_output.txt")
        );
        //assert
        //验证是否抛出指定异常
        IOException expectedException = assertThrows(IOException.class, () -> writeTo(output.toString(), "test"));
        //验证异常内容与给定内容是否一致
        assertEquals("file already exists", expectedException.getMessage());
    }

    /**
     * 每个测试方法执行后执行,相当于JUnit4中的 @After
     */
    @AfterEach
    void afterEach() {
        System.out.println("afterEach...");
    }
}
