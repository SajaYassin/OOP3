package Testing;


import Provided.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Solution.StoryTesterImpl;

import java.util.Arrays;

/**
 * This is a minimal test. Write your own tests!
 * Please don't submit your tests!
 */
public class DogTest {

	private StoryTesterImpl tester;
	private String goodStory;
	private String badStory;
	private String derivedStory;
	private String nestedStory;
	private String storyWith3Then;
	private String storyMissGiven;
	private String storyMissWhen;
	private String storyMissThen;
	private String storyWithOrs;
	private String storyDerived2Nested1;
	private String storyDerived2Nested2;
	private Class<?> testClass;
	private Class<?> derivedTestClass;
	private Class<?> derived2TestClass;
	
	@Before
	public void setUp() throws Exception {
		goodStory = "Given a Dog of age 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "Then the house condition is clean";
		
		badStory = "Given a Dog of age 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "Then the house condition is smelly";
		
		derivedStory = "Given a Dog of age 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 15\n"
				+ "When the house is cleaned, the number of hours is 11\n"
				+ "Then the house condition is clean";
		
		nestedStory = "Given a Dog that his age is 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "Then the house condition is clean";

		storyWith3Then = "Given a Dog of age 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "When the dog's name is Max\n"
				+ "Then the house condition is clean and the dog's name is Prince\n"
				+ "When the dog is not taken out for a walk, the number of hours is 2\n"
				+ "When the dog's name is Prince\n"
				+ "Then the house condition is clean and the dog's name is Max\n"
				+ "When the dog is not taken out for a walk, the number of hours is 4\n"
				+ "Then the house condition is clean";

		storyMissGiven = "Given a Boy that his age is 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "Then the house condition is clean";

		storyMissWhen = "Given a Dog that his age is 6\n"
				+ "When the boy is not taken out for a walk, the number of hours is 5\n"
				+ "Then the house condition is clean";

		storyMissThen = "Given a Dog that his age is 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "Then the home condition is clean";

		storyWithOrs = "Given a Dog of age 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "When the dog's name is Max\n"
				+ "Then the house condition is clean and the dog's name is Prince or " +
				"the house condition is smelly and the dog's name is Max or " +
				"the house condition is smelly and the dog's name is Prince\n" // Then supposed to fail
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "When the dog's name is Max\n"
				+ "Then the house condition is smelly and the dog's name is Max or " +
				"the house condition is clean and the dog's name is Prince or " +
				"the house condition is clean and the dog's name is Max or " +
				"the house condition is smelly and the dog's name is Prince"; // Then supposed to success

		storyDerived2Nested1 = "Given a Dog, his age is 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 5\n"
				+ "Then the house condition is clean";

		storyDerived2Nested2 = "Given a Dog with age 6\n"
				+ "When the dog is not taken out for a walk, the number of hours is 15\n"
				+ "When the house is cleaned, the number of hours is 11\n"
				+ "Then the house condition is clean";

		testClass = DogStoryTest.class;
		derivedTestClass = DogStoryDerivedTest.class;
		derived2TestClass = DogStoryDerived2Test.class;
		tester = new StoryTesterImpl();
	}


	
	@Test
	public void test1() throws Exception {
		try {
			tester.testOnInheritanceTree(goodStory, testClass);
			Assert.assertTrue(true);
		} catch (StoryTestException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test2() throws Exception {
		try {
			tester.testOnNestedClasses(badStory, testClass);
			Assert.assertTrue(false);
		} catch (StoryTestException e) {
			Assert.assertTrue(true);
			Assert.assertEquals("Then the house condition is smelly", e.getSentence());
			Assert.assertEquals(Arrays.asList("smelly"), e.getStoryExpected());
			Assert.assertEquals(Arrays.asList("clean"), e.getTestResult());
		}
	}
	
	@Test
	public void test3() throws Exception {
		try {
			tester.testOnNestedClasses(derivedStory, derivedTestClass);
			Assert.assertTrue(true);
		} catch (StoryTestException e) {
			Assert.assertTrue(false);
		}
	}
	@Test
	public void test4() throws Exception {		
		try {
			tester.testOnNestedClasses(nestedStory, derivedTestClass);
			Assert.assertTrue(true);
		} catch (StoryTestException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testWith3Then() throws Exception {
		try {
			tester.testOnNestedClasses(storyWith3Then, testClass);
			Assert.assertTrue(false);
		} catch (StoryTestException e) {
			Assert.assertTrue(true);
			Assert.assertEquals("Then the house condition is clean and the dog's name is Prince", e.getSentence());
			Assert.assertEquals(Arrays.asList("Prince"), e.getStoryExpected());
			Assert.assertEquals(Arrays.asList("Max"), e.getTestResult());
			Assert.assertEquals(2, e.getNumFail());
		}
	}

	@Test
	public void testWordsNotFound() throws Exception {
		try {
			tester.testOnNestedClasses(storyMissGiven, derivedTestClass);
			Assert.assertTrue(false);
		} catch (GivenNotFoundException e) {
			Assert.assertTrue(true);
		}

		try {
			tester.testOnNestedClasses(storyMissWhen, derivedTestClass);
			Assert.assertTrue(false);
		} catch (WhenNotFoundException e) {
			Assert.assertTrue(true);
		}

		try {
			tester.testOnNestedClasses(storyMissThen, derivedTestClass);
			Assert.assertTrue(false);
		} catch (ThenNotFoundException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testThenWithOrs() throws Exception {
		try {
			tester.testOnNestedClasses(storyWithOrs, testClass);
			Assert.assertTrue(false);
		} catch (StoryTestException e) {
			Assert.assertTrue(true);
			Assert.assertEquals("Then the house condition is clean and the dog's name is Prince or " +
					"the house condition is smelly and the dog's name is Max or " +
					"the house condition is smelly and the dog's name is Prince", e.getSentence());
			Assert.assertEquals(Arrays.asList("Prince","smelly","smelly"), e.getStoryExpected());
			Assert.assertEquals(Arrays.asList("Max","clean","clean"), e.getTestResult());
			Assert.assertEquals(1, e.getNumFail());
		}
	}

	@Test
	public void testNestedClasses() throws Exception {
		try {
			tester.testOnNestedClasses(storyDerived2Nested1, derived2TestClass);
			Assert.assertTrue(true);
		} catch (StoryTestException e) {
			Assert.assertTrue(false);
		}

		try {
			tester.testOnNestedClasses(storyDerived2Nested2, derived2TestClass);
			Assert.assertTrue(true);
		} catch (StoryTestException e) {
			Assert.assertTrue(false);
		}
	}

}
