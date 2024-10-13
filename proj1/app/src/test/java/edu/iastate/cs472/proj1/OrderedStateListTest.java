package edu.iastate.cs472.proj1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderedStateListTest {
	@Test
	public void testListOpen() {
		OrderedStateList list = new OrderedStateList(Heuristic.TileMismatch,
				true);

		// 0
		int[][] b1 = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
		State s1 = new State(b1);
		list.addState(s1);
		// 7
		int[][] b2 = {{7, 2, 4}, {5, 0, 6}, {8, 3, 1}};
		State s2 = new State(b2);
		list.addState(s2);
		// 2
		int[][] b3 = {{1, 8, 3}, {2, 0, 4}, {7, 6, 5}};
		State s3 = new State(b3);
		list.addState(s3);
		// 3
		int[][] b4 = {{1, 8, 3}, {2, 7, 4}, {0, 6, 5}};
		State s4 = new State(b4);
		list.addState(s4);
		list.addState(s2);

		assertEquals(s1, list.findState(new State(b1)));
		assertEquals(s2, list.findState(new State(b2)));
		assertEquals(s3, list.findState(new State(b3)));
		assertEquals(s4, list.findState(new State(b4)));

		assertEquals(4, list.size());
		list.removeState(new State(b4));
		assertEquals(3, list.size());
		assertEquals(s1, list.remove());
		assertEquals(s3, list.remove());
		assertEquals(s2, list.remove());
	}

	@Test
	public void testListClosed() {
		OrderedStateList list = new OrderedStateList(Heuristic.TileMismatch,
				false);

		// second
		int[][] b1 = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
		State s1 = new State(b1);
		list.addState(s1);
		// fourth
		int[][] b2 = {{7, 2, 4}, {5, 0, 6}, {8, 3, 1}};
		State s2 = new State(b2);
		list.addState(s2);
		// first
		int[][] b3 = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
		State s3 = new State(b3);
		list.addState(s3);
		// third
		int[][] b4 = {{1, 8, 3}, {2, 7, 4}, {0, 6, 5}};
		State s4 = new State(b4);
		list.addState(s4);

		list.addState(s2);

		assertEquals(s1, list.findState(new State(b1)));
		assertEquals(s2, list.findState(new State(b2)));
		assertEquals(s3, list.findState(new State(b3)));
		assertEquals(s4, list.findState(new State(b4)));

		assertEquals(4, list.size());
		list.removeState(new State(b2));
		assertEquals(3, list.size());

		assertEquals(s3, list.remove());
		assertEquals(s1, list.remove());
		assertEquals(s4, list.remove());
	}
}
