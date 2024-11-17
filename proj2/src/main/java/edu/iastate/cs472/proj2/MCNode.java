package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * Node type for the Monte Carlo search tree.
 * @author Jacob Duba
 */
public class MCNode {
	CheckersMove move;
	MCNode parent;
	int wins, playouts;
	CheckersMove[] legalMoves;
	ArrayList<MCNode> successorNodes;
	int player;

	public MCNode(CheckersMove cm, MCNode parent, int player) {
		this.parent = parent;
		this.wins = 0;
		this.playouts = 0;
		this.legalMoves = null;
		this.successorNodes = new ArrayList<>();
		this.move = cm;
		this.player = player;
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

