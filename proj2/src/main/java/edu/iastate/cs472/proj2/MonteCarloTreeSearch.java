package edu.iastate.cs472.proj2;

/**
 * @author
 */

import java.util.ArrayList;

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {

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

		MCTree mctree = new MCTree();
		mctree.root = new MCNode(null, null, this);
		mctree.size = 1;

		int[][] initialState = board.saveState();

		while (mctree.size < 1000) {
			mctree.size++;
		}
		// TODO

		board.restoreState(initialState);

		// Return the move for the current state.
		// Here, we simply return the first legal move for demonstration.
		return legalMoves[0];
	}

	// TODO
	//
	// Implement your helper methods here. They include at least the methods for selection,
	// expansion, simulation, and back-propagation.
	//
	// For representation of the search tree, you are suggested (but limited) to use a
	// child-sibling tree already implemented in the two classes CSTree and CSNode (which
	// you may feel free to modify).  If you decide not to use the child-sibling tree, simply
	// remove these two classes.
	//

	MCNode select(MCNode node) {
		if (!node.fullyExpanded())
			return node;

		ArrayList<MCNode> successors = node.successorNodes;

		int maxI = -1;
		for (int i = 0; i < successors.size(); i++) {
			if (successors.get(i).ucb > successors.get(maxI).ucb) {
				maxI = i;
			}
		}

		MCNode suc = successors.get(maxI);
		board.makeMove(suc.move);
		return select(suc);
	}

	MCNode expand(MCNode node) {
		if (node.fullyExpanded())
			throw new IllegalStateException("You should only call expand on " +
					"nodes that are not fully expanded.");

		int firstUnexpanded = node.firstUnexpandedNode();
		CheckersMove move = node.legalMoves[firstUnexpanded];

		board.makeMove(move);

		MCNode child = new MCNode(move, node, this);
		node.successorNodes.add(child);

		return child;
	}
}
