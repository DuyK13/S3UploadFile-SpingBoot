package com.example.demo.controllers;

import com.example.demo.models.MyFile;
import com.example.demo.services.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;


@RestController
@RequestMapping(value= "/s3")
public class AWSS3Ctrl {
    @Autowired
    private AWSS3Service service;

    @PostMapping(value= "/upload")
    public ResponseEntity<MyFile> uploadFile(@RequestPart(value= "file") MultipartFile multipartFile) {
        MyFile myFile = service.uploadFile(multipartFile);
        if(myFile != null)
        return new ResponseEntity<>(myFile, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/download/{folder}/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String folder, @PathVariable String fileName) {
        ByteArrayOutputStream downloadInputStream = service.downloadFile(folder, fileName);
        return ResponseEntity.ok()
                .contentType(contentType(fileName))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + fileName + "\"")
                .body(downloadInputStream.toByteArray());
    }

    private MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length - 1];
        switch (type) {
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
