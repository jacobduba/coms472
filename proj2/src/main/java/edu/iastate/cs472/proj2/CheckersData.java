package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

	static final int EMPTY = 0, RED = 1, RED_KING = 2, BLACK = 3, BLACK_KING =
			4;


	int[][] board;  // board[r][c] is the contents of row r, column c.


	/**
	 * Constructor.  Create the board and set it up for a new game.
	 */
	CheckersData() {
		board = new int[8][8];
		setUpGame();
	}

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_YELLOW = "\u001B[33m";

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < board.length; i++) {
			int[] row = board[i];
			sb.append(8 - i).append(" ");
			for (int n : row) {
				if (n == 0) {
					sb.append(" ");
				} else if (n == 1) {
					sb.append(ANSI_RED + "R" + ANSI_RESET);
				} else if (n == 2) {
					sb.append(ANSI_RED + "K" + ANSI_RESET);
				} else if (n == 3) {
					sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
				} else if (n == 4) {
					sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
				}
				sb.append(" ");
			}
			sb.append(System.lineSeparator());
		}
		sb.append("  a b c d e f g h");

		return sb.toString();
	}

	/**
	 * Set up the board with checkers in position for the beginning
	 * of a game.  Note that checkers can only be found in squares
	 * that satisfy  row % 2 == col % 2.  At the start of the game,
	 * all such squares in the first three rows contain black squares
	 * and all such squares in the last three rows contain red squares.
	 */
	void setUpGame() {
		// Set up the board with pieces BLACK, RED, and EMPTY
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if ((col % 2 == 0 && row % 2 == 0) ||
						(col % 2 == 1 && row % 2 == 1)) {
					if (row < 3) {
						board[row][col] = BLACK;
					} else if (row > 4) {
						board[row][col] = RED;
					} else {
						board[row][col] = EMPTY;
					}
				}
			}
		}
	}


	/**
	 * Return the contents of the square in the specified row and column.
	 */
	int pieceAt(int row, int col) {
		return board[row][col];
	}


	/**
	 * Make the specified move.  It is assumed that move
	 * is non-null and that the move it represents is legal.
	 * <p>
	 * Make a single move or a sequence of jumpsa
	 * recorded in rows and cols.
	 */
	void makeMove(CheckersMove move) {
		int l = move.rows.size();
		for (int i = 0; i < l - 1; i++)
			makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i + 1),
					move.cols.get(i + 1));
	}


	/**
	 * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
	 * assumed that this move is legal.  If the move is a jump, the
	 * jumped piece is removed from the board.  If a piece moves to
	 * the last row on the opponent's side of the board, the
	 * piece becomes a king.
	 *
	 * @param fromRow row index of the from square
	 * @param fromCol column index of the from square
	 * @param toRow   row index of the to square
	 * @param toCol   column index of the to square
	 */
	void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
		// TODO
		//
		// Update the board for the given move. You need to take care of the following situations:
		// 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
		// 2. if this move is a jump, remove the captured piece
		// 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king

		int piece = board[fromRow][fromCol];
		board[fromRow][fromCol] = EMPTY;
		board[toRow][toCol] = piece;

		if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
			board[fromRow + (toRow - fromRow) / 2][fromCol +
					(toCol - fromCol) / 2] = EMPTY;
		}

		if (piece == RED && toRow == 0) {
			board[toRow][toCol] = RED_KING;
		} else if (piece == BLACK && toRow == 7) {
			board[toRow][toCol] = BLACK_KING;
		}
	}

	boolean tileIsInBounds(int row, int col) {
		return -1 < row && row < 8 && -1 < col && col < 8;
	}

	boolean tileIsEmpty(int row, int col) {
		return tileIsInBounds(row, col) && board[row][col] == EMPTY;
	}

	boolean tileIsOpponentOf(int row, int col, int player) {
		return tileIsInBounds(row, col) && ((player == RED &&
				(board[row][col] == BLACK || board[row][col] == BLACK_KING)) ||
				(player == BLACK && (board[row][col] == RED ||
						board[row][col] == RED_KING)));
	}

	/**
	 * Return an array containing all the legal CheckersMoves
	 * for the specified player on the current board.  If the player
	 * has no legal moves, null is returned.  The value of player
	 * should be one of the constants RED or BLACK; if not, null
	 * is returned.  If the returned value is non-null, it consists
	 * entirely of jump moves or entirely of regular moves, since
	 * if the player can jump, only jumps are legal moves.
	 *
	 * @param player color of the player, RED or BLACK
	 */
	CheckersMove[] getLegalMoves(int player) {
		if (player != RED && player != BLACK)
			return null;

		ArrayList<CheckersMove> legalJumps = new ArrayList<CheckersMove>();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				CheckersMove[] legalJumpsFromTile =
						getLegalJumpsFrom(player, row, col);
				if (legalJumpsFromTile != null)
					legalJumps.addAll(Arrays.asList(legalJumpsFromTile));
			}
		}

		if (!legalJumps.isEmpty())
			return legalJumps.toArray(new CheckersMove[0]);

		ArrayList<CheckersMove> legalMoves = new ArrayList<>();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if ((player == RED && (board[row][col] == RED ||
						board[row][col] == RED_KING)) ||
						(player == BLACK && board[row][col] == BLACK_KING)) {
					if (tileIsEmpty(row - 1, col - 1))
						legalMoves.add(new CheckersMove(row, col, row - 1,
								col - 1));

					if (tileIsEmpty(row - 1, col + 1))
						legalMoves.add(new CheckersMove(row, col, row - 1,
								col + 1));
				}

				if ((player == BLACK && (board[row][col] == BLACK ||
						board[row][col] == BLACK_KING)) ||
						(player == RED && board[row][col] == RED_KING)) {
					if (tileIsEmpty(row + 1, col - 1))
						legalMoves.add(new CheckersMove(row, col, row + 1,
								col - 1));

					if (tileIsEmpty(row + 1, col + 1))
						legalMoves.add(new CheckersMove(row, col, row + 1,
								col + 1));
				}
			}

		}

		if (legalMoves.isEmpty())
			return null;

		return legalMoves.toArray(new CheckersMove[0]);
	}


	private void exploreJump(int row, int col, int rowOffset, int colOffset,
							 int player, ArrayList<CheckersMove> moves,
							 int[][] initialState) {
		if (tileIsOpponentOf(row + rowOffset, col + colOffset, player) &&
				tileIsEmpty(row + 2 * rowOffset, col + 2 * colOffset)) {

			makeMove(row, col, row + 2 * rowOffset, col + 2 * colOffset);

			CheckersMove[] pathMoves =
					getLegalJumpsFrom(player, row + 2 * rowOffset,
							col + 2 * colOffset);

			if (pathMoves == null) {
				moves.add(new CheckersMove(row, col, row + 2 * rowOffset,
						col + 2 * colOffset));
			} else {
				for (CheckersMove move : pathMoves) {
					CheckersMove combinedMove = new CheckersMove();
					combinedMove.addMove(row, col);
					for (int i = 0; i < move.cols.size(); i++) {
						combinedMove.addMove(move.rows.get(i),
								move.cols.get(i));
					}
					moves.add(combinedMove);
				}
			}

			// restore initial state to backtrack moves made
			restoreState(initialState);
		}
	}

	/**
	 * Return a list of the legal jumps that the specified player can
	 * make starting from the specified row and column.  If no such
	 * jumps are possible, null is returned.  The logic is similar
	 * to the logic of the getLegalMoves() method.
	 * <p>
	 * Note that each CheckerMove may contain multiple jumps.
	 * Each move returned in the array represents a sequence of jumps
	 * until no further jump is allowed.
	 * another
	 *
	 * @param player The player of the current jump, either RED or BLACK.
	 * @param row    row index of the start square.
	 * @param col    col index of the start square.
	 */
	CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
		if ((player != RED ||
				(board[row][col] != RED && board[row][col] != RED_KING)) &&
				(player != BLACK || (board[row][col] != BLACK &&
						board[row][col] != BLACK_KING))) {
			return null;
		}

		ArrayList<CheckersMove> legalJumps = new ArrayList<>();

		// Save initial state for backtracking
		int[][] initialState = saveState();

		if (player == RED || board[row][col] == BLACK_KING) {
			exploreJump(row, col, -1, -1, player, legalJumps, initialState);
			exploreJump(row, col, -1, 1, player, legalJumps, initialState);
		}

		if (player == BLACK || board[row][col] == RED_KING) {
			exploreJump(row, col, 1, -1, player, legalJumps, initialState);
			exploreJump(row, col, 1, 1, player, legalJumps, initialState);
		}

		if (legalJumps.isEmpty()) return null;

		return legalJumps.toArray(new CheckersMove[0]);
	}

	int[][] saveState() {
		int[][] state = new int[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				state[i][j] = board[i][j];
			}
		}

		return state;
	}

	void restoreState(int[][] state) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = state[i][j];
			}
		}
	}
}
