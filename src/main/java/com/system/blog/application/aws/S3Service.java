package com.system.blog.application.aws;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Buckets s3Buckets;

    public void uploadCustomerImage(Integer customerId, MultipartFile file) {
        //check if customer exists
        String profileImageId = UUID.randomUUID().toString();
        try {
            putObject(
                s3Buckets.getCustomer(),
                "profile-image/%s/%s".formatted(customerId, profileImageId),
                file.getBytes()
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
        //TODO: store profileImageId to db

    }

    public byte[] getCustomerImage(Integer customerId) {
        //check if customer exists, get customer by id
        //check if profileImageId empty or null

        String profileImageId = "image-id";//get from customer
        return getObject(
            s3Buckets.getCustomer(), 
            "profile-image/%s/%s".formatted(customerId, profileImageId)
            );
        
    }

    private void putObject(String bucketName, String key, byte[] file) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build();

        s3Client.putObject(objectRequest, RequestBody.fromBytes(file));                        
    }

    private byte[] getObject(String keyName, String bucketName) {
        GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(objectRequest);
        try {
            return response.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
