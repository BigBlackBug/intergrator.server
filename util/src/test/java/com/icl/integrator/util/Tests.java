package com.icl.integrator.util;

import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by BigBlackBug on 10.05.2014.
 */
public class Tests {

	@Test
	public void test(){
		RuntimeException runtimeException = new RuntimeException();
		ExceptionUtils.getRealSQLError(runtimeException);
	}

	@Test
	public void test2(){
		SQLException ex = new SQLException("PARENT_EXCEPTION");
		ex.setNextException(new SQLException("REAL_EXCEPTION"));
		RuntimeException root = new RuntimeException(ex);
		System.out.println(ExceptionUtils.getRealSQLError(root));
	}

}
