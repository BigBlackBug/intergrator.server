package com.icl.integrator.util;

import com.icl.integrator.dto.util.Utils;

import java.sql.SQLException;

/**
 * Created by BigBlackBug on 05.05.2014.
 */
public class DatabaseExceptionUtils {

	public static String getRealErrorCause(Throwable ex) {
		Throwable x = ex;
		while (!((x = x.getCause()) instanceof SQLException)) {
		}
		SQLException realException = ((SQLException) x).getNextException();
		if (realException != null) {
			return Utils.getStackTraceAsString(realException);
		} else {
			return Utils.getStackTraceAsString(x);
		}
	}

}
