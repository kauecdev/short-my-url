package com.kauecdev.shortmyurl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RedirectHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client;

    public RedirectHandler(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public RedirectHandler() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .build();
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String pathParameters = input.get("rawPath").toString();
        String shortUrlCode = pathParameters.replace("/", "");

        if (shortUrlCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required.");
        }

        InputStream s3ObjectStream;

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket("shortmyurl-storage")
                    .key(shortUrlCode + ".json")
                    .build();

            s3ObjectStream = s3Client.getObject(request);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching data from S3: " + ex.getMessage(), ex);
        }

        UrlData urlData;

        try {
            urlData = objectMapper.readValue(s3ObjectStream, UrlData.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error deserializing URL data: " + ex.getMessage(), ex);
        }

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        Map<String, Object> response = new HashMap<>();

        if (currentTimeInSeconds > urlData.expirationTime()) {
            response.put("statusCode", 410);
            response.put("body", "This URL has expired.");

            return response;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Location", urlData.originalUrl());

        response.put("statusCode", 302);
        response.put("headers", headers);

        return response;
    }
}