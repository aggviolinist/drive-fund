package com.drivefundproject.drive_fund.config.jwt;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.security.PrivateKey;
import java.security.PublicKey;

public class AwsKeyLoader {

    private static final Region REGION = Region.of("us-east-1");

    public static String getSecretValue(String secretName) {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(REGION)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(getSecretValueRequest);
        return response.secretString();
    }

    public static PrivateKey loadPrivateKey(String secretName) throws Exception {
        String privateKeyPem = getSecretValue(secretName);
        return KeyUtils.convertPemToPrivateKey(privateKeyPem);
    }

    public static PublicKey loadPublicKey(String secretName) throws Exception {
        String publicKeyPem = getSecretValue(secretName);
        return KeyUtils.convertPemToPublicKey(publicKeyPem);
    }
}

