package com.icl.integrator.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BigBlackBug on 29.04.2014.
 */
@Service
public class VersioningService {

	private final Map<String, Long> loggedInUsers = new HashMap<>();

	private long serverState = 1L;

	public void login(String username) {
		loggedInUsers.put(username, serverState);
	}

	public boolean isAllowedToContinue(String username) {
		return loggedInUsers.get(username).equals(serverState);
	}

	public void logout(String username) {
		loggedInUsers.remove(username);
	}

	public void changeServerState() {
		serverState++;
	}

}
