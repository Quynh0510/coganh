package game.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import game.manager.BoardUtils;

public abstract class Title {

	protected final int tileCoordinate;

	private static final Map<Integer, EmptyTile> EMPTY_TILE_CACHE = creatAllPossibleEmptyTile();

	public int getTileCoordinate() {
		return tileCoordinate;
	}

	private static Map<Integer, EmptyTile> creatAllPossibleEmptyTile() {

		final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			emptyTileMap.put(i, new EmptyTile(i));
		}

		Collections.unmodifiableMap(emptyTileMap);
		return ImmutableMap.copyOf(emptyTileMap);
	}

	public static Title creatTile(final int tileCoordinate, final Piece piece) {
		return piece != null ? new OcupyTile(tileCoordinate, piece) : EMPTY_TILE_CACHE.get(tileCoordinate);
	}

	private Title(int tileCoordinate) {
		this.tileCoordinate = tileCoordinate;
	}

	public abstract boolean isTileOccupied();

	public abstract Piece getPiece();

	public static final class EmptyTile extends Title {

		public EmptyTile(int tileCoordinate) {
			super(tileCoordinate);
		}

		@Override
		public boolean isTileOccupied() {
			return false;
		}

		@Override
		public Piece getPiece() {
			return null;
		}

		@Override
		public String toString() {
			return "-";
		}
	}

	public static final class OcupyTile extends Title {

		private Piece pieceOnTile;

		public OcupyTile(int tileCoordinate, final Piece pieceOnTile) {
			super(tileCoordinate);
			this.pieceOnTile = pieceOnTile;
		}

		@Override
		public boolean isTileOccupied() {
			return true;
		}

		@Override
		public Piece getPiece() {
			return this.pieceOnTile;
		}

		@Override
		public String toString() {
			return getPiece().getPieceAlliance().isBlue() ? getPiece().toString().toLowerCase() : getPiece().toString();
		}

	}

}
