package com.example.demo.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.demo.models.FileAvatar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.endpointUrl}")
    private String endpointUrl;

    // Chuyển đổi file
    @Override
    public File convertMultiPartFileToFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            System.out.println(ex.getMessage());
        }
        return file;
    }

    // Async để chờ upload thành công
    @Override
    @Async
    public void uploadFileToS3Bucket(String bucketName, String folderName, String fileName, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + "/" + fileName, file).withCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = amazonS3.putObject(putObjectRequest);
    }

    // Async để chờ download thành công
    @Override
    @Async
    public S3ObjectInputStream downloadFileFromS3Bucket(String bucketName, String keyFile) {
        S3Object s3Object = amazonS3.getObject(bucketName, keyFile);
        return s3Object.getObjectContent();
    }

    @Override
    public FileAvatar uploadAvatar(MultipartFile multipartFile, long userId) {
        FileAvatar fileAvatar = new FileAvatar();
        try {
            // Chuyển multipartFile sang file
            File file = convertMultiPartFileToFile(multipartFile);
            // Lấy file name là userId
            String fileName = String.valueOf(userId);
            String[] arr = file.getName().replaceAll("\\s", "").split("\\.");
            String type = arr[arr.length - 1];
            String folderName = "userAvatar";
            // upload file vào bucket, folder với data là file và tên file
            uploadFileToS3Bucket(bucketName, folderName, fileName, file);
            // Cập nhật modal truyền api
            fileAvatar.setFileName(fileName);
            fileAvatar.setFileType(type);
            // Xoá tiết kiệm data
            file.delete();
        } catch (final AmazonServiceException ignore) {
        }
        return fileAvatar;
    }

    @Override
    public StreamingResponseBody downloadAvatar(long userId) {
        S3ObjectInputStream finalObject = downloadFileFromS3Bucket(bucketName, "userAvatar/" + userId);

        final StreamingResponseBody body = outputStream -> {
            int numberOfBytesToWrite = 0;
            byte[] data = new byte[1024];
            while ((numberOfBytesToWrite = finalObject.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, numberOfBytesToWrite);
            }
            finalObject.close();
        };
        return body;
    }

    @Override
    public FileAvatar uploadImage(MultipartFile multipartFile, String folderName, long chatId) {
        return null;
    }

    @Override
    public StreamingResponseBody downloadImage(String folder, long userId) {
        return null;
    }
}
