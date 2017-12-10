package game.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import game.model.Alliance;
import game.model.BluePlayer;
import game.model.Dot;
import game.model.Piece;
import game.model.Player;
import game.model.RedPlayer;
import game.model.Title;

public class Board {

	private final List<Title> gameBoard;
	private final Collection<Piece> bluePieces;
	private final Collection<Piece> redPieces;

	private final BluePlayer bluePlayer;
	private final RedPlayer redPlayer;
	private Player currentPlayer;

	private Board(Builder builder) {

		this.gameBoard = creatGameBoard(builder);
		this.bluePieces = caculateActivePieces(this.gameBoard, Alliance.BLUE);
		this.redPieces = caculateActivePieces(this.gameBoard, Alliance.RED);

		final Collection<Move> blueStandardLegalMoves = caculateLegalMoves(this.bluePieces);
		final Collection<Move> redStandardLegalMoves = caculateLegalMoves(this.redPieces);

		this.bluePlayer = new BluePlayer(this, blueStandardLegalMoves, redStandardLegalMoves);
		this.redPlayer = new RedPlayer(this, redStandardLegalMoves, blueStandardLegalMoves);
		this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.redPlayer, this.bluePlayer);
	}

	private Collection<Move> caculateLegalMoves(Collection<Piece> pieces) {

		Collection<Move> legalMove = new ArrayList<>();
		for (final Piece piece : pieces) {
			legalMove.addAll(piece.calculateLegalMoves(this));
		}
		return ImmutableList.copyOf(legalMove);
	}

	private static Collection<Piece> caculateActivePieces(final List<Title> gameBoard, final Alliance alliance) {

		List<Piece> activePiece = new ArrayList<>();
		for (final Title title : gameBoard) {
			if (title.isTileOccupied()) {
				final Piece piece = title.getPiece();
				if (piece.getPieceAlliance() == alliance) {
					activePiece.add(piece);
				}
			}
		}
		return ImmutableList.copyOf(activePiece);
	}

	public Player bluePlayer() {
		return this.bluePlayer;
	}

	public Player redPlayer() {
		return this.redPlayer;
	}
	
	public Player currentPlayer() {
		return this.currentPlayer;
	}

	public Title getTile(final int tileCoordinate) {
		return gameBoard.get(tileCoordinate);
	}

	public Collection<Piece> getBluePieces() {
		return this.bluePieces;
	}

	public Collection<Piece> getRedPieces() {
		return this.redPieces;
	}
	
	public Iterable<Move> getAllLegalMoves(){
		return Iterables.unmodifiableIterable(Iterables.concat(this.redPlayer.getLegalMoves(),this.bluePlayer.getLegalMoves()));
	}

	public static Board creatStandardBoard() {
	
		final Builder builder = new Builder();
		// BLUE layout
		for (int i = 0; i < 11; i++) {
			if (i > 5 && i < 9)
				continue;
			builder.setPiece(new Dot(i, Alliance.BLUE));
		}
		// RED layout
		for (int i = 14; i < 25; i++) {
			if (i > 15 && i < 19)
				continue;
			builder.setPiece(new Dot(i, Alliance.RED));
		}
		// RED to move
		builder.setMoveMaker(Alliance.RED);
		return builder.build();
	}

	private static List<Title> creatGameBoard(final Builder builder) {

		final Title[] titles = new Title[BoardUtils.NUM_TILES];
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			titles[i] = Title.creatTile(i, builder.boardConfig.get(i));
		}
		return ImmutableList.copyOf(titles);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			final String tileText = this.gameBoard.get(i).toString();
			builder.append(String.format("%3s", tileText));
			if ((i + 1) % BoardUtils.NUM_TILE_PER_ROW == 0) {
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	public static class Builder {

		Map<Integer, Piece> boardConfig;
		Alliance nextMoveMaker;

		public Builder() {
			this.boardConfig = new HashMap<>();
		}

		public Builder setPiece(final Piece piece) {
			this.boardConfig.put((Integer) piece.getPiecePosition(), piece);
			return this;
		}

		public Builder setMoveMaker(Alliance nextMoveMaker) {
			this.nextMoveMaker = nextMoveMaker;
			return this;
		}

		public Board build() {
			return new Board(this);
		}
	}

}
