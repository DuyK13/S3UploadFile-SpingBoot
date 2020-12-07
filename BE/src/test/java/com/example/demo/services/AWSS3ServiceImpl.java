package com.example.demo.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.example.demo.models.MyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.endpointUrl}")
    private String endpointUrl;

    @Override
    public MyFile uploadFile(MultipartFile multipartFile) {
        MyFile myFile = new MyFile();
        try {
            File file = convertMultiPartFileToFile(multipartFile);
            String fileName = file.getName().replaceAll("\\s", "");
            String fileURL = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileToS3Bucket(bucketName, fileName, file);
            myFile.setFileName(fileName);
            myFile.setFileURL(fileURL);
            file.delete();  // To remove the file locally created in the project folder.
        } catch (final AmazonServiceException ex) {
            System.out.println(ex.getMessage());
        }
        return myFile;
    }

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

    @Override
    @Async
    public void uploadFileToS3Bucket(String bucketName, String fileName, File file) {
//        boolean isHaveDir = false;
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead);
//        List<Bucket> buckets = amazonS3.listBuckets();
//        for(Bucket b: buckets){
//            if(b.getName().equals("image")){
//                isHaveDir = true;
//            }
//        }
//        if(isHaveDir){
//            TransferManager tm = new TransferManager();
//            tm.uploadDirectory(bucketName, "image", file, false);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "image/" + fileName, file).withCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = amazonS3.putObject(putObjectRequest);
    }

    @Override
    @Async
    public ByteArrayOutputStream downloadFile(String folder, String keyName) {
        try {
            byte[] content = null;
            S3Object s3Object = amazonS3.getObject(bucketName, folder + "/" + keyName);
//            S3Object s3Object = amazonS3.getO
            InputStream is = s3Object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }
}
