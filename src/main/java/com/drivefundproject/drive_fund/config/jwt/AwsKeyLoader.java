package com.drivefundproject.drive_fund.config.jwt;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.security.PrivateKey;
import java.security.PublicKey;

public class AwsKeyLoader {

    private static final Region REGION = Region.of("us-east-1");

    public static String getSecretValue(String secretName) {
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(REGION)
                .build()) {

            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(getSecretValueRequest);
            return response.secretString();

        } catch (SecretsManagerException e) {
            System.err.println("Failed to fetch secret from AWS (" + secretName + "): " 
                    + e.awsErrorDetails().errorMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error fetching AWS secret: " + e.getMessage());
            return null;
        }
    }

    public static PrivateKey loadPrivateKey(String secretName) throws Exception {
        String privateKeyPem = getSecretValue(secretName);
        if (privateKeyPem == null) {
            throw new IllegalStateException("AWS secret for private key '" + secretName + "' is missing or null.");
        }
        return KeyUtils.convertPemToPrivateKey(privateKeyPem);
    }

    public static PublicKey loadPublicKey(String secretName) throws Exception {
        String publicKeyPem = getSecretValue(secretName);
        if (publicKeyPem == null) {
            throw new IllegalStateException("AWS secret for public key '" + secretName + "' is missing or null.");
        }
        return KeyUtils.convertPemToPublicKey(publicKeyPem);
    }
}
