
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;

public class Main {
	
    public static void main(String[] args) throws Exception {
        // runStg()
        // runEnc()
    }

    public static void runStg() throws Exception {
        try {
            String inputImgPath = "C:\\image\\path\\originalImg.png";
            String outputImgPath = "C:\\image\\path\\hiddenTextImg.png";
            String s = "My passsword";

			BufferedImage coverImageText = ImageIO.read(new File(inputImgPath));
			Steganography.embedText(coverImageText, s, outputImgPath);							
			Steganography.extractText(ImageIO.read(new File(outputImgPath)), s.length()); 
		} catch(IOException e) {		
			System.out.print("Error: " + e);
		}	
    }

    

    public static void runEnc() {
        String publicKey= "";
        String privateKey= "";
      try {
            // Specify the algorithm for key pair generation (e.g., RSA)
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            // Initialize the key pair generator with a key size (e.g., 2048 bits)
            keyPairGenerator.initialize(2048);

            // Generate the key pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Retrieve the public and private keys from the key pair
            PublicKey pubK = keyPair.getPublic();
            PrivateKey priK = keyPair.getPrivate();

            // Print the encoded forms of the keys (can be stored or transmitted as needed)
            publicKey = Base64.getEncoder().encodeToString(pubK.getEncoded());
            privateKey = Base64.getEncoder().encodeToString(priK.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // secret message
        String originalMessage = "My password";
        System.out.println("Original message:"+originalMessage);

        // Encrypt using the public key
        String encryptedMessage = "";
		try {
			encryptedMessage = encrypt(originalMessage, publicKey);
		} catch (Exception e1) {}
        System.out.println("Encrypted message: " + new String(encryptedMessage));

        // Decrypt using the private key
        String decryptedMessage = "";
		try {
			decryptedMessage = decrypt(encryptedMessage, privateKey);
		} catch (Exception e) {}
        System.out.println("Private key: " + privateKey);
        System.out.println("Decrypted message: " + decryptedMessage);
    }

    public static String encrypt(String message, String  publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString.getBytes());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey =  keyFactory.generatePublic(publicKeySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String encryptedMessage, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString.getBytes());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey= keyFactory.generatePrivate(privateKeySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)), StandardCharsets.UTF_8);
    }
}

