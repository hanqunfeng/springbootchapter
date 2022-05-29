package org.example;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.Test;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Unit test for simple App.
 */
public class AppTest {

    Path imagePath = new File("testFileDir/test.png").toPath();
    Path docxPath = new File("testFileDir/test.docx").toPath();
    Path txtPath = new File("testFileDir/test.txt").toPath();
    Path htmlPath = new File("testFileDir/test.html").toPath();
    Path mdPath = new File("testFileDir/test.md").toPath();
    Path pdfPath = new File("testFileDir/test.pdf").toPath();

    /**
     * 使用Files.probeContentType
     * Java1.7开始，提供了用于解决MIME类型的方法 Files.probeContentType
     * <p>
     * 此方法利用已安装的FileTypeDetector实现来探查MIME类型。它调用每个实现的 probeContentType来解析类型。
     * 但是，其默认实现是特定于操作系统的，并且可能会失败，具体取决于我们使用的操作系统。
     * <p>
     * 结论：根据文件扩展名判断。
     */
    @Test
    public void probeContentType() throws IOException {
        System.out.println(imagePath.getFileName() + "#" + Files.probeContentType(imagePath)); // image/png
        System.out.println(docxPath.getFileName() + "#" + Files.probeContentType(docxPath));  // null
        System.out.println(txtPath.getFileName() + "#" + Files.probeContentType(txtPath));  // text/plain
        System.out.println(htmlPath.getFileName() + "#" + Files.probeContentType(htmlPath));  // text/html
        System.out.println(mdPath.getFileName() + "#" + Files.probeContentType(mdPath));  // null
        System.out.println(pdfPath.getFileName() + "#" + Files.probeContentType(pdfPath));  // application/pdf
    }


    /**
     * 该方法利用内部的FileNameMap来判断MIME类型。
     * <p>
     * 结论：根据文件扩展名判断。
     */
    @Test
    public void guessContentTypeFromName() {
        System.out.println(imagePath.getFileName() + "#" + URLConnection.guessContentTypeFromName(imagePath.toFile().getName())); // image/png
        System.out.println(docxPath.getFileName() + "#" + URLConnection.guessContentTypeFromName(docxPath.toFile().getName()));  // null
        System.out.println(txtPath.getFileName() + "#" + URLConnection.guessContentTypeFromName(txtPath.toFile().getName()));  // text/plain
        System.out.println(htmlPath.getFileName() + "#" + URLConnection.guessContentTypeFromName(htmlPath.toFile().getName()));  // text/html
        System.out.println(mdPath.getFileName() + "#" + URLConnection.guessContentTypeFromName(mdPath.toFile().getName()));  // null
        System.out.println(pdfPath.getFileName() + "#" + URLConnection.guessContentTypeFromName(pdfPath.toFile().getName()));  // application/pdf
    }

    /**
     * 结论：根据文件流中前几个字符判断。但是，这种方法的主要缺点是速度非常慢。
     */
    @Test
    public void getContentType() throws IOException {
        System.out.println(imagePath.getFileName() + "#" + imagePath.toFile().toURI().toURL().openConnection().getContentType()); // image/png
        System.out.println(docxPath.getFileName() + "#" + docxPath.toFile().toURI().toURL().openConnection().getContentType());  // content/unknown
        System.out.println(txtPath.getFileName() + "#" + txtPath.toFile().toURI().toURL().openConnection().getContentType());  // text/plain
        System.out.println(htmlPath.getFileName() + "#" + htmlPath.toFile().toURI().toURL().openConnection().getContentType());  // text/html
        System.out.println(mdPath.getFileName() + "#" + mdPath.toFile().toURI().toURL().openConnection().getContentType());  // content/unknown
        System.out.println(pdfPath.getFileName() + "#" + pdfPath.toFile().toURI().toURL().openConnection().getContentType());  // application/pdf
    }

    /**
     * 结论：根据文件流中前几个字符判断。非常不准啊
     */
    @Test
    public void guessContentTypeFromStream() throws IOException {
        System.out.println(imagePath.getFileName() + "#" + URLConnection.guessContentTypeFromStream(new BufferedInputStream(new FileInputStream(imagePath.toFile())))); // image/png
        System.out.println(docxPath.getFileName() + "#" + URLConnection.guessContentTypeFromStream(new BufferedInputStream(new FileInputStream(docxPath.toFile()))));  // null
        System.out.println(txtPath.getFileName() + "#" + URLConnection.guessContentTypeFromStream(new BufferedInputStream(new FileInputStream(txtPath.toFile()))));  // null
        System.out.println(htmlPath.getFileName() + "#" + URLConnection.guessContentTypeFromStream(new BufferedInputStream(new FileInputStream(htmlPath.toFile()))));  // text/html
        System.out.println(mdPath.getFileName() + "#" + URLConnection.guessContentTypeFromStream(new BufferedInputStream(new FileInputStream(mdPath.toFile()))));  // null
        System.out.println(pdfPath.getFileName() + "#" + URLConnection.guessContentTypeFromStream(new BufferedInputStream(new FileInputStream(pdfPath.toFile()))));  // null
    }


    /**
     * 该方法返回URLConnection的所有实例使用的MIME类型表。然后，该表用于解析输入文件类型。
     * <p>
     * 当涉及URLConnection时，MIME类型的内置表非常有限。
     * <p>
     * 默认情况下，该类使用content-types.properties文件，其所在目录为JRE_HOME/lib。但是，我们可以通过使用content.types.user.table属性指定用户特定的表来扩展它 ：
     * <p>
     * System.setProperty("content.types.user.table","<path-to-file>");
     * 结论：根据文件扩展名判断。
     */
    @Test
    public void getFileNameMap() {
        System.setProperty("content.types.user.table","chapter60/src/main/resources/META-INF/content-types.properties");
        System.out.println(imagePath.getFileName() + "#" + URLConnection.getFileNameMap().getContentTypeFor(imagePath.toFile().getName())); // image/png
        System.out.println(docxPath.getFileName() + "#" + URLConnection.getFileNameMap().getContentTypeFor(docxPath.toFile().getName()));  // null
        System.out.println(txtPath.getFileName() + "#" + URLConnection.getFileNameMap().getContentTypeFor(txtPath.toFile().getName()));  // text/plain
        System.out.println(htmlPath.getFileName() + "#" + URLConnection.getFileNameMap().getContentTypeFor(htmlPath.toFile().getName()));  // text/html
        System.out.println(mdPath.getFileName() + "#" + URLConnection.getFileNameMap().getContentTypeFor(mdPath.toFile().getName()));  // null
        System.out.println(pdfPath.getFileName() + "#" + URLConnection.getFileNameMap().getContentTypeFor(pdfPath.toFile().getName()));  // application/pdf
    }

    /**
     * 该类是Java 6附带的，因此在使用JDK 1.6时非常方便。
     *
     * 此方法查找名为mime.types的文件以进行类型解析。请务必注意，该方法以特定顺序搜索文件：
     *
     * 以编程方式将条目添加到MimetypesFileTypeMap实例
     *
     * 用户主目录中的mime.types
     *
     * <java.home>/lib/mime.types
     *
     * 名为META-INF/mime.types的资源
     *
     * 名为META-INF/mimetypes.default的资源（通常仅在activation.jar文件中找到）
     *
     * 但是，如果找不到文件，它将返回application/octet-stream作为响应。
     *
     * 结论：根据文件扩展名判断。
    */
    @Test
    public void MimeTypesFileTypeMap() {
        //读取 META-INF/mime.types

        System.out.println(imagePath.getFileName() + "#" + new MimetypesFileTypeMap().getContentType(imagePath.toFile().getName())); // application/octet-stream
        System.out.println(docxPath.getFileName() + "#" + new MimetypesFileTypeMap().getContentType(docxPath.toFile().getName()));  // application/octet-stream
        System.out.println(txtPath.getFileName() + "#" + new MimetypesFileTypeMap().getContentType(txtPath.toFile().getName()));  // text/plain
        System.out.println(htmlPath.getFileName() + "#" + new MimetypesFileTypeMap().getContentType(htmlPath.toFile().getName()));  // text/html
        System.out.println(mdPath.getFileName() + "#" + new MimetypesFileTypeMap().getContentType(mdPath.toFile().getName()));  // application/octet-stream
        System.out.println(pdfPath.getFileName() + "#" + new MimetypesFileTypeMap().getContentType(pdfPath.toFile().getName()));  // application/octet-stream
    }

    /**
     * Apache Tika是一个工具集，可检测并从各种文件中提取元数据和文本。
     *
     * <dependency>
     *     <groupId>org.apache.tika</groupId>
     *     <artifactId>tika-core</artifactId>
     *     <version>xxx.xx</version>
     * </dependency>
     *
     * 结论：通过文件扩展名，内置类型文件：org/apache/tika/mime/tika-mimetypes.xml
     * 或者 根据文件流中前几个字符判断。
    */
    @Test
    public void ApacheTikaDetect() throws IOException {
        // 下面是通过文件扩展名判断类型，速度快，内置类型就很多了，基本常用类型都有，不认识就返回 application/octet-stream
        // 推荐这种方式，内置1千多种类型，又快又准
        final long start1 = System.currentTimeMillis();
        System.out.println(imagePath.getFileName() + "#" + new Tika().detect(imagePath.toFile().getName())); // image/png
        System.out.println(docxPath.getFileName() + "#" + new Tika().detect(docxPath.toFile().getName()));  // application/vnd.openxmlformats-officedocument.wordprocessingml.document
        System.out.println(txtPath.getFileName() + "#" + new Tika().detect(txtPath.toFile().getName()));  // text/plain
        System.out.println(htmlPath.getFileName() + "#" + new Tika().detect(htmlPath.toFile().getName()));  // text/html
        System.out.println(mdPath.getFileName() + "#" + new Tika().detect(mdPath.toFile().getName()));  // text/x-web-markdown
        System.out.println(pdfPath.getFileName() + "#" + new Tika().detect(pdfPath.toFile().getName()));  // application/pdf
        final long end1 = System.currentTimeMillis();
        System.out.println(end1-start1);
        System.out.println("####################################");
        //下面是根据文件流中前几个字符判断类型，速度略慢
        final long start2 = System.currentTimeMillis();
        System.out.println(imagePath.getFileName() + "#" + new Tika().detect(imagePath)); // image/png
        System.out.println(docxPath.getFileName() + "#" + new Tika().detect(docxPath));  // application/vnd.openxmlformats-officedocument.wordprocessingml.document
        System.out.println(txtPath.getFileName() + "#" + new Tika().detect(txtPath));  // text/plain
        System.out.println(htmlPath.getFileName() + "#" + new Tika().detect(htmlPath));  // text/html
        System.out.println(mdPath.getFileName() + "#" + new Tika().detect(mdPath));  // text/x-web-markdown
        System.out.println(pdfPath.getFileName() + "#" + new Tika().detect(pdfPath));  // application/pdf
        final long end2 = System.currentTimeMillis();
        System.out.println(end2-start1);
    }

    @Test
    public void ApacheTikaTranslate(){
        //System.out.println(new Tika().translate("你好", "en"));
        System.out.println(1);
    }


    @Test
    public void ApacheTikaParser() throws TikaException, IOException {
        System.out.println(new Tika().parseToString(txtPath));
    }




}
