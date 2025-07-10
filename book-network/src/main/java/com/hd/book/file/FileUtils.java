package com.hd.book.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {

    // 从指定位置读取文件
    public static byte[] readFileFromLocation(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return null;
        }
        try {
            Path path = new File(fileUrl).toPath();
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.warn("在指定路径中未找到文件 {}", fileUrl);
        }
        return null;
    }
}
