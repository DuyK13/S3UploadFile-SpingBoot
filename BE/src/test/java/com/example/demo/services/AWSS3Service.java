package com.example.demo.services;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.demo.models.FileAvatar;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;

public interface AWSS3Service {
    // File converter
    File convertMultiPartFileToFile(MultipartFile multipartFile);
    // Upload and download to s3
    void uploadFileToS3Bucket(String bucketName, String folderName, String fileName, File file);
    S3ObjectInputStream downloadFileFromS3Bucket(String bucketName, String keyFile);
    // User avatar
    FileAvatar uploadAvatar(MultipartFile multipartFile, long userId);
    StreamingResponseBody downloadAvatar(final long userId);
    // Chat
    FileAvatar uploadImage(MultipartFile multipartFile, String folderName, long chatId);
    StreamingResponseBody downloadImage(final String folder, final long userId);
}
