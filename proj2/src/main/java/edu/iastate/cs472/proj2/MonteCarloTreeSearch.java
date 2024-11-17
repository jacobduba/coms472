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
	final static double c = Math.sqrt(2);

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

		while (tree.size < 1000) {
			MCNode leaf = select(tree.root);
			MCNode child = expand(leaf);
			tree.size++;
			int simWinner = simulation(child);
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

	// TODO WHAT IF A PLAYER WINS IN THE TREE
	//
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

	// Returns which player won game with random moves
	int simulation(MCNode child) {
		Random rand = new Random();

		// start with next player after child player played
		int player = opponent(child.player);
		CheckersMove[] legalMoves = board.getLegalMoves(player);

		while (legalMoves != null) {
			CheckersMove randMove = legalMoves[rand.nextInt(legalMoves.length)];

			board.makeMove(randMove);

			player = opponent(child.player);
			legalMoves = board.getLegalMoves(player);
		}

		// player is the looser, so opponent is winner
		return opponent(player);
	}

	void backPropagate(MCNode node, int winner) {
		if (node == null)
			return;

		node.playouts++;

		if (node.player == winner)
			node.wins++;

		backPropagate(node.parent, winner);
	}
}
