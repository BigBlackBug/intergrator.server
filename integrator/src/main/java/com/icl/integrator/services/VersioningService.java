package com.icl.integrator.services;

import com.icl.integrator.dto.Modification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Методы должны быть синхронизированны снаружи
 * Created by BigBlackBug on 29.04.2014.
 */
@Service
public class VersioningService {

	private final Map<String, Integer> loggedInUsers = new LinkedHashMap<>();

	private final List<Modification> modifications = new LinkedList<>();

	private Queue<UserVersionEntity> versionEntities = new PriorityQueue<>();

	List<Modification> getModifications() {
		return modifications;
	}

	public void login(String username) {
		loggedInUsers.put(username, modifications.size());
		versionEntities.add(new UserVersionEntity(modifications.size(), username));
	}

	public boolean isAllowedToContinue(String username) throws IllegalArgumentException {
		Integer integer = loggedInUsers.get(username);
		if (integer == null) {
			throw new IllegalArgumentException("Пользователь не залогинен");
		}
		return integer.equals(modifications.size());
	}

	public void logout(String username) {
		loggedInUsers.remove(username);
		versionEntities.remove(new UserVersionEntity(-1, username));
	}

	public void logModification(Modification modification) {
		modifications.add(modification);
	}

	private void refreshUserState(String username) {
		logout(username);
		login(username);
	}

	public List<Modification> fetchModifications(String username) throws IllegalArgumentException {
		Integer startIndex = loggedInUsers.get(username);
		if (startIndex == null) {
			throw new IllegalArgumentException("Пользователь не залогинен");
		}
		refreshUserState(username);
		return Collections
				.unmodifiableList(modifications.subList(startIndex, modifications.size()));
	}

	@Scheduled(fixedRate = 60000)
	public void scheduleUserRemoval() {
		UserVersionEntity peek = versionEntities.peek();
		if (peek == null) {
			return;
		}
		Integer startIndex = peek.startIndex;
		if (startIndex != 0) {
			ListIterator<Modification> it = modifications.listIterator();
			for (int i = 0; i < startIndex; i++) {
				it.next();
				it.remove();
			}
		}
		Queue<UserVersionEntity> newVersionEntities = new PriorityQueue<>();
		for (UserVersionEntity versionEntity : versionEntities) {
			newVersionEntities.add(
					new UserVersionEntity(versionEntity.startIndex - startIndex,
					                      versionEntity.username)
			);
		}
		this.versionEntities.clear();
		this.versionEntities = newVersionEntities;
	}

	private static final class UserVersionEntity implements Comparable<UserVersionEntity> {

		private final String username;

		private Integer startIndex;

		private UserVersionEntity(Integer startIndex, String username) {
			this.startIndex = startIndex;
			this.username = username;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			UserVersionEntity versionEntity = (UserVersionEntity) o;

			if (!username.equals(versionEntity.username)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return username.hashCode();
		}

		@Override
		public int compareTo(UserVersionEntity other) {
			return (int) Math.signum(startIndex - other.startIndex);
		}
	}

}
