package edu.iastate.cs472.proj1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StateComparatorTest {
	@Test
	public void stateComparatorTest() {
		int[][] b1 = {{2, 0, 3}, {1, 8, 4}, {7, 6, 5}};
		State s1 = new State(b1);
		int[][] b2 = {{2, 8, 1}, {7, 5, 3}, {6, 0, 4}};
		State s2 = new State(b2);

		StateComparator sc = new StateComparator();

		assertEquals(-1, sc.compare(s1, s2));
		assertEquals(1, sc.compare(s2, s1));

		int[][] b3 = {{2, 0, 3}, {1, 8, 4}, {7, 6, 5}};
		State s3 = new State(b1);
		assertEquals(0, sc.compare(s1, s3));
	}
}
