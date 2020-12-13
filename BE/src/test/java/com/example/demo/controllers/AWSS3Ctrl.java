package com.example.demo.controllers;

import com.example.demo.models.FileAvatar;
import com.example.demo.services.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@RestController
@RequestMapping(value = "/s3")
public class AWSS3Ctrl {
    @Autowired
    private AWSS3Service service;

    @PostMapping(value = "/upload-avatar/{userId}")
    public ResponseEntity<FileAvatar> uploadAvatar(@RequestPart(value = "file") MultipartFile multipartFile, @PathVariable long userId) {
        FileAvatar myFile = service.uploadAvatar(multipartFile, userId);
        if (myFile != null)
            return new ResponseEntity<>(myFile, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/download-avatar/{userId}")
    public ResponseEntity<StreamingResponseBody> downloadAvatar(@PathVariable long userId) {
        StreamingResponseBody d = service.downloadAvatar(userId);
        return new ResponseEntity<StreamingResponseBody>(d, HttpStatus.OK);
    }
}
