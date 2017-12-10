package game.model;

import java.util.Collection;

import game.manager.Board;
import game.manager.Move;

public class RedPlayer extends Player {

	public RedPlayer(final Board board, final Collection<Move> redStandardLegalMoves,
			final Collection<Move> blueStandardLegalMoves) {
		super(board, redStandardLegalMoves, blueStandardLegalMoves);
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.RED;
	}

	@Override
	public Player getOpponent() {
		return this.board.bluePlayer();
	}

	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getRedPieces();
	}

	@Override
	public String toString() {
		return "red player";
	}

}
