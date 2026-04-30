package com.sadguru.blogapplication.services.impl;

import com.sadguru.blogapplication.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        String originalName = file.getOriginalFilename();

        String randomId = UUID.randomUUID().toString();

        String fileName = randomId.concat(originalName.substring(originalName.lastIndexOf(".")));

        String filePath = path + File.separator + fileName;

        File f = new File(path);

        if(!f.exists()){
            f.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));
        return fileName;
    }

    public InputStream getResource(String path, String fileName) throws FileNotFoundException {

        String fullPath = path + File.separator + fileName;

        return new FileInputStream(fullPath);
    }

    @Override
    public void deleteImage(String path, String fileName) {

        try {
            String fullPath = path + File.separator + fileName;

            File file = new File(fullPath);

            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            System.out.println("Error while deleting image: " + e.getMessage());
        }
    }
}
