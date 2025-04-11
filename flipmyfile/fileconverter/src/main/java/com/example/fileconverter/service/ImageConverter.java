package com.example.fileconverter.service;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.*;

@Service
public class ImageConverter {

    public ByteArrayOutputStream convertImageFormat(InputStream inputStream, String outputFormat) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);

        if (inputImage == null) {
            throw new IOException("Invalid image file.");
        }

        // Use appropriate image type for JPG (which doesn't support transparency)
        BufferedImage newImage;
        if (outputFormat.equalsIgnoreCase("jpg") || outputFormat.equalsIgnoreCase("jpeg")) {
            newImage = new BufferedImage(
                    inputImage.getWidth(),
                    inputImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Paint the original image on white background
            Graphics2D g = newImage.createGraphics();
            g.drawImage(inputImage, 0, 0, java.awt.Color.WHITE, null);
            g.dispose();

            outputFormat = "jpeg"; // required for ImageIO
        } else {
            newImage = inputImage;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean result = ImageIO.write(newImage, outputFormat.toLowerCase(), outputStream);

        if (!result) {
            throw new IOException("Could not write output in format: " + outputFormat);
        }

        return outputStream;
    }
}
