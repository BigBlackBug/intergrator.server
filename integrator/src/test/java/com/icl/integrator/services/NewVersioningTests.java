package com.icl.integrator.services;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by BigBlackBug on 05.05.2014.
 */
public class NewVersioningTests {

	private final static String USER1 = "VASYA";

	private VersioningService vs;

	@Before
	public void a() {
		vs = new VersioningService();
	}

	@Test
	public void test() {
		vs.login(USER1);
//		vs.logModification(USER1,new Modification());
	}

}
