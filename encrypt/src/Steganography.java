import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Steganography {
	// embed secret information/TEXT into a "cover image"
	public static void embedText(BufferedImage image, String s, String fileName) throws Exception {
        int n = s.length();

        if (n * 8 > image.getWidth() * image.getHeight()) {
            throw new Exception("The provided text is too large to be embedded in the image.");
        }

        int x = 0;
        int y = 0;

        for (int i = 0; i < n; i++) { // for every char in string
            char c = s.charAt(i);
        	int binaryNum = (int) c; // get ASCII value
            for (int j = 0; j < 8; j++) { // write every bit from binaryNum

                int bit = binaryNum & 0x00000001; // take LSB
                binaryNum = binaryNum >> 1; // move to next digit

                int colorByte = image.getRGB(x, y);

                if (bit == 1) {
                    image.setRGB(x, y, colorByte | 0x00000001);
                } else {
                    image.setRGB(x, y, colorByte & 0xFFFFFFFE);
                }
                
                x++;
                if (x >= image.getWidth()) {
                    x = 0;
                    y++;
                }

            }
        }
        System.out.println();
        File outputfile = new File(fileName);	
		ImageIO.write(image, "png", outputfile);
    }
		
		// extract secret information/Text from a "cover image"
		public static void extractText(BufferedImage image, int length) {
			int x = 0, y = 0;
			
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
				System.out.print(c);  
			}
		}
}
