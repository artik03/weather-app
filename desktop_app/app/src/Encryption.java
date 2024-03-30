import javax.crypto.Cipher;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
	
public class Encryption {
	
    public static String decrypt(String encryptedMessage, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString.getBytes());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey= keyFactory.generatePrivate(privateKeySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)), StandardCharsets.UTF_8);
    }
	    
	    public static String getKey() {	
	    	String encKey = "";
	    	InputStream imageStream = MainFrame.class.getResourceAsStream("/images/logo.png");

	    	if (imageStream != null) {
	    	    try {
	    	        BufferedImage image = ImageIO.read(imageStream);
	    	        encKey = extractText(image, 344);
	    	    } catch (IOException e) {
	    	        System.out.print("encryption: " + e);
	    	    } finally {
	    	        try {
	    	            imageStream.close();
	    	        } catch (IOException e) {
	    	            // Handle closing stream exception if needed
	    	        }
	    	    }
	    	} else {
	    	    System.out.println("Image not found");
	    	}
	    	return encKey;
	    }
	    
		
	 // extract secret information/Text from a "cover image"
	 		public static String extractText(BufferedImage image, int length) {
	 			int x = 0, y = 0;
	 			String text = "";
	 			
	 			for (int i = 0; i< length; i++) {
	 				int binaryNum = 0;
	 				for (int j = 0; j<8; j++) {
	 					int colorByte = image.getRGB(x, y);
	 					int bit = colorByte & 0x00000001;
	 					binaryNum = binaryNum >> 1;
	 					if (bit == 1) {
	 						binaryNum = binaryNum | 0x80;
	 					}
	 					
	 					
	 					x++;
	 					if (x >= image.getWidth()) {
	 		               x = 0;
	 		               y++;
	 		            }

	 				}
	 				
	 				char c = (char) binaryNum;  
	 				text = text + c;  
	 			}
	 			
	 			return text;
	 		}
}
