package com.drivefundproject.drive_fund.utilities.profileImages;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.images-bucko}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        // Generate a unique filename to prevent overwrites
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        
        try {
            // Set metadata for the object
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            // Upload the file to S3
            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
            
            // Get and return the public URL of the uploaded file
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            // Handle the exception
            throw new IllegalStateException("Failed to upload file", e);
        }
    }
}