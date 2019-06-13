package com.pinyougou.shop.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传
 */
@RestController
public class UploadController {

    @Value("${fileServerUrl}")
    private String fileServerUrl;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "500");
        try {
            String path = this.getClass().getResource("/fastdfs-client.conf").getPath();

            ClientGlobal.init(path);

            StorageClient storageClient = new StorageClient();

            String filename = multipartFile.getOriginalFilename();

            String[] arr = storageClient.upload_file(multipartFile.getBytes(), FilenameUtils.getExtension(filename), null);

            StringBuilder url = new StringBuilder(fileServerUrl);
            for (String s : arr) {
                url.append("/" + s);
            }
            data.put("status", "200");
            data.put("url", url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @GetMapping("/imgs/delete")
    public boolean upload(String url) {
        String s = url.substring(url.indexOf(fileServerUrl) + fileServerUrl.length());
        String[] arr = s.split("/", 3);
        try {
            String path = this.getClass().getResource("/fastdfs-client.conf").getPath();
            ClientGlobal.init(path);
            StorageClient storageClient = new StorageClient();
            int res = storageClient.delete_file(arr[1], arr[2]);
            return res == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
