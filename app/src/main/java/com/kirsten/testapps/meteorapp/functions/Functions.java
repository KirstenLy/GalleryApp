package com.kirsten.testapps.meteorapp.functions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Functions {

    public static boolean checkArray(boolean[] checked) {
        for (boolean b : checked) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> prepareData(File[] files) {
        if (files == null) {
            return null;
        }
        ArrayList<String> imageList = new ArrayList<>();
        for (File file : files) {
            if (!file.getName().endsWith(".jpg")) {
                continue;
            }
            imageList.add(file.getName());
        }
        return imageList;
    }

    public static File createImageFile(String path) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return File.createTempFile(imageFileName, ".jpg", new File(path));
    }
}
