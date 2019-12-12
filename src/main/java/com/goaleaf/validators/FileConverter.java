package com.goaleaf.validators;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class FileConverter {

    public static String encodeFileToBase64Binary(File file) {
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        return encodedString;
    }

    public static File decodeFileFromBase64Binary(String string) {
        byte[] decodedBytes = Base64.getDecoder().decode(string);
        File output = new File("image.png");
        try {
            FileUtils.writeByteArrayToFile(output, decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }
}
