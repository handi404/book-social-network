package com.hd.book.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.*;
import static java.lang.System.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @NonNull MultipartFile sourceFile,
            @NonNull Integer userId
    ) {
        // 上传文件保存到服务器的子路径(根据id为每个用户创建一个文件夹，用于上传属于该用户的全部文件)
        // File.separator: 要获得正确的分隔符，必须使用文件分隔符(File.separator)
        // 它将根据平台使用正确的分隔符，如果是 Windows，它将是"\\"; Linux, 它是"/"
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(
            @NonNull MultipartFile sourceFile,
            @NonNull String fileUploadSubPath
    ) {
        // 最终上传路径 例如：./uploads/users/1
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        // 确保有目标文件夹
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("无法创建目标文件夹：" + targetFolder);
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // 保存文件路径 例如：./uploads/users/1/1720588800000.png
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            return targetFilePath;
        } catch (IOException e) {
            log.error("文件未保存", e);
        }
        return null;
    }

    // 获取文件扩展名
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        // 文件名最后一个点的索引 例如：hello.png 中"."的索引为5
        int lastDotIndex  = fileName.lastIndexOf(".");
        // 检查是否有扩展名
        if (lastDotIndex == -1) {
            return "";
        }
        // .JPG -> jpg
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
