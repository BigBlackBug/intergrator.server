package com.icl.integrator.services;

import com.icl.integrator.dto.Modification;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by BigBlackBug on 15.05.2014.
 */
@Service
//TODO написать чистку
public class VersioningService {

	private final Map<String, Integer> loggedInUsers = new LinkedHashMap<>();

	private final List<Modification> modifications = new LinkedList<>();

//	private Queue<UserVersionEntity> versionEntities = new PriorityQueue<>();

//	Set<Modification> getModifications() {
//		return Collections.unmodifiableSet(new HashSet<>(modifications));
//	}

	public void login(String username) {
		loggedInUsers.put(username, modifications.size());
//		versionEntities.add(new UserVersionEntity(modifications.size(), username));
	}

	public boolean hasAccess(String username, Modification modification) {
		Integer integer = getIndex(username);
		if (integer.equals(modifications.size())) {
			return true;
		}
		List<Modification> mods = modifications.subList(integer, modifications.size());

		return !mods.contains(modification);
	}

	public boolean hasAccess(String username, Modification.Subject subject,
	                         Modification.ActionType actionType,
	                         Modification.ActionType... actionTypes) {
		Integer integer = getIndex(username);
		if (integer.equals(modifications.size())) {
			return true;
		}
		List<Modification> mods = modifications.subList(integer, modifications.size());

		boolean hasChanges = mods.contains(new Modification(actionType, subject));
		for (Modification.ActionType type : actionTypes) {
			hasChanges &= mods.contains(new Modification(type, subject));
		}
		return !hasChanges;
	}

//	public boolean isAllowedToContinue(String username) throws IllegalArgumentException {
//		Integer integer = loggedInUsers.get(username);
//		if (integer == null) {
//			throw new IllegalArgumentException("Пользователь не залогинен");
//		}
//		return integer.equals(modifications.size());
//	}

	public void logout(String username) {
		loggedInUsers.remove(username);
//		versionEntities.remove(new UserVersionEntity(-1, username));
	}

	private Integer getIndex(String username) {
		Integer integer = loggedInUsers.get(username);
		if (integer == null) {
			throw new IllegalArgumentException("Пользователь не залогинен");
		}
		return integer;
	}

	public void logModification(String username, Modification modification) {
		int newValue = getIndex(username) + 1;
		loggedInUsers.put(username, newValue);

		modifications.add(modification);
//		UserVersionEntity entity = new UserVersionEntity(newValue, username);
//		versionEntities.remove(entity);
//		versionEntities.add(entity);
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

//	//	@Scheduled(fixedRate = 60000)
//	public void scheduleUserRemoval() {
//		UserVersionEntity peek = versionEntities.peek();
//		if (peek == null) {
//			return;
//		}
//		Integer startIndex = peek.startIndex;
//		if (startIndex != 0) {
//			ListIterator<Modification> it = modifications.listIterator();
//			for (int i = 0; i < startIndex; i++) {
//				it.next();
//				it.remove();
//			}
//		}
//		Queue<UserVersionEntity> newVersionEntities = new PriorityQueue<>();
//		for (UserVersionEntity versionEntity : versionEntities) {
//			newVersionEntities.add(
//					new UserVersionEntity(versionEntity.startIndex - startIndex,
//					                      versionEntity.username)
//			);
//		}
//		this.versionEntities.clear();
//		this.versionEntities = newVersionEntities;
//	}

//	private static final class UserVersionEntity implements Comparable<UserVersionEntity> {
//
//		private final String username;
//
//		private Integer startIndex;
//
//		private UserVersionEntity(Integer startIndex, String username) {
//			this.startIndex = startIndex;
//			this.username = username;
//		}
//
//		@Override
//		public boolean equals(Object o) {
//			if (this == o) {
//				return true;
//			}
//			if (o == null || getClass() != o.getClass()) {
//				return false;
//			}
//
//			UserVersionEntity versionEntity = (UserVersionEntity) o;
//
//			if (!username.equals(versionEntity.username)) {
//				return false;
//			}
//
//			return true;
//		}
//
//		@Override
//		public int hashCode() {
//			return username.hashCode();
//		}
//
//		@Override
//		public int compareTo(UserVersionEntity other) {
//			return (int) Math.signum(startIndex - other.startIndex);
//		}
//	}

}
