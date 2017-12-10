package game.model;

import java.util.Collection;

import game.manager.Board;
import game.manager.Move;

public abstract class Piece {

	protected final int piecePosition;
	protected final PieceType pieceType;
	protected Alliance pieceAlliance;


	public Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance) {
		this.pieceType = pieceType;
		this.piecePosition = piecePosition;
		this.pieceAlliance = pieceAlliance;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Piece)) {
			return false;
		}
		final Piece otherPiece = (Piece) other;
		return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType()
				&& pieceAlliance == otherPiece.getPieceAlliance();
	}
	
	public Alliance getPieceAlliance() {
		return pieceAlliance;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public int getPiecePosition() {
		return piecePosition;
	}

	public abstract Piece movePiece(Move move);

	public abstract Piece atePiece(Alliance alliance);

	public abstract Collection<Move> calculateLegalMoves(final Board board);

	public enum PieceType {

		DOT("D");

		private String pieceName;

		PieceType(final String pieceName) {
			this.pieceName = pieceName;
		}

		@Override
		public String toString() {
			return this.pieceName;
		}
	}

}
