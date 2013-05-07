package com.aakash.app.wi_net.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import com.aakashapp.modguru.Main;

import android.os.Environment;
import android.util.Log;

public class StroreData {

    private long size;
    private String fileName, temp;
    private File file;
    private FileOutputStream outputStream;
    public boolean complete = false;

    public void readData(byte[] bytes, int bytesRead) throws IOException {

        int line = 1;
        Scanner scanner = new Scanner(new ByteArrayInputStream(bytes));
        scanner.useDelimiter("\n");
        if (scanner.next().startsWith("start/metadata")) {
            while (line < 5) {
                if (line == 2) {
                    fileName = scanner.next();
                    if (fileName.startsWith("name:")) {
                        fileName = fileName.replace("name:", "");
                        file = new File(Environment.getDataDirectory() + File.separator + "data" + File.separator + Main.PACKAGE_NAME + File.separator + fileName);
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        if (file.exists()) {
                            File oldResFolder = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/" + Main.PACKAGE_NAME + "/res/" + file.getParentFile().getName());
                            File newResFolder = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/" + Main.PACKAGE_NAME + "/res/" + file.getParentFile().getName() + "-" + System.currentTimeMillis());
                            if (oldResFolder.isDirectory()) {
                                oldResFolder.renameTo(newResFolder);
                            }
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        } else {
                            file.createNewFile();
                        }
                        new RandomAccessFile(file, "rw").setLength(0);
                        outputStream = new FileOutputStream(file, true);
                        Log.e("File", file.getAbsolutePath());
                    } else {
                        fileName = null;
                    }
                    Log.e("Stroe", fileName);
                }
                if (line == 3) {
                    temp = scanner.next();
                    if (temp.startsWith("len:")) {
                        temp = temp.replace("len:", "");
                        size = Long.parseLong(temp);
                        Log.e("Stroe", temp);
                    }
                }
                line++;
            }
        }
        String data = new String(bytes, 0, bytesRead);
        int off = 0;
        if (data.startsWith("start/metadata")) {
            off = (data.indexOf("start/data") + 10);
            bytesRead -= off;
        }
        if (data.endsWith("end/data")) {
            bytesRead = bytesRead - "end/data".length();
            line = 1;
        }
        if (data.equals("transcomp/term")) {
            complete = true;
            return;
        }
        outputStream.write(bytes, off, bytesRead);
        Log.e("Store", new String(bytes, off, bytesRead));
    }

    public long getSize() {
        return size;
    }

    public void deleteFile() {
        file.delete();
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return file.getPath();
    }
}
