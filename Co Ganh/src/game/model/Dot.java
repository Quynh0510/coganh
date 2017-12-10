package game.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import game.manager.Board;
import game.manager.BoardUtils;
import game.manager.Move;

public class Dot extends Piece {

	public Dot(final int piecePosition, final Alliance pieceAlliance) {
		super(Piece.PieceType.DOT, piecePosition, pieceAlliance);
	}

	@Override
	public Collection<Move> calculateLegalMoves(Board board) {

		final int[] CANDIDATE_MOVE_COORDINATE = { -5, -1, 1, 5 };
		final int[] CANDIDATE_MOVE_COORDINATE_EXTEND = { -6, -5, -4, -1, 1, 4, 5, 6 };
		int[] candidateMoveCoordinates;
		final List<Move> legalMoves = new ArrayList<>();
		int candidateDestinationCoordinate;

		if (piecePosition % 2 == 0) {
			candidateMoveCoordinates = CANDIDATE_MOVE_COORDINATE_EXTEND;
		} else {
			candidateMoveCoordinates = CANDIDATE_MOVE_COORDINATE;
		}

		for (final int currentCandidateOffset : candidateMoveCoordinates) {

			candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
			if (isFirstColumnEclusion(this.piecePosition, currentCandidateOffset)
					|| isLastColumnEclusion(this.piecePosition, currentCandidateOffset)) {
				continue;
			}

			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				final Title candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				if (!candidateDestinationTile.isTileOccupied()) {
					legalMoves.add(new Move.MajorMove(board, this, candidateDestinationTile.tileCoordinate));
				}
			}
		}
		return ImmutableList.copyOf(legalMoves);
	}

	@Override
	public String toString() {
		return (pieceAlliance.isRed() ? PieceType.DOT.toString() : PieceType.DOT.toString().toLowerCase())
				+ this.piecePosition;
	}

	@Override
	public Dot movePiece(final Move move) {
		return new Dot(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
	}

	@Override
	public Dot atePiece(Alliance alliance) {
		this.pieceAlliance = alliance;
		return this;
	}

	private boolean isFirstColumnEclusion(int currentPosition, int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition]
				&& (candidateOffset == -6 || candidateOffset == -1 || candidateOffset == 4);
	}

	private boolean isLastColumnEclusion(int currentPosition, int candidateOffset) {
		return BoardUtils.LAST_COLUMN[currentPosition]
				&& (candidateOffset == -4 || candidateOffset == 1 || candidateOffset == 6);
	}

}
