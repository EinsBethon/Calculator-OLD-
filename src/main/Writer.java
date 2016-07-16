package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    public static void write(String s) {
        BufferedWriter writer;
        File file = new File(System.getProperty("user.home") + "/Desktop/calculator/history.txt");
        if (file.exists()) {
            try {
                writer = new BufferedWriter(new FileWriter(file, true));
                writer.write(s + System.lineSeparator());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
