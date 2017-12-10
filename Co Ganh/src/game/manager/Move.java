package game.manager;

import java.util.ArrayList;
import java.util.List;

import game.manager.Board.Builder;
import game.model.Dot;
import game.model.Piece;
import game.model.Title;

public abstract class Move {

	protected final Board board;
	protected final Piece movedPiece;
	protected final int destinationCoordinate;

	private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
		this.board = board;
		this.movedPiece = movedPiece;
		this.destinationCoordinate = destinationCoordinate;

	}

	public Board getBoard() {
		return board;
	}

	public int getDestinationCoordinate() {
		return destinationCoordinate;
	}

	public int getCurrentCoordinate() {
		return this.movedPiece.getPiecePosition();
	}

	public Piece getMovedPiece() {
		return movedPiece;
	}

	public abstract Board execute();

	public abstract String toString();

	public abstract int getScore();

	@Override
	public boolean equals(Object other) {

		if (!(other instanceof Move)) {
			return false;
		}
		Move otherMove = (Move) other;
		return movedPiece.equals(otherMove.getMovedPiece())
				&& this.destinationCoordinate == otherMove.getDestinationCoordinate();
	}

	public static class MajorMove extends Move {
		private final List<Integer> attackedPices;

		public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
			super(board, movedPiece, destinationCoordinate);
			this.attackedPices = caculateAttackedPieces(this.board, this.destinationCoordinate, this.movedPiece);
		}

		public Board execute() {
			final Board.Builder builder = new Builder();
			for (Piece piece : this.board.currentPlayer().getActivePieces()) {
				if (!this.movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				if (this.attackedPices.contains(piece.getPiecePosition())) {
					builder.setPiece(atePiece(piece));
				} else {
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			return builder.build();
		}

		private Piece atePiece(Piece piece) {
			return new Dot(piece.getPiecePosition(), this.movedPiece.getPieceAlliance());
		}

		private List<Integer> caculateAttackedPieces(final Board board, final int candidateDestinationCoordinate,
				final Piece movedPiece) {
			final Integer[] CANDIDATE_ATTACKED_COORDINATE = { -5, -1, 1, 5 };
			final Integer[] CANDIDATE_ATTACKED_COORDINATE_EXTEND = { -6, -5, -4, -1, 1, 4, 5, 6 };
			Integer candidateAttackedCoordinate;
			final Integer[] candidateAttackedCoordinates;
			List<Integer> attackedPices = new ArrayList<>();
			if (candidateDestinationCoordinate % 2 == 0) {
				candidateAttackedCoordinates = CANDIDATE_ATTACKED_COORDINATE_EXTEND;
			} else {
				candidateAttackedCoordinates = CANDIDATE_ATTACKED_COORDINATE;
			}
			for (final Integer currentAttackedOffset : candidateAttackedCoordinates) {
				candidateAttackedCoordinate = candidateDestinationCoordinate + currentAttackedOffset;
				if (BoardUtils.isValidTileCoordinate(candidateAttackedCoordinate)) {
					if (isFirstColumnEclusion(candidateDestinationCoordinate, currentAttackedOffset)
							|| isLastColumnEclusion(candidateDestinationCoordinate, currentAttackedOffset)) {
						continue;
					}

					Title title = board.getTile(candidateAttackedCoordinate);
					if (title.isTileOccupied()) {
						if (title.getPiece().getPieceAlliance() != movedPiece.getPieceAlliance()) {
							if (isAttacked(board, candidateAttackedCoordinate, candidateDestinationCoordinate,
									movedPiece)) {
								attackedPices.add(candidateAttackedCoordinate);
								continue;
							}
							if (isIsolation(board, candidateAttackedCoordinate, candidateDestinationCoordinate,
									movedPiece)) {
								attackedPices.add(candidateAttackedCoordinate);
							}
						}
					}
				}
			}
			return attackedPices;
		}

		private boolean isIsolation(final Board board, final Integer candidateAttackedCoordinate,
				final int candidateDestinationCoordinate, final Piece movedPiece) {
			final Integer[] CANDIDATE_COORDINATE = { -5, -1, 1, 5 };
			final Integer[] CANDIDATE_COORDINATE_EXTEND = { -6, -5, -4, -1, 1, 4, 5, 6 };

			Integer candidateVincinityCoordinate;
			final Integer[] candidateVincinityCoordinates;

			if (candidateAttackedCoordinate % 2 == 0) {
				candidateVincinityCoordinates = CANDIDATE_COORDINATE_EXTEND;
			} else {
				candidateVincinityCoordinates = CANDIDATE_COORDINATE;
			}

			for (Integer currentVincinityOffset : candidateVincinityCoordinates) {
				candidateVincinityCoordinate = candidateAttackedCoordinate + currentVincinityOffset;
				if (!BoardUtils.isValidTileCoordinate(candidateVincinityCoordinate)) {
					continue;
				}
				if (isFirstColumnEclusion(candidateAttackedCoordinate, currentVincinityOffset)
						|| isLastColumnEclusion(candidateAttackedCoordinate, currentVincinityOffset)) {
					continue;
				}
				if (candidateVincinityCoordinate.equals(candidateDestinationCoordinate)) {
					continue;
				}
				if (candidateVincinityCoordinate.equals(movedPiece.getPiecePosition())) {
					return false;
				}
				if (!board.getTile(candidateVincinityCoordinate).isTileOccupied()) {
					return false;
				}
				if (board.getTile(candidateVincinityCoordinate).getPiece().getPieceAlliance() != movedPiece
						.getPieceAlliance()) {
					return false;
				}

			}

			return true;
		}

		private boolean isAttacked(final Board board, final Integer candidateAttackedCoordinate,
				final int candidateDestinationCoordinate, Piece movedPiece) {
			int result = 2 * candidateDestinationCoordinate - candidateAttackedCoordinate;
			if (BoardUtils.isValidTileCoordinate(result) && board.getTile(result).isTileOccupied()
					&& !(isFirstColumnEclusion(candidateDestinationCoordinate, result - candidateDestinationCoordinate)
							|| isLastColumnEclusion(candidateDestinationCoordinate,
									result - candidateDestinationCoordinate))) {
				if (board.getTile(result).getPiece().getPieceAlliance() != movedPiece.getPieceAlliance()) {
					return true;
				}
			}
			return false;
		}

		private boolean isLastColumnEclusion(int currentPosition, int candidateOffset) {
			return BoardUtils.LAST_COLUMN[currentPosition]
					&& (candidateOffset == -4 || candidateOffset == 1 || candidateOffset == 6);
		}

		private boolean isFirstColumnEclusion(int currentPosition, int candidateOffset) {
			return BoardUtils.FIRST_COLUMN[currentPosition]
					&& (candidateOffset == -6 || candidateOffset == -1 || candidateOffset == 4);
		}

		public List<Integer> getAttackedPices() {
			return attackedPices;
		}

		@Override
		public int getScore() {
			return this.attackedPices.size();
		}

		@Override
		public String toString() {
			return "[" + this.movedPiece.toString() + "; " + String.valueOf(destinationCoordinate)+ "]";
		}

	}

	private static class NullMove extends Move {

		private NullMove() {
			super(null, null, -1);
		}

		@Override
		public int getCurrentCoordinate() {
			return -1;
		}

		@Override
		public int getDestinationCoordinate() {
			return -1;
		}

		@Override
		public Board execute() {
			throw new RuntimeException("cannot execute null move!");
		}

		@Override
		public String toString() {
			return "Null Move";
		}

		@Override
		public int getScore() {
			return 0;
		}
	}

	public static class MoveFactory {

		private static final Move NULL_MOVE = new NullMove();

		private MoveFactory() {
			throw new RuntimeException("Not instantiatable!");
		}

		public static Move getNullMove() {
			return NULL_MOVE;
		}

		public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate) {
			for (final Move move : board.getAllLegalMoves()) {
				if (move.getCurrentCoordinate() == currentCoordinate
						&& move.getDestinationCoordinate() == destinationCoordinate) {
					return move;
				}
			}
			return NULL_MOVE;
		}
	}

}
