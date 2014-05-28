package com.icl.integrator.services;

import com.icl.integrator.dto.Modification;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by BigBlackBug on 02.05.2014.
 */
public class VersioningTests {

	private VersioningService vs;

	@Before
	public void before() {
		vs = new VersioningService();
	}

	@Test
	public void test1() {
		String username = "vasya";
		vs.login(username);
		String petya = "petya";
		vs.login(petya);
		//кто-то изменил
		Modification.ServiceSubject subject = new Modification.ServiceSubject("SERVICE");
		Modification modification = new Modification(Modification.ActionType.ADDED, subject);
		vs.logModification(petya, modification);
		//доступа васе ессно нет
		Assert.assertFalse(vs.hasAccess(username, subject, Modification.ActionType.ADDED));
		List<Modification> modifications = vs.fetchModifications(username);
		Assert.assertEquals(1, modifications.size());
		Assert.assertEquals(modifications.get(0), modification);
		vs.logout(username);
	}

	@Test
	public void test14() {
		String vasya = "vasya";
		vs.login(vasya);
		String petya = "petya";
		vs.login(petya);
		//сами поменяли
		Modification.ServiceSubject subject = new Modification.ServiceSubject("SERVICE");
		Modification modification = new Modification(Modification.ActionType.ADDED, subject);
		vs.logModification(vasya, modification);
		//доступ  на добавление есть
		Assert.assertTrue(vs.hasAccess(vasya, subject, Modification.ActionType.ADDED));
		List<Modification> modifications = vs.fetchModifications(vasya);
		Assert.assertEquals(0, modifications.size());
		vs.logout(vasya);
	}

	@Test
	public void test2() {
		String username = "vasya";
		vs.login(username);

		String petya = "petya";
		vs.login(petya);

		//кто-то изменил
		Modification.ServiceSubject subject = new Modification.ServiceSubject("SERVICE");
		Modification modification = new Modification(Modification.ActionType.ADDED, subject);
		vs.logModification(petya, modification);
		//доступ для васи есть
		Assert.assertTrue(vs.hasAccess(username, subject, Modification.ActionType.CHANGED));
		vs.logout(username);
	}

	@Test
	public void test3() {
		String username = "vasya";
		vs.login(username);
		Modification.ServiceSubject subject = new Modification.ServiceSubject("SERVICE");
		//доступ для васи есть
		Assert.assertTrue(vs.hasAccess(username, subject, Modification.ActionType.CHANGED));
		vs.logout(username);
	}

}
