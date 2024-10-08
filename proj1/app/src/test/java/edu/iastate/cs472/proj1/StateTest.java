package edu.iastate.cs472.proj1;

import org.junit.Test;

import static org.junit.Assert.*;

public class StateTest {
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsIllegalExceptionWhenGivenEmptyArray() {
		int[][] board = {};
		new State(board);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsIllegalExceptionWhenOneArrayNotRightSize() {
		int[][] board = {{1, 2, 3}, {4, 5}, {2, 3, 4}};
		new State(board);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorThrowsIllegalExceptionWhenNineEntriesAreNotRespectiveDigits() {
		int[][] board = {{1, 2, 3}, {4, 5, 0}, {2, 3, 4}};
		new State(board);
	}

	@Test
	public void testConstructorGoodWhenNineEntriesAreRespectiveDigits() {
		int[][] board = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		assertArrayEquals(board, s.board);
	}

	@Test
	public void testConstructorSetInstanceVariablesAsNull() {
		int[][] board = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		assertNull(s.previous);
		assertNull(s.next);
		assertNull(s.predecessor);
		assertNull(s.move);
		assertEquals(0, s.numMoves);
	}
}