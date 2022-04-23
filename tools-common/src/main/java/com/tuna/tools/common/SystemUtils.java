package com.tuna.tools.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SystemUtils {

    public static String getPluginDataDir(String plugin) {
        return getBaseDir() + File.separator + "data" +
                File.separator + "plugin" + File.separator + plugin;
    }

    public static String getBaseDir() {
        String appPath = getAppPath();
        if (appPath.contains("tuna-dev-tools")) {
            appPath = appPath.substring(0, appPath.indexOf("tuna-dev-tools"));
            appPath += "tuna-dev-tools" + File.separator + "web";
            System.out.println(appPath);
            return appPath;
        } else {
            appPath = appPath.substring(0, appPath.indexOf("tuna.app"));
            appPath += "tuna.app";
            Collection<File> list = FileUtils.listFilesAndDirs(new File(appPath), new IOFileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }

                @Override
                public boolean accept(File dir, String name) {
                    return false;
                }
            }, new IOFileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }

                @Override
                public boolean accept(File dir, String name) {
                    return name.equals("web");
                }
            });
            for (File file : list) {
                if (file.getName().equals("web")) {
                    if (new File(file, "html").exists()) {
                        System.out.println(file.getAbsolutePath());
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        throw new RuntimeException("Can not find app path");
    }

    public static void main(String[] args) {
        System.out.println(getBaseDir());
    }

    private static String getAppPath() {
        Class cls = VertxInstance.class;
        ClassLoader loader = cls.getClassLoader();
        //获得类的全名，包括包名
        String clsName = cls.getName() + ".class";
        //获得传入参数所在的包
        Package pack = cls.getPackage();
        String path = "";
        //如果不是匿名包，将包名转化为路径
        if (pack != null) {
            String packName = pack.getName();
            clsName = clsName.substring(packName.length() + 1);
            //判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if (packName.indexOf(".") < 0) {
                path = packName + "/";
            } else {//否则按照包名的组成部分，将包名转换为路径
                int start = 0, end = 0;
                end = packName.indexOf(".");
                while (end != -1) {
                    path = path + packName.substring(start, end) + "/";
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                path = path + packName.substring(start) + "/";
            }
        }
        //调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url = loader.getResource(path + clsName);
        //从URL对象中获取路径信息
        String realPath = url.getPath();
        //去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }
        //去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);
        //如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
      /*------------------------------------------------------------
       ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
        中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
        的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
        中文及空格路径
      -------------------------------------------------------------*/
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realPath;
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
