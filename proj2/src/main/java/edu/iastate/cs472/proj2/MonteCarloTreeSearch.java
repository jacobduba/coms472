package edu.iastate.cs472.proj2;

/**
 * @author Jacob Duba
 */

import java.util.ArrayList;
import java.util.Random;

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {
	final static int MAX_MOVE_COUNT = 100;
	final static double c = Math.sqrt(2);
	final static int DRAW = 0;
	final static double CENTER_SQUARE_BONUS = 0.3;
	final static double BACK_ROW_BONUS = 0.2;
	final static double PROTECTED_PIECE_BONUS = 0.25;

	/**
	 * The input parameter legalMoves contains all the possible moves.
	 * It contains four integers:  fromRow, fromCol, toRow, toCol
	 * which represents a move from (fromRow, fromCol) to (toRow, toCol).
	 * It also provides a utility method `isJump` to see whether this
	 * move is a jump or a simple move.
	 * <p>
	 * Each legalMove in the input now contains a single move
	 * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
	 * (rows[2], cols[2]).
	 *
	 * @param legalMoves All the legal moves for the agent at current step.
	 */
	public CheckersMove makeMove(CheckersMove[] legalMoves) {
		// The checker board state can be obtained from this.board,
		// which is an 2D array of the following integers defined below:
		//
		// 0 - empty square,
		// 1 - red man
		// 2 - red king
		// 3 - black man
		// 4 - black king
		System.out.println(board);
		System.out.println();

		MCTree tree = new MCTree();

		MCNode rootNode = new MCNode(null, null, CheckersData.RED);
		rootNode.legalMoves = board.getLegalMoves(CheckersData.BLACK);

		if (rootNode.legalMoves == null)
			return null;

		tree.root = rootNode;
		tree.size = 1;

		int[][] initialState = board.saveState();

		while (tree.size < 5000) {
			MCNode leaf = select(tree.root);
			MCNode child;
			int simWinner;
			if (leaf.legalMoves == null) {
				child = leaf;
				simWinner = opponent(child.player);
			} else {
				child = expand(leaf);
				simWinner = simulation(child);
			}
			tree.size++;
			backPropagate(child, simWinner);
			board.restoreState(initialState);
		}

		ArrayList<MCNode> sucList = tree.root.successorNodes;
		int maxI = 0;
		for (int i = 0; i < tree.root.successorNodes.size(); i++) {
			if (sucList.get(i).playouts > sucList.get(maxI).playouts)
				maxI = i;
		}

		return sucList.get(maxI).move;
	}

	// Implement your helper methods here. They include at least the methods for selection,
	// expansion, simulation, and back-propagation.
	//
	// For representation of the search tree, you are suggested (but limited) to use a
	// child-sibling tree already implemented in the two classes CSTree and CSNode (which
	// you may feel free to modify).  If you decide not to use the child-sibling tree, simply
	// remove these two classes.
	//

	double findUcb(MCNode node) {
		return ((double) node.wins / node.playouts) +
				c * Math.sqrt(Math.log(node.parent.playouts) / node.playouts);
	}

	int opponent(int player) {
		if (player == CheckersData.RED)
			return CheckersData.BLACK;

		return CheckersData.RED;
	}

	MCNode select(MCNode node) {
		if (!node.fullyExpanded())
			return node;

		ArrayList<MCNode> successors = node.successorNodes;

		double maxUcb = -1;
		int maxI = -1;
		for (int i = 0; i < successors.size(); i++) {
			double curUcb = findUcb(successors.get(i));
			if (curUcb > maxUcb) {
				maxI = i;
				maxUcb = curUcb;
			}
		}

		MCNode suc = successors.get(maxI);
		board.makeMove(suc.move);
		return select(suc);
	}

	MCNode expand(MCNode leaf) {
		if (leaf.fullyExpanded())
			throw new IllegalStateException("You should only call expand on " +
					"nodes that are not fully expanded.");

		int firstUnexpanded = leaf.firstUnexpandedNode();
		CheckersMove move = leaf.legalMoves[firstUnexpanded];

		int opponent = opponent(leaf.player);

		MCNode child = new MCNode(move, leaf, opponent);

		board.makeMove(move);
		child.legalMoves = board.getLegalMoves(opponent);

		leaf.successorNodes.add(child);

		return child;
	}

	// Returns which player won game with random moves or 0 for draw
	int simulation(MCNode child) {
		Random rand = new Random();

		// start with next player after child player played
		int player = opponent(child.player);
		CheckersMove[] legalMoves = board.getLegalMoves(player);

		int moveCount = 0;

		while (legalMoves != null && moveCount++ < MAX_MOVE_COUNT) {
			CheckersMove randMove = legalMoves[rand.nextInt(legalMoves.length)];

			board.makeMove(randMove);

			player = opponent(player);
			legalMoves = board.getLegalMoves(player);
		}

		// heuristic to determine winner (count up score)
		if (moveCount == MAX_MOVE_COUNT) {
			double score = evaluatePosition();

			if (score == 0) {
				return DRAW;
			} else if (score > 0) {
				return CheckersData.RED;
			} else {
				return CheckersData.BLACK;
			}
		}

		// player is the looser, so opponent is winner
		return opponent(player);
	}

	void backPropagate(MCNode node, int winner) {
		if (node == null)
			return;

		node.playouts++;

		if (winner == DRAW)
			node.wins += 0.5;
		else if (node.player == winner)
			node.wins++;

		backPropagate(node.parent, winner);
	}

	private double evaluatePosition() {
		double redScore = 0, blackScore = 0;

		// Count material and position
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int piece = board.pieceAt(row, col);
				switch (piece) {
					case CheckersData.RED:
						redScore += 10;
						// Bonus for advancement
						redScore += (7 - row) * 0.5;
						// Center control bonus
						if ((row == 3 || row == 4) && (col == 3 || col == 4)) {
							redScore += CENTER_SQUARE_BONUS;
						}
						// Back row bonus
						if (row == 7) {
							redScore += BACK_ROW_BONUS;
						}
						// Protected piece bonus
						if (isProtected(row, col, CheckersData.RED)) {
							redScore += PROTECTED_PIECE_BONUS;
						}
						break;

					case CheckersData.RED_KING:
						redScore += 30;
						if (isProtected(row, col, CheckersData.RED)) {
							redScore += PROTECTED_PIECE_BONUS;
						}
						break;

					case CheckersData.BLACK:
						blackScore += 10;
						// Bonus for advancement
						blackScore += row * 0.5;
						// Center control
						if ((row == 3 || row == 4) && (col == 3 || col == 4)) {
							blackScore += CENTER_SQUARE_BONUS;
						}
						// Back row bonus
						if (row == 0) {
							blackScore += BACK_ROW_BONUS;
						}
						// Protected piece bonus
						if (isProtected(row, col, CheckersData.BLACK)) {
							blackScore += PROTECTED_PIECE_BONUS;
						}
						break;

					case CheckersData.BLACK_KING:
						blackScore += 30;
						if (isProtected(row, col, CheckersData.BLACK)) {
							blackScore += PROTECTED_PIECE_BONUS;
						}
						break;
				}
			}
		}

		// Factor in mobility
		redScore += getMobilityScore(CheckersData.RED) * 0.2;
		blackScore += getMobilityScore(CheckersData.BLACK) * 0.2;

		return redScore - blackScore;
	}

	private boolean isProtected(int row, int col, int player) {
		// Check if piece is protected by friendly pieces
		int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
		int protectedCount = 0;

		for (int[] dir : directions) {
			int newRow = row + dir[0];
			int newCol = col + dir[1];
			if (board.tileIsInBounds(newRow, newCol)) {
				int piece = board.pieceAt(newRow, newCol);
				if (player == CheckersData.RED &&
						(piece == CheckersData.RED || piece == CheckersData.RED_KING)) {
					protectedCount++;
				} else if (player == CheckersData.BLACK &&
						(piece == CheckersData.BLACK || piece == CheckersData.BLACK_KING)) {
					protectedCount++;
				}
			}
		}

		return protectedCount > 0;
	}

	private int getMobilityScore(int player) {
		CheckersMove[] moves = board.getLegalMoves(player);
		if (moves == null) return 0;

		int mobilityScore = 0;
		for (CheckersMove move : moves) {
			if (move.isJump()) {
				mobilityScore += 3;  // Jumps worth more
			} else {
				mobilityScore += 1;
			}
		}
		return mobilityScore;
	}
}
