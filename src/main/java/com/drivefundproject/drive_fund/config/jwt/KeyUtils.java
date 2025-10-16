package com.drivefundproject.drive_fund.config.jwt;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource; 

public class KeyUtils {


    private KeyUtils(){} //We create Utility class since we don't want to instantiate it

    public static PrivateKey loadPrivateKey(final String pemPath) throws Exception{
        final String key = readKeyFromResource(pemPath).replace("-----BEGIN PRIVATE KEY-----","")
                                                       .replace("-----END PRIVATE KEY-----","")
                                                       .replaceAll("\\s+", "");
        final byte[] decoded = Base64.getDecoder().decode(key);
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
    public static PublicKey loadPublicKey(final String pemPath) throws Exception{
        final String key = readKeyFromResource(pemPath).replace("-----BEGIN PUBLIC KEY-----","")
                                                       .replace("-----END PUBLIC KEY-----","")
                                                       .replaceAll("\\s+","");
        final byte[] decoded = Base64.getDecoder().decode(key);
        final X509EncodedKeySpec  spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private static String readKeyFromResource(final String pemPath) throws Exception{
        try (final InputStream inputstreamz = new ClassPathResource(pemPath).getInputStream()){
        
        return new String(inputstreamz.readAllBytes());

            }
        }
    }
