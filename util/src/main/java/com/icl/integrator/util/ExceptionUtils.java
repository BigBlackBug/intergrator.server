package com.icl.integrator.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

/**
 * Created by BigBlackBug on 08.05.2014.
 */
public class ExceptionUtils {

	public static String getStackTraceAsString(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}

	public static String getRealSQLError(Throwable ex) {
		Throwable x = ex;
		while (!((x = x.getCause()) instanceof SQLException)) {
		}
		SQLException realException = ((SQLException) x).getNextException();
		if (realException != null) {
			return getStackTraceAsString(realException);
		} else {
			return getStackTraceAsString(x);
		}
	}
}
