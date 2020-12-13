package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileChat {
    // Tên
    private String fileName;
    // Loại
    private String fileType;
    // Nơi lưu cuộc trò chuyện giữa 2 người or nhóm
    private String fileFolder;
}
