package com.goaleaf.validators;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class FileConverter {

    public static String encodeFileToBase64Binary(File file) {

        File input = scale(file);
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        return encodedString;
    }

    public static File decodeFileFromBase64Binary(String string) {
        byte[] decodedBytes = Base64.getDecoder().decode(string);
        File output = new File("image.jpg");
        try {
            FileUtils.writeByteArrayToFile(output, decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    private static File scale(File input) {

        final int w = 40;
        final int h = 40;

        Image image = null;
        try {
            image = ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image newImage = image.getScaledInstance(w, h, Image.SCALE_DEFAULT);

        BufferedImage resultImage = null;
        File resultFile = new File(".temp/RESULT_IMAGE.jpg");
        try {

            resultImage = ImageIO.read((ImageInputStream) newImage);

            ImageIO.write(resultImage, "jpg", resultFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultFile;

    }
}
