package com.tuna.tools.common;

import java.io.File;

public class SystemUtils {

    public static String getPluginRootDir(String plugin) {
        return org.apache.commons.lang3.SystemUtils.getUserDir().getAbsolutePath() + File.separator + "data" +
                File.separator + "plugin" + File.separator + plugin;
    }
}
