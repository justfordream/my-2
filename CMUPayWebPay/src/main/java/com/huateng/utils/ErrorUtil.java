package com.huateng.utils;

import org.apache.log4j.Logger;

public class ErrorUtil {

    /**
     * 错误日志打印  后8行
     * @param e
     * @param logger
     */
    public static void printErrorLog(Exception e, Logger logger) {

        if (logger == null || e == null) {
            return;
        }

        logger.error(e.getMessage());

        StackTraceElement[] s = e.getStackTrace();

        int len = s.length;

        for (int i = 0; i < 8 && i < len; i++) {

            logger.error(s[i]);

        }

    }

}
