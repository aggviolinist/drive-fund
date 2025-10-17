package com.drivefundproject.drive_fund.config.jwt;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class KeyUtils {

    //Utiliy classes aren't instantited coz they just static helpers
    private KeyUtils(){} //We create Utility class since we don't want to instantiate it

    public static PrivateKey convertPemToPrivateKey(String pemContent) throws Exception{
        String privateKeyPEM = pemContent
                    .replace("-----BEGIN PRIVATE KEY-----","")
                    .replace("-----END PRIVATE KEY-----","")
                    .replaceAll("\\s+", "");
        final byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
    public static PublicKey convertPemToPublicKey(String pemContent) throws Exception{
        String publicKeyPEM = pemContent
                     .replace("-----BEGIN PUBLIC KEY-----","")
                     .replace("-----END PUBLIC KEY-----","")
                     .replaceAll("\\s+","");
        final byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        final X509EncodedKeySpec  spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
    // Reading from local Public and Public file
    
    // private static String readKeyFromResource(final String pemPath) throws Exception{
    //     try (final InputStream inputstreamz = new ClassPathResource(pemPath).getInputStream()){
        
    //     return new String(inputstreamz.readAllBytes());

    //         }
    //     }
    }
