package com.tuna.tools.fiddler.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String format(long ts) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }
}
