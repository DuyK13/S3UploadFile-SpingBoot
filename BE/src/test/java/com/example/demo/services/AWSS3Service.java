package com.example.demo.services;

import com.example.demo.models.MyFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;

public interface AWSS3Service {
    public MyFile uploadFile(MultipartFile multipartFile);
    public File convertMultiPartFileToFile(MultipartFile multipartFile);
    public void uploadFileToS3Bucket(String bucketName, String fileName, File file);
    public ByteArrayOutputStream downloadFile(final String folder, final String keyName);
}
