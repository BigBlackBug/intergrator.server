package com.icl.integrator.services;

import com.icl.integrator.dto.Modification;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * Created by BigBlackBug on 02.05.2014.
 */
public class VersioningTests {

	private VersioningService versioningService;

	@Before
	public void before() {
		versioningService = new VersioningService();
	}

	@Test
	public void test1() {
		versioningService.login("vasya");
		Assert.assertEquals(true, versioningService.isAllowedToContinue("vasya"));
		versioningService.logout("vasya");
	}

	@Test
	public void test2() {
		versioningService.login("vasya");
		versioningService.logModification("vasya",new Modification(Modification.SubjectType.ACTION,
		                                                   Modification.ActionType.ADDED,
		                                                   "actionname"));
		versioningService.logModification("vasya",new Modification(Modification.SubjectType.SERVICE,
		                                                   Modification.ActionType.CHANGED,
		                                                   "servname"));
		Assert.assertEquals(true, versioningService.isAllowedToContinue("vasya"));
		List<Modification> vasya = versioningService.fetchModifications("vasya");
		Assert.assertEquals(0, vasya.size());
		versioningService.logout("vasya");
	}

	@Test
	public void test3() {
		versioningService.login("vasya");
		versioningService.logModification("vasya",new Modification(Modification.SubjectType.ACTION,
		                                                   Modification.ActionType.ADDED,
		                                                   "actionname"));
		versioningService.logModification("vasya",new Modification(Modification.SubjectType.SERVICE,
		                                                   Modification.ActionType.CHANGED,
		                                                   "servname"));
		List<Modification> vasya = versioningService.fetchModifications("vasya");
		versioningService.scheduleUserRemoval();
		Set<Modification> modifications = versioningService.getModifications();
		Assert.assertEquals(0, modifications.size());
		versioningService.logout("vasya");
	}

	@Test
	public void test4() {
		versioningService.login("vasya");
		Modification mod1 = new Modification(Modification.SubjectType.ACTION,
		                                     Modification.ActionType.ADDED, "actionname");
		versioningService.logModification("vasya",mod1);
		Modification mod2 = new Modification(Modification.SubjectType.SERVICE,
		                                     Modification.ActionType.CHANGED, "servname");
		versioningService.logModification("vasya",mod2);
		versioningService.login("petya");
		Modification mod3 = new Modification(Modification.SubjectType.ACTION,
		                                     Modification.ActionType.ADDED, "actionname3");
		versioningService.logModification("petya",mod3);
		Modification mod4 = new Modification(Modification.SubjectType.SERVICE,
		                                     Modification.ActionType.CHANGED, "servname4");
		versioningService.logModification("petya",mod4);
		List<Modification> vasya = versioningService.fetchModifications("vasya");
		Assert.assertEquals(2, vasya.size());
		Assert.assertEquals(mod3, vasya.get(0));
		Assert.assertEquals(mod4, vasya.get(1));
//		Assert.assertEquals(mod3, vasya.get(2));
//		Assert.assertEquals(mod4, vasya.get(3));
		versioningService.scheduleUserRemoval();
		Set<Modification> modifications = versioningService.getModifications();
		Assert.assertEquals(0, modifications.size());
		versioningService.logout("vasya");
		versioningService.logout("petya");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test5() {
		versioningService.login("vasya");
		versioningService.logModification("vasya",new Modification(Modification.SubjectType.ACTION,
		                                                   Modification.ActionType.ADDED,
		                                                   "actionname"));
		versioningService.logModification("vasya",new Modification(Modification.SubjectType.SERVICE,
		                                                   Modification.ActionType.CHANGED,
		                                                   "servname"));
		Assert.assertEquals(true, versioningService.isAllowedToContinue("vasya"));
		versioningService.logout("vasya");
		versioningService.fetchModifications("vasya");
	}

	@Test
	public void test6() {
		versioningService.login("vasya");
		Assert.assertEquals(true, versioningService.isAllowedToContinue("vasya"));
		Assert.assertEquals(0, versioningService.fetchModifications("vasya").size());
		versioningService.logout("vasya");
	}
}
