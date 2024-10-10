package edu.iastate.cs472.proj1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * @author
 */


/**
 * This class represents a board configuration in the 8-puzzle.  Only the initial configuration is
 * generated by a constructor, while intermediate configurations will be generated via calling
 * the method successorState().  State objects will form two circular doubly-linked lists OPEN and
 * CLOSED, which will be used by the A* algorithm to search for a path from a given initial board
 * configuration to the final board configuration below:
 * <p>
 * 1 2 3
 * 8   4
 * 7 6 5
 * <p>
 * The final configuration (i.e., the goal state) above is not explicitly represented as an object
 * of the State class.
 */

public class State implements Cloneable, Comparable<State> {
	public static Heuristic heu; // heuristic used. shared by all the states.
	final int BOARD_WIDTH = 3;
	public int[][] board;        // configuration of tiles
	public State previous;        // previous node on the OPEN/CLOSED list
	public State next;            // next node on the OPEN/CLOSED list
	public State predecessor;
	// predecessor node on the path from the initial state
	public Move move;
	// the move that generated this state from its predecessor
	public int numMoves;
	// number of moves from the initial state to this state
	private int numMismatchedTiles = -1;
	// number of mismatched tiles between this state
	// and the goal state; negative if not computed yet.
	private int ManhattanDistance = -1;
	// number of single and double moves with each double
	// move counted as one; negative if not computed yet.
	// Manhattan distance between this state and the
	// goal state; negative if not computed yet.
	private int numSingleDoubleMoves = -1;

	/**
	 * Constructor (for the initial state).
	 * <p>
	 * It takes a 2-dimensional array representing an initial board configuration. The empty
	 * square is represented by the number 0.
	 * <p>
	 * a) Initialize all three links previous, next, and predecessor to null.
	 * b) Set move to null and numMoves to zero.
	 *
	 * @param board
	 * @throws IllegalArgumentException if board is not a 3X3 array or its nine entries are
	 *                                  not respectively the digits 0, 1, ..., 8.
	 */
	public State(int[][] board) throws IllegalArgumentException {

		if (board.length != BOARD_WIDTH || board[0].length != BOARD_WIDTH ||
				board[1].length != BOARD_WIDTH || board[2].length !=
				BOARD_WIDTH)
			throw new IllegalArgumentException();

		HashSet<Integer> digitsNotUsed = new HashSet<>();

		for (int i = 0; i < 9; i++) {
			digitsNotUsed.add(i);
		}

		this.board = new int[BOARD_WIDTH][BOARD_WIDTH];

		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				digitsNotUsed.remove(board[i][j]);
				this.board[i][j] = board[i][j];
			}
		}

		if (!digitsNotUsed.isEmpty())
			throw new IllegalArgumentException();
	}


	/**
	 * Constructor (for the initial state)
	 * <p>
	 * It takes a state from an input file that has three rows, each containing three digits
	 * separated by exactly one blank.  Every row starts with a digit. The nine digits are
	 * from 0 to 8 with no duplicates.
	 * <p>
	 * Do the same initializations as for the first constructor.
	 *
	 * @param inputFileName
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException if the file content does not meet the above requirements.
	 */
	public State(String inputFileName)
			throws FileNotFoundException, IllegalArgumentException {
		// TODO handle invalid files???
		board = new int[BOARD_WIDTH][BOARD_WIDTH];

		HashSet<Integer> digitsNotUsed = new HashSet<>();

		for (int i = 0; i < 9; i++) {
			digitsNotUsed.add(i);
		}

		Scanner scan = new Scanner(new File(inputFileName));
		for (int i = 0; scan.hasNext(); i++) {
			board[i / BOARD_WIDTH][i % BOARD_WIDTH] =
					Integer.parseInt(scan.next());
			digitsNotUsed.remove(board[i / BOARD_WIDTH][i % BOARD_WIDTH]);
		}

		if (!digitsNotUsed.isEmpty())
			throw new IllegalArgumentException();
	}


	/**
	 * Generate the successor state resulting from a given move.  Throw an exception if the move
	 * cannot be executed.  Besides setting the array board[][] properly, you also need to do the
	 * following:
	 * <p>
	 * a) set the predecessor of the successor state to this state;
	 * b) set the private instance variable move of the successor state to the parameter m;
	 * c) Set the links next and previous to null;
	 * d) Set the variable numMoves for the successor state to this.numMoves + 1.
	 *
	 * @param m one of the moves LEFT, RIGHT, UP, DOWN, DBL_LEFT, DBL_RIGHT, DBL_UP, and DBL_DOWN
	 * @return null            if the successor state is this.predecessor
	 * successor state  otherwise
	 * @throws IllegalArgumentException if LEFT when the empty square is in the right column, or
	 *                                  if RIGHT when the empty square is in the left column, or
	 *                                  if UP when the empty square is in the bottom row, or
	 *                                  if DOWN when the empty square is in the top row, or
	 *                                  if DBL_LEFT when the empty square is not in the left column, or
	 *                                  if DBL_RIGHT when the empty square is not in the right column, or
	 *                                  if DBL_UP when the empty square is not in the top row, or
	 *                                  if DBL_DOWN when the empty square is not in the bottom row.
	 */
	public State successorState(Move m) throws IllegalArgumentException {
		// TODO
		int row = -1, col = -1;

		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (board[i][j] == 0) {
					row = i;
					col = j;
				}
			}
		}

		if ((col == 2 && m == Move.LEFT)
				|| (col == 0 && m == Move.RIGHT)
				|| (row == 2 && m == Move.UP)
				|| (row == 0 && m == Move.DOWN)
				|| (col != 0 && m == Move.DBL_LEFT)
				|| (col != 2 && m == Move.DBL_RIGHT)
				|| (row != 0 && m == Move.DBL_UP)
				|| (row != 2 && m == Move.DBL_DOWN)) {
			throw new IllegalArgumentException();
		}

		// successor
		State s = (State) this.clone();

		if (m == Move.LEFT) {
			s.board[row][col] = s.board[row][col + 1];
			s.board[row][col + 1] = 0;
		} else if (m == Move.RIGHT) {
			s.board[row][col] = s.board[row][col - 1];
			s.board[row][col - 1] = 0;
		} else if (m == Move.UP) {
			s.board[row][col] = s.board[row + 1][col];
			s.board[row + 1][col] = 0;
		} else if (m == Move.DOWN) {
			s.board[row][col] = s.board[row - 1][col];
			s.board[row - 1][col] = 0;
		} else if (m == Move.DBL_LEFT) {
			s.board[row][0] = s.board[row][1];
			s.board[row][1] = s.board[row][2];
			s.board[row][2] = 0;
		} else if (m == Move.DBL_RIGHT) {
			s.board[row][2] = s.board[row][1];
			s.board[row][1] = s.board[row][0];
			s.board[row][0] = 0;
		} else if (m == Move.DBL_UP) {
			s.board[0][col] = s.board[1][col];
			s.board[1][col] = s.board[2][col];
			s.board[2][col] = 0;
		} else if (m == Move.DBL_DOWN) {
			s.board[2][col] = s.board[1][col];
			s.board[1][col] = s.board[0][col];
			s.board[0][col] = 0;
		}

		s.move = m;
		s.predecessor = this;
		s.numMoves = numMoves + 1;

		return s;
	}


	/**
	 * Determines if the board configuration in this state can be rearranged into the goal configuration.
	 * According to the PowerPoint notes that introduce the 8-puzzle, we check if this state has an odd number
	 * of inversions.
	 */
	/**
	 * @return true if the puzzle starting in this state can be rearranged into the goal state.
	 */
	public boolean solvable() {
		final int NUM_TILES = BOARD_WIDTH * BOARD_WIDTH;

		int inversions = 0;

		for (int i = NUM_TILES - 1; i > -1; i--) {
			int cur = board[i / BOARD_WIDTH][i % BOARD_WIDTH];
			for (int j = i + 1; j < NUM_TILES; j++) {
				if (cur > board[j / BOARD_WIDTH][j % BOARD_WIDTH]) {
					inversions++;
				}
			}
		}

		return inversions % 2 == 1;
	}


	/**
	 * Check if this state is the goal state, namely, if the array board[][] stores the following contents:
	 * <p>
	 * 1 2 3
	 * 8 0 4
	 * 7 6 5
	 *
	 * @return
	 */
	public boolean isGoalState() {
		return board[0][0] == 1
				&& board[0][1] == 2
				&& board[0][2] == 3
				&& board[1][0] == 8
				&& board[1][1] == 0
				&& board[1][2] == 4
				&& board[2][0] == 7
				&& board[2][1] == 6
				&& board[2][2] == 5;
	}


	private String fmtToS(int s) {
		if (s == 0)
			return " ";

		return Integer.toString(s);
	}

	/**
	 * Write the board configuration according to the following format:
	 * <p>
	 * a) Output row by row in three lines with no indentations.
	 * b) Two adjacent tiles in each row have exactly one blank in between.
	 * c) The empty square is represented by a blank.
	 * <p>
	 * For example,
	 * <p>
	 * 2   3
	 * 1 8 4
	 * 7 6 5
	 */
	@Override
	public String toString() {
		return fmtToS(board[0][0]) + " "
				+ fmtToS(board[0][1]) + " "
				+ fmtToS(board[0][2]) + "\n"
				+ fmtToS(board[1][0]) + " "
				+ fmtToS(board[1][1]) + " "
				+ fmtToS(board[1][2]) + "\n"
				+ fmtToS(board[2][0]) + " "
				+ fmtToS(board[2][1]) + " "
				+ fmtToS(board[2][2]);
	}


	/**
	 * Create a clone of this State object by copying over the board[][]. Set the links previous,
	 * next, and predecessor to null.
	 * <p>
	 * The method is called by SuccessorState();
	 */
	@Override
	public Object clone() {
		int[][] boardClone = new int[BOARD_WIDTH][BOARD_WIDTH];

		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				boardClone[i][j] = board[i][j];
			}
		}

		return new State(boardClone);
	}


	/**
	 * Compare this state with the argument state.  Two states are equal if their arrays board[][]
	 * have the same content.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof State))
			return false;

		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (board[i][j] != ((State) o).board[i][j])
					return false;
			}
		}

		return true;
	}


	/**
	 * Evaluate the cost of this state as the sum of the number of moves from the initial state and
	 * the estimated number of moves to the goal state using the heuristic stored in the instance
	 * variable heu.
	 * <p>
	 * If heu == TileMismatch, add up numMoves and the return values from computeNumMismatchedTiles().
	 * If heu == MahattanDist, add up numMoves and the return values of computeMahattanDistance().
	 * If heu == DoubleMoveHeuristic, add up numMoves and the return value of computeNumSingleDoubleMoves().
	 *
	 * @return estimated number of moves from the initial state to the goal state via this state.
	 * @throws IllegalArgumentException if heuristic is none of TileMismatch, MahattanDist, DoubleMoveHeuristic.
	 */
	public int cost() throws IllegalArgumentException {
		if (heu == Heuristic.TileMismatch) {
			return numMoves + computeNumMismatchedTiles();
		} else if (heu == Heuristic.ManhattanDist) {
			return numMoves + computeManhattanDistance();
		}

		return numMoves + computeNumSingleDoubleMoves();
	}


	/**
	 * Compare two states by the cost. Let c1 and c2 be the costs of this state and the argument state s.
	 *
	 * @return -1 if c1 < c2
	 * 0 if c1 = c2
	 * 1 if c1 > c2
	 * <p>
	 * Call the method cost(). This comparison will be used in maintaining the OPEN list by the A* algorithm.
	 */
	@Override
	public int compareTo(State s) {
		if (this.cost() < s.cost()) return -1;
		if (this.cost() == s.cost()) return 0;
		// this.cost() > s.cost()
		return -1;
	}


	/**
	 * Return the value of the private variable numMismatchedTiles if it is non-negative, and compute its
	 * value otherwise.
	 *
	 * @return the number of mismatched tiles between this state and the goal state.
	 */
	private int computeNumMismatchedTiles() {
		if (this.numMismatchedTiles > -1) {
			return this.numMismatchedTiles;
		}

		numMismatchedTiles = 0;

		if (board[0][0] != 1) numMismatchedTiles++;
		if (board[0][1] != 2) numMismatchedTiles++;
		if (board[0][2] != 3) numMismatchedTiles++;
		if (board[1][2] != 4) numMismatchedTiles++;
		if (board[2][2] != 5) numMismatchedTiles++;
		if (board[2][1] != 6) numMismatchedTiles++;
		if (board[2][0] != 7) numMismatchedTiles++;
		if (board[1][0] != 8) numMismatchedTiles++;

		return numMismatchedTiles;
	}


	/**
	 * Return the value of the private variable ManhattanDistance if it is non-negative, and compute its value
	 * otherwise.
	 *
	 * @return the Manhattan distance between this state and the goal state.
	 */
	private int computeManhattanDistance() {
		if (ManhattanDistance > -1) {
			return ManhattanDistance;
		}

		ManhattanDistance = 0;

		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				int block = board[i][j];

				if (block == 1) {
					ManhattanDistance += Math.abs(i - 0) + Math.abs(j - 0);
				} else if (block == 2) {
					ManhattanDistance += Math.abs(i - 0) + Math.abs(j - 1);
				} else if (block == 3) {
					ManhattanDistance += Math.abs(i - 0) + Math.abs(j - 2);
				} else if (block == 4) {
					ManhattanDistance += Math.abs(i - 1) + Math.abs(j - 2);
				} else if (block == 5) {
					ManhattanDistance += Math.abs(i - 2) + Math.abs(j - 2);
				} else if (block == 6) {
					ManhattanDistance += Math.abs(i - 2) + Math.abs(j - 1);
				} else if (block == 7) {
					ManhattanDistance += Math.abs(i - 2) + Math.abs(j - 0);
				} else if (block == 8) {
					ManhattanDistance += Math.abs(i - 1) + Math.abs(j - 0);
				}
			}
		}

		return ManhattanDistance;
	}


	/**
	 * Return the value of the private variable numSingleDoubleMoves if it is non-negative, and compute its value
	 * otherwise.
	 *
	 * @return the value of the private variable numSingleDoubleMoves that bounds from below the number of moves,
	 * single or double, which will take this state to the goal state.
	 */
	private int computeNumSingleDoubleMoves() {
		// TODO 
		return 0;
	}
}
