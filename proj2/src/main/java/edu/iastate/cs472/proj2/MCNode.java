package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode {
	CheckersMove move;
	MCNode parent;
	int wins, playouts;
	float ucb;
	CheckersMove[] legalMoves;
	ArrayList<MCNode> successorNodes;

	public MCNode(CheckersMove cm, MCNode parent, AdversarialSearch as) {
		this.parent = parent;
		this.wins = 0;
		this.playouts = 0;
		this.ucb = -1;
		this.legalMoves = as.legalMoves();
		this.successorNodes = new ArrayList<>();
		this.move = cm;
	}

	public boolean fullyExpanded() {
		return legalMoves.length == successorNodes.size();
	}

	public int firstUnexpandedNode() {
		if (fullyExpanded())
			return -1;

		return successorNodes.size();
	}
}

