package com.aptiv.smb.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * java解压缩zip包
 * */
@Slf4j
public class UnZipUtil {

    public static List<String> unzipFile(List<String> localPaths) {
        List<String> xmlStrs = new ArrayList<>();

        for (String localPath : localPaths) {
            File srcFile = new File(localPath);
            // 开始解压
            ZipFile zipFile = null;

            try {
                zipFile = new ZipFile(srcFile, Charset.forName("GBK"));
                Enumeration<?> entries = zipFile.entries();
                while (entries.hasMoreElements()) { // 逐个解析压缩包下的xml文件
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    log.info(entry.getName());

                    if (entry.getName().endsWith(".xml")) {
                        StringBuilder xmlContentSb = new StringBuilder(); // xml文件的内容
                        try (InputStream in = zipFile.getInputStream(entry);
                            InputStreamReader inputStreamReader = new InputStreamReader(in);
                            BufferedReader reader = new BufferedReader(inputStreamReader)) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    xmlContentSb.append(line).append("\n");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                        log.info(xmlContentSb);
                        xmlStrs.add(xmlContentSb.toString());
                    }
                }
            } catch (Exception e) {
                log.error("unzip error from UnZipUtil." + srcFile.getName() + ".\n" + e);
                throw new RuntimeException("unzip error from UnZipUtil", e);
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return xmlStrs;
    }

    public static void main(String[] args) {
        List<String> localPaths = Arrays.asList("C:\\ARCHIVE_CHO_K426CSCQ_2017-08-15.zip", "C:\\ARCHIVE_CHO_K426CSCQ_2017-08-16.zip");
        unzipFile(localPaths);
    }
}
