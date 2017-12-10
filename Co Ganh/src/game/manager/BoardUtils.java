package game.manager;

import javax.swing.JOptionPane;

import game.model.Alliance;

public class BoardUtils {

	public static final boolean[] FIRST_COLUMN = initColumn(0);
	public static final boolean[] LAST_COLUMN = initColumn(4);

	public static final int NUM_TILES = 25;
	public static final int NUM_TILE_PER_ROW = 5;

	private BoardUtils() {
		throw new RuntimeException("Mày không thể khởi tạo tao");
	}

	public static boolean isValidTileCoordinate(final int coordinate) {
		return coordinate >= 0 && coordinate < NUM_TILES;
	}

	public static void checkWinner(Board board) {
		if (isEndGame(board)) {
			JOptionPane.showMessageDialog(null, board.currentPlayer().getOpponent().toString() + " win: Game over");
		}
	}

	public static boolean isEndGame(Board board) {
		return board.currentPlayer().getLegalMoves().isEmpty();
	}

	public static int evaluate(Board board, Alliance alliance) {
		if (isEndGame(board) && alliance == board.currentPlayer().getAlliance()) {
			return -2000;
		}
		if (isEndGame(board) && alliance != board.currentPlayer().getAlliance()) {
			return 2000;
		}
		return alliance.isBlue() ? (board.getBluePieces().size() - board.getRedPieces().size())
				: (board.getRedPieces().size() - board.getBluePieces().size());
	}

	private static boolean[] initColumn(int columnNumber) {

		final boolean[] column = new boolean[NUM_TILES];
		do {
			column[columnNumber] = true;
			columnNumber += NUM_TILE_PER_ROW;
		} while (columnNumber < NUM_TILES);
		return column;

	}

}
