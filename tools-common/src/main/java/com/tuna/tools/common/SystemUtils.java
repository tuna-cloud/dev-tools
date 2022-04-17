package com.tuna.tools.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SystemUtils {

    public static String getPluginDataDir(String plugin) {
        return getBaseDir() + File.separator + "data" +
                File.separator + "plugin" + File.separator + plugin;
    }

    public static String getBaseDir() {
        String base = org.apache.commons.lang3.SystemUtils.getUserDir().getAbsolutePath();
        if (new File(base + File.separator + "html").exists()) {
            return base;
        }
        String project = base + File.separator + "tools-resource";
        if (new File(project).exists()) {
            return project + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        }
        throw new RuntimeException("找不到工作目录,程序不应该跑到这里");
    }

    public static void unzip(String input, String output, Set<String> extName) {
        // 要解缩的文件
        File zipFile = new File(input);
        // 解压后的目录
        File dir = new File(output);
        // 输出流
        OutputStream out = null;
        // 压缩输入流
        ZipInputStream zin = null;

        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            zin = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry = null;// 压缩实体对象
            while ((entry = zin.getNextEntry()) != null) {
                if (!isMatch(entry.getName(), extName)) {
                    continue;
                }
                FileUtils.forceMkdirParent(new File(dir, entry.getName()));
                out = new FileOutputStream(new File(dir, entry.getName()));
                int temp = 0;
                while ((temp = zin.read()) != -1) {
                    out.write(temp);
                }
                out.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                zin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isMatch(String name, Set<String> ext) {
        if (ext == null) {
            return true;
        }
        for (String s : ext) {
            if (name.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
