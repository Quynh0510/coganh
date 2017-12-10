package game.model;

import java.util.Collection;

import game.manager.Board;
import game.manager.Move;
import game.manager.MoveTransition;

public abstract class Player {

	protected final Board board;
	protected final Collection<Move> legalMoves;

	protected Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
		this.board = board;
		this.legalMoves = legalMoves;
	}

	public boolean islegalMoves(final Move move) {
		return legalMoves.contains(move);
	}

	public MoveTransition makeMove(final Move move) {
		if (!islegalMoves(move)) {
			return new MoveTransition(this.board, this.board, move/* , MoveStatus.ILLEGAL_MOVE */);
		}
		final Board transitionedBoard = move.execute();
		return new MoveTransition(this.board, transitionedBoard, move/* , MoveStatus.DONE */);

	}

	public Collection<Move> getLegalMoves() {
		return legalMoves;
	}

	public abstract Collection<Piece> getActivePieces();

	public abstract Alliance getAlliance();

	public abstract Player getOpponent();

}
