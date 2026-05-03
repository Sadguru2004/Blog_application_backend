package com.sadguru.blogapplication.services.impl;

import com.sadguru.blogapplication.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        // create folder if not exists
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // original filename
        String originalName = file.getOriginalFilename();

        // extension
        String ext = originalName.substring(originalName.lastIndexOf("."));

        // random filename
        String fileName = UUID.randomUUID() + ext;

        // full path
        Path filePath = Paths.get(path, fileName);

        // save
        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return fileName;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        Path fullPath = Paths.get(path, fileName);
        return new FileInputStream(fullPath.toFile());
    }

    @Override
    public void deleteImage(String path, String fileName) {
        try {
            Path fullPath = Paths.get(path, fileName);
            Files.deleteIfExists(fullPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}