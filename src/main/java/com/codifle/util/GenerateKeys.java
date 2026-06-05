package com.codifle.util;

import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * Run once to generate RSA keypair PEM files for JWT.
 * Output: src/main/resources/META-INF/resources/publicKey.pem
 *         privateKey.pem (keep secret, do not commit)
 */
public class GenerateKeys {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();

        String pub = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'})
                         .encodeToString(pair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----\n";

        String priv = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'})
                         .encodeToString(pair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";

        String pubPath  = "src/main/resources/META-INF/resources/publicKey.pem";
        String privPath = "privateKey.pem";

        try (FileWriter fw = new FileWriter(pubPath))  { fw.write(pub);  }
        try (FileWriter fw = new FileWriter(privPath)) { fw.write(priv); }

        System.out.println("Generated: " + pubPath);
        System.out.println("Generated: " + privPath);
        System.out.println("\nPrivateKey (keep this, use for signing tokens):");
        System.out.println(priv);
    }
}
