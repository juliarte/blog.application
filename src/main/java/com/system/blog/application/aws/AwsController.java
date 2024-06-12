package com.system.blog.application.aws;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AwsController {

    private S3Service s3Service;

    @PostMapping(
        value = "{customerId}/profile-image",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
    public void uploadProfilePictureToAws(@PathVariable("customerId") Integer customerId,
            @RequestParam("file") MultipartFile file) {
                s3Service.uploadCustomerImage(null, null);

    }

    @GetMapping("{customerId}/profile-image")
    public byte[] getProfilePictureToAws(@PathVariable("customerId") Integer customerId) {
                return s3Service.getCustomerImage(customerId);

    }

}
