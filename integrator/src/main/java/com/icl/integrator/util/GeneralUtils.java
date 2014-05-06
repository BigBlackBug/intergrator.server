package com.icl.integrator.util;

/**
 * Created by BigBlackBug on 06.05.2014.
 */
public class GeneralUtils {

	private static final String IP_MATCHING_REGEXP =
			"\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}\n" +
					"  (?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

	public static boolean isValidIP(String string) {
		return string.matches(IP_MATCHING_REGEXP);
	}

}
