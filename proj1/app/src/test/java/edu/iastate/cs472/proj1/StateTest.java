package edu.iastate.cs472.proj1;

import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class StateTest {
	final String FILE_NAME_1 = "src/test/resources/8Puzzle1.txt";
	final String FILE_NAME_INVALID_1 = "src/test/resources/8Puzzle-invalid1" +
			".txt";

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

	@Test
	public void testConstructorLoadFromFile() throws FileNotFoundException {
		State state = new State(FILE_NAME_1);
		int[][] expectedBoard = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};
		assertArrayEquals(expectedBoard, state.board);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorLoadFromFileWithoutAllDigits()
			throws FileNotFoundException {
		State state = new State(FILE_NAME_INVALID_1);
	}

	@Test
	public void testToString() {
		int[][] board = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		assertEquals("""
				  1 2
				3 4 5
				6 7 8""", s.toString());
	}

	@Test
	public void testIsGoalStateButIsNot() {
		int[][] board = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		assertFalse(s.isGoalState());
	}

	@Test
	public void testGoalStateButIs() {
		int[][] board = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
		State s = new State(board);
		assertTrue(s.isGoalState());
	}

	@Test
	public void testIsSolvableUsingGoalState() {
		int[][] board = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
		State s = new State(board);
		assertTrue(s.solvable());
	}

	@Test
	public void testIsSolvableUsingLectureExample() {
		int[][] board = {{4, 1, 2}, {5, 3, 0}, {8, 6, 7}};
		State s = new State(board);
		assertTrue(s.solvable());
	}

	@Test
	public void testIsSolvableUsingZeroInversion() {
		int[][] board = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
		State s = new State(board);
		assertFalse(s.solvable());
	}

	@Test
	public void testIsSolvableUsingZeroInversionNoZeroInMiddle() {
		int[][] board = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s = new State(board);
		assertFalse(s.solvable());
	}

	@Test
	public void testClone() {
		int[][] board = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s = new State(board);
		State sc = (State) s.clone();
		assertArrayEquals(s.board, sc.board);
		assertNotEquals(s.board, sc.board);
	}

	@Test
	public void testEqualsUsingTwoIdenticalBoards() {
		int[][] board1 = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s1 = new State(board1);
		int[][] board2 = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s2 = new State(board2);
		assertTrue(s1.equals(s2));
	}

	@Test
	public void testEqualsAndCloneIsTrue() {
		int[][] board1 = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s1 = new State(board1);
		Object s2 = s1.clone();
		assertTrue(s1.equals(s2));
	}

	@Test
	public void testNotEqualsValidBoards() {
		int[][] board1 = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s1 = new State(board1);
		int[][] board2 = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};
		State s2 = new State(board2);
		assertFalse(s1.equals(s2));
	}

	@Test
	public void successorStateUsingProjectExampleNumberMismatchedTiles() {
		int[][] board = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};

		State s1 = new State(board);
		State s2 = s1.successorState(Move.RIGHT);
		State s3 = s2.successorState(Move.RIGHT);
		State s4 = s3.successorState(Move.DOWN);
		State s5 = s4.successorState(Move.LEFT);
		State s6 = s5.successorState(Move.LEFT);
		State s7 = s6.successorState(Move.UP);
		State s8 = s7.successorState(Move.UP);
		State s9 = s8.successorState(Move.RIGHT);
		State s10 = s9.successorState(Move.DOWN);

		int[][] expectedBoard = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};

		assertArrayEquals(expectedBoard, s10.board);
		assertEquals(s9, s10.predecessor);
		assertNull(s10.next);
		assertNull(s10.previous);
		assertEquals(9, s10.numMoves);
		assertEquals(Move.DOWN, s10.move);
	}

	@Test
	public void successorStateUsingProjectExampleDoublesAllowed() {
		int[][] board = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};

		State s1 = new State(board);
		State s2 = s1.successorState(Move.DBL_RIGHT);
		State s3 = s2.successorState(Move.DOWN);
		// Go up and down to test DBL_DOWN
		State s4 = s3.successorState(Move.DBL_UP);
		State s5 = s4.successorState(Move.DBL_DOWN);
		State s6 = s5.successorState(Move.DBL_LEFT);
		State s7 = s6.successorState(Move.DBL_UP);
		State s8 = s7.successorState(Move.RIGHT);
		State s9 = s8.successorState(Move.DOWN);

		int[][] expectedBoard = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
		assertArrayEquals(expectedBoard, s9.board);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenLeftOnRightCol() {
		int[][] board = {{1, 2, 0}, {3, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		s.successorState(Move.LEFT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenRightOnLeftCol() {
		int[][] board = {{1, 2, 3}, {0, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		s.successorState(Move.RIGHT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenUpOnBottomRow() {
		int[][] board = {{1, 2, 3}, {4, 5, 6}, {7, 0, 8}};
		State s = new State(board);
		s.successorState(Move.UP);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenDownOnTopRow() {
		int[][] board = {{1, 2, 0}, {3, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		s.successorState(Move.DOWN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenDLeftNotInLeftCol() {
		int[][] board = {{1, 0, 2}, {3, 4, 5}, {6, 7, 8}};
		State s = new State(board);
		s.successorState(Move.DBL_LEFT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenDRightNotInRightCol() {
		int[][] board = {{1, 2, 3}, {4, 5, 6}, {7, 0, 8}};
		State s = new State(board);
		s.successorState(Move.DBL_RIGHT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenDUpNotInTopRow() {
		int[][] board = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s = new State(board);
		s.successorState(Move.DBL_UP);
	}

	@Test(expected = IllegalArgumentException.class)
	public void successorStateThrowsIllegalArgumentWhenDDownNotInBottomRow() {
		int[][] board = {{1, 2, 3}, {4, 0, 5}, {6, 7, 8}};
		State s = new State(board);
		s.successorState(Move.DBL_DOWN);
	}

	@Test
	public void numMismatchedTilesTest() {
		State.heu = Heuristic.TileMismatch;

		int[][] board1 = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
		State s1 = new State(board1);
		assertEquals(0, s1.cost());

		int[][] board2 = {{7, 2, 4}, {5, 0, 6}, {8, 3, 1}};
		State s2 = new State(board2);
		assertEquals(7, s2.cost());
	}

	@Test
	public void manhattenDistanceTest() {
		State.heu = Heuristic.ManhattanDist;

		int[][] board1 = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
		State s1 = new State(board1);
		assertEquals(0, s1.cost());

		int[][] board2 = {{7, 2, 4}, {5, 0, 6}, {8, 3, 1}};
		State s2 = new State(board2);
		assertEquals(16, s2.cost());

		int[][] board3 = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
		State s3 = new State(board3);
		assertEquals(12, s3.cost());
	}
}