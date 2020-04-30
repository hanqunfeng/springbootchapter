package com.example.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * <p>服务端模拟</p>
 * 部分参数使用map，这里只是为了演示方便，可以接收任意数量的参数，正式环境不要这样使用，因为map很难让人确定到底需要传递哪些参数
 * Created by hanqf on 2020/4/20 12:28.
 */

@RestController
@RequestMapping("/demo")
public class IndexController {

    /**
     * <p>get请求</p>
     *
     * @param map 接收参数
     * @return java.lang.String
     * @author hanqf
     * 2020/4/20 14:19
     */
    @GetMapping("/get")
    public String get(@RequestParam Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toJSONString();
    }

    @PostMapping("/post")
    public String post(@RequestParam Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toJSONString();
    }

    @PostMapping(value = "/form", headers = "Content-Type=application/x-www-form-urlencoded")
    public String form(@RequestParam Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toJSONString();
    }

    @PostMapping(value = "/json", headers = "Content-Type=application/json;charset=utf8")
    public String json(@RequestBody Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toJSONString();
    }

    @PostMapping("/stream")
    public String inputStream(InputStream is) {
        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @PostMapping("/files")
    public String files(@RequestParam Map<String, Object> map, @RequestParam("files") List<MultipartFile> multipartFileList){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectMap = new JSONObject(map);
        jsonObject.put("map",jsonObjectMap);

        JSONArray jsonArray = new JSONArray();
        JSONObject js = null;
        try {
            for (MultipartFile file : multipartFileList) {
                js = new JSONObject();
                js.put("fileName", URLDecoder.decode(file.getOriginalFilename(), "utf-8"));
                js.put("fileSize", file.getSize());
                js.put("fileContentType", file.getContentType());
                jsonArray.add(js);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        jsonObject.put("files",jsonArray);
        return jsonObject.toJSONString();
    }

    @RequestMapping("/getBytes")
    public byte[] getBytes(@RequestParam Map<String, Object> map){
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toJSONString().getBytes(Charset.forName("utf-8"));
    }

    @RequestMapping("/getBytesZip")
    public byte[] getBytesZip(@RequestParam Map<String, Object> map, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = new JSONObject(map);
        byte[] bytes = jsonObject.toJSONString().getBytes(Charset.forName("utf-8"));
        //压缩
        response.addHeader("Content-Encoding", "gzip");
        ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
        originalContent.write(bytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        originalContent.writeTo(gzipOut);
        gzipOut.finish();
        //返回压缩后的字节数组
        return baos.toByteArray();
    }



}
