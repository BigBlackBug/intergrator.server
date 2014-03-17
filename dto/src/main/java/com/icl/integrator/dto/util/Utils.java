package com.icl.integrator.dto.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 13.01.14
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static String getStackTraceAsString(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
