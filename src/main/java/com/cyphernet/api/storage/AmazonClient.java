package com.cyphernet.api.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
public class AmazonClient {
    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        endpointUrl,
                        "fr-par"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public String generateFileName() {
        return UUID.randomUUID().toString();
    }

    private void uploadFileTos3bucket(String fileName, MultipartFile file) throws IOException {
        ObjectMetadata objMetadata = new ObjectMetadata();
        objMetadata.setContentLength(file.getSize());
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), objMetadata)
                .withCannedAcl(CannedAccessControlList.Private));
    }

    public URL getPresignedUrl(String key, HttpMethod method) {
        try {
            // Set the pre-signed URL to expire after one day.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60 * 24;
            expiration.setTime(expTimeMillis);

            // Generate the pre-signed URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                    .withMethod(method)
                    .withExpiration(expiration);
            return this.s3client.generatePresignedUrl(generatePresignedUrlRequest);
        } catch (SdkClientException e) {
            e.printStackTrace();
            throw new Error("Could not create url presigned for the file " + key);
        }
    }

    public String uploadFile(MultipartFile multipartFile, String fileName) throws IOException {
        String fileUrl = "";
        try {
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, multipartFile);
        } catch (Exception e) {
            throw e;
        }
        return fileUrl;
    }

    public S3Object download(String key) throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        S3Object s3Object = s3client.getObject(getObjectRequest);

        return s3Object;
    }

    public void deleteFileFromS3Bucket(String fileName) throws SdkClientException {
        s3client.deleteObject(bucketName, fileName);
    }
}
