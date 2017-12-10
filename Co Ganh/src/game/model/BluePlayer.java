package game.model;

import java.util.Collection;

import game.manager.Board;
import game.manager.Move;

public class BluePlayer extends Player {

	public BluePlayer(final Board board, final Collection<Move> blueStandardLegalMoves,
			final Collection<Move> redStandardLegalMoves) {
		super(board, blueStandardLegalMoves, redStandardLegalMoves);
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.BLUE;
	}

	@Override
	public Player getOpponent() {
		return this.board.redPlayer();
	}

	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getBluePieces();
	}

	@Override
	public String toString() {
		return "blue player";
	}
}
