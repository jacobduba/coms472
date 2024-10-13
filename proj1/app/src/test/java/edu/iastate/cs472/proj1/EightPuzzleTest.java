package edu.iastate.cs472.proj1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EightPuzzleTest {
	@Test
	public void testAStarMismatchedTiles() {
		int[][] b0 = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};
		State s0 = new State(b0);

		String sol = EightPuzzle.AStar(s0, Heuristic.TileMismatch);
		assertEquals("""
				9 moves in total (heuristic: number of mismatched tiles)
				
				8 1 2
				6 3 \s
				7 5 4
				
				RIGHT
				
				8 1 2
				6   3
				7 5 4
				
				RIGHT
				
				8 1 2
				  6 3
				7 5 4
				
				DOWN
				
				  1 2
				8 6 3
				7 5 4
				
				LEFT
				
				1   2
				8 6 3
				7 5 4
				
				LEFT
				
				1 2 \s
				8 6 3
				7 5 4
				
				UP
				
				1 2 3
				8 6 \s
				7 5 4
				
				UP
				
				1 2 3
				8 6 4
				7 5 \s
				
				RIGHT
				
				1 2 3
				8 6 4
				7   5
				
				DOWN
				
				1 2 3
				8   4
				7 6 5
				
				""", sol);
	}


	@Test
	public void testAStarManhattan() {
		int[][] b0 = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};
		State s0 = new State(b0);

		String sol = EightPuzzle.AStar(s0, Heuristic.ManhattanDist);
		assertEquals("""
				9 moves in total (heuristic: the Manhattan distance)
				
				8 1 2
				6 3 \s
				7 5 4
				
				RIGHT
				
				8 1 2
				6   3
				7 5 4
				
				RIGHT
				
				8 1 2
				  6 3
				7 5 4
				
				DOWN
				
				  1 2
				8 6 3
				7 5 4
				
				LEFT
				
				1   2
				8 6 3
				7 5 4
				
				LEFT
				
				1 2 \s
				8 6 3
				7 5 4
				
				UP
				
				1 2 3
				8 6 \s
				7 5 4
				
				UP
				
				1 2 3
				8 6 4
				7 5 \s
				
				RIGHT
				
				1 2 3
				8 6 4
				7   5
				
				DOWN
				
				1 2 3
				8   4
				7 6 5
				
				""", sol);
	}

	@Test
	public void testAStarDoubleMoves() {
		int[][] b0 = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};
		State s0 = new State(b0);

		String sol = EightPuzzle.AStar(s0, Heuristic.DoubleMoveHeuristic);
		assertEquals("""
				6 moves in total (heuristic: double moves allowed)
				
				8 1 2
				6 3 \s
				7 5 4
				
				DBL_RIGHT
				
				8 1 2
				  6 3
				7 5 4
				
				DOWN
				
				  1 2
				8 6 3
				7 5 4
				
				DBL_LEFT
				
				1 2 \s
				8 6 3
				7 5 4
				
				DBL_UP
				
				1 2 3
				8 6 4
				7 5 \s
				
				RIGHT
				
				1 2 3
				8 6 4
				7   5
				
				DOWN
				
				1 2 3
				8 \s 4
				7 6 5
				
				""", sol);
	}


	@Test
	public void testSolve8PuzzleWithExample() {
		int[][] b0 = {{8, 1, 2}, {6, 3, 0}, {7, 5, 4}};
		State s0 = new State(b0);

		String sol = EightPuzzle.solve8Puzzle(s0);
		assertEquals("""
				9 moves in total (heuristic: number of mismatched tiles)
				
				8 1 2
				6 3 \s
				7 5 4
				
				RIGHT
				
				8 1 2
				6   3
				7 5 4
				
				RIGHT
				
				8 1 2
				  6 3
				7 5 4
				
				DOWN
				
				  1 2
				8 6 3
				7 5 4
				
				LEFT
				
				1   2
				8 6 3
				7 5 4
				
				LEFT
				
				1 2 \s
				8 6 3
				7 5 4
				
				UP
				
				1 2 3
				8 6 \s
				7 5 4
				
				UP
				
				1 2 3
				8 6 4
				7 5 \s
				
				RIGHT
				
				1 2 3
				8 6 4
				7   5
				
				DOWN
				
				1 2 3
				8   4
				7 6 5
				
				
				9 moves in total (heuristic: the Manhattan distance)
				
				8 1 2
				6 3 \s
				7 5 4
				
				RIGHT
				
				8 1 2
				6   3
				7 5 4
				
				RIGHT
				
				8 1 2
				  6 3
				7 5 4
				
				DOWN
				
				  1 2
				8 6 3
				7 5 4
				
				LEFT
				
				1   2
				8 6 3
				7 5 4
				
				LEFT
				
				1 2 \s
				8 6 3
				7 5 4
				
				UP
				
				1 2 3
				8 6 \s
				7 5 4
				
				UP
				
				1 2 3
				8 6 4
				7 5 \s
				
				RIGHT
				
				1 2 3
				8 6 4
				7   5
				
				DOWN
				
				1 2 3
				8   4
				7 6 5
				
				
				6 moves in total (heuristic: double moves allowed)
				
				8 1 2
				6 3 \s
				7 5 4
				
				DBL_RIGHT
				
				8 1 2
				  6 3
				7 5 4
				
				DOWN
				
				  1 2
				8 6 3
				7 5 4
				
				DBL_LEFT
				
				1 2 \s
				8 6 3
				7 5 4
				
				DBL_UP
				
				1 2 3
				8 6 4
				7 5 \s
				
				RIGHT
				
				1 2 3
				8 6 4
				7   5
				
				DOWN
				
				1 2 3
				8   4
				7 6 5
				
				""", sol);
	}

}
