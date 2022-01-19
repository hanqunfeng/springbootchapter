package com.example;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2021/12/12 14:04.
 */


public class IOUtilTest {

    @Test
    public void FileUtil() {
        final Path workdir = Paths.get("/Users/hanqf/Desktop/fileUtilDir/test");
        //创建目录结构
        FileUtil.mkdir(workdir);

        Path src = Paths.get(workdir + "/a.txt");

        //写文件
        FileUtil.writeUtf8String("好好学习，天天向上", src.toFile());

        Path dest = Paths.get(workdir + "/b.txt");
        //复制文件
        FileUtil.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

        //读文件
        System.out.println(FileUtil.readUtf8String(dest.toFile()));

        //创建文件，目录不存在会同时创建
        final File touchFile = Paths.get(workdir + "/test2/123.txt").toFile();
        FileUtil.touch(touchFile);
        //写文件
        final List<String> stringList = Arrays.asList("白日依山尽，", "黄河入海流。", "欲穷千里目，", "更上一层楼。");
        FileUtil.writeUtf8Lines(stringList, touchFile);
        //读文件
        FileUtil.readUtf8Lines(touchFile).forEach(System.out::println);

        //文件类型
        final String type = FileTypeUtil.getType(touchFile);
        System.out.println(type);

        //删除目录
        FileUtil.del(workdir);

    }
}
