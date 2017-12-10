package game.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.google.common.collect.Lists;

import game.ai.AlphaBeta;
import game.ai.MiniMax;
import game.ai.MoveTrategy;
import game.ai.StockAlphaBeta;
import game.manager.Board;
import game.manager.BoardUtils;
import game.manager.Move;
import game.manager.Move.MoveFactory;
import game.manager.MoveTransition;
import game.model.Image;
import game.model.Piece;
import game.model.Title;

@SuppressWarnings({ "unused", "deprecation" })
public class Table extends Observable {

	private final JFrame gameFrame;
	private final BoardPanel boardPanel;
	private Board gameBoard;
	private Title sourceTile;
	private Title destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;

	private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
	private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(500, 500);
	private static final Dimension TILE_LABEL_DIMENSION = new Dimension(50, 50);
	private static final Point BOARD_PANEL_LOCATION = new Point(25, 25);

	private static final Table INSTANCE = new Table();

	private Table() {
		try {
			this.gameFrame = new JFrame("Co ganh");
			this.gameFrame.setLayout(null);
			this.gameBoard = Board.creatStandardBoard();
			this.boardPanel = new BoardPanel();
			this.boardPanel.setLayout(null);
			this.addObserver(new TableGameAIWatcher());
			this.boardDirection = BoardDirection.NORMAL;
			this.gameFrame.add(this.boardPanel);
			JFrame.setDefaultLookAndFeelDecorated(true);
			this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.gameFrame.setResizable(false);
			this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
			this.gameFrame.setLocationRelativeTo(null);
			this.gameFrame.setVisible(true);
		} finally {
		}
	}

	private BoardPanel getBoardPanel() {
		return this.boardPanel;
	}

	private Board getGameBoard() {
		return this.gameBoard;
	}

	private void moveMadeUpdate(final PlayerType playerType) {
		setChanged();
		notifyObservers(playerType);
	}

	private void updateGameBoard(final Board board) {
		this.gameBoard = board;
	}

	public void show() {
		Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());

	}

	public static Table get() {
		return INSTANCE;
	}

	enum PlayerType {
		HUMAN, COMPUTER
	}

	private static class TableGameAIWatcher implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			BoardUtils.checkWinner(Table.get().gameBoard);
			if (Table.get().getGameBoard().currentPlayer().getAlliance().isBlue()
					&& !Table.get().getGameBoard().currentPlayer().getLegalMoves().isEmpty()) {
				System.out.println(Table.get().getGameBoard().currentPlayer() + " is set to AI, thinking....");
				final AIThinkTank thinkTank = new AIThinkTank();
				thinkTank.execute();
			}
		}

	}

	private static class AIThinkTank extends SwingWorker<Move, String> {

		private AIThinkTank() {
		}

		@Override
		protected Move doInBackground() throws Exception {
			final Move bestMove;
			//final MoveTrategy strategy = new AlphaBeta(8);
			//final MoveTrategy strategy = new MiniMax(7);
			final MoveTrategy strategy = new StockAlphaBeta(2);
			bestMove = strategy.execute(Table.get().getGameBoard());
			return bestMove;
		}

		@Override
		public void done() {
			try {
				final Move bestMove = get();
				Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
				Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
				Table.get().moveMadeUpdate(PlayerType.COMPUTER);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	enum BoardDirection {
		NORMAL {
			@Override
			List<TitleLabel> traverse(final List<TitleLabel> boardTiles) {
				return boardTiles;
			}

			@Override
			BoardDirection opposite() {
				return FLIPPED;
			}
		},
		FLIPPED {
			@Override
			List<TitleLabel> traverse(final List<TitleLabel> boardTiles) {
				return Lists.reverse(boardTiles);
			}

			@Override
			BoardDirection opposite() {
				return NORMAL;
			}
		};

		abstract List<TitleLabel> traverse(final List<TitleLabel> boardTiles);

		abstract BoardDirection opposite();

	}

	@SuppressWarnings("serial")
	private class BoardPanel extends JPanel {

		final List<TitleLabel> boardTiles;

		public BoardPanel() {
			setLayout(null);
			this.boardTiles = new ArrayList<>();
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				final TitleLabel titleLabel = new TitleLabel(this, i);
				this.boardTiles.add(titleLabel);
				add(titleLabel);
			}
			setLocation(BOARD_PANEL_LOCATION);
			setSize(BOARD_PANEL_DIMENSION);
			validate();
		}

		public void drawBoard(final Board board) {
			removeAll();
			for (final TitleLabel boardTile : boardDirection.traverse(boardTiles)) {
				boardTile.drawTile(board);
				add(boardTile);
			}
			validate();
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {

			super.paintComponent(g);
			g.drawLine(25, 25, 25, 475);
			g.drawLine(25, 25, 475, 25);
			g.drawLine(475, 475, 475, 25);
			g.drawLine(475, 475, 25, 475);

			g.drawLine(25, 25 + 450 / 4, 475, 25 + 450 / 4);
			g.drawLine(25, 25 + 2 * 450 / 4, 475, 25 + 2 * 450 / 4);
			g.drawLine(25, 25 + 3 * 450 / 4, 475, 25 + 3 * 450 / 4);

			g.drawLine(25 + 450 / 4, 25, 25 + 450 / 4, 475);
			g.drawLine(25 + 2 * 450 / 4, 25, 25 + 2 * 450 / 4, 475);
			g.drawLine(25 + 3 * 450 / 4, 25, 25 + 3 * 450 / 4, 475);

			g.drawLine(25, 25, 475, 475);
			g.drawLine(25, 475, 475, 25);

			g.drawLine(25 + 450 / 2, 25, 25, 25 + 450 / 2);
			g.drawLine(25 + 450 / 2, 25, 475, 25 + 450 / 2);

			g.drawLine(25, 25 + 450 / 2, 25 + 450 / 2, 475);
			g.drawLine(475, 25 + 450 / 2, 25 + 450 / 2, 475);

		}

	}

	@SuppressWarnings("serial")
	private class TitleLabel extends JLabel {

		private final int titleId;

		TitleLabel(final BoardPanel boardPanel, final int tileId) {

			this.titleId = tileId;
			setLayout(null);
			setSize(TILE_LABEL_DIMENSION);
			;
			setLocation(titleId % BoardUtils.NUM_TILE_PER_ROW * 450 / 4,
					titleId / BoardUtils.NUM_TILE_PER_ROW * 450 / 4);

			assignTilePieceIcon(gameBoard);
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(final java.awt.event.MouseEvent event) {

					if (Table.get().getGameBoard().currentPlayer().getAlliance().isBlue()
							|| BoardUtils.isEndGame(Table.get().getGameBoard())) {
						return;
					}

					if (SwingUtilities.isRightMouseButton(event)) {
						sourceTile = null;
						destinationTile = null;
						humanMovedPiece = null;
					} else if (SwingUtilities.isLeftMouseButton(event)) {
						if (sourceTile == null) {
							sourceTile = gameBoard.getTile(titleId);
							humanMovedPiece = sourceTile.getPiece();
							if (humanMovedPiece == null) {
								sourceTile = null;
							}
						} else {
							destinationTile = gameBoard.getTile(tileId);
							final Move move = MoveFactory.createMove(gameBoard, sourceTile.getTileCoordinate(),
									destinationTile.getTileCoordinate());
							final MoveTransition transition = gameBoard.currentPlayer().makeMove(move);
							gameBoard = transition.getToBoard();
							sourceTile = null;
							destinationTile = null;
							humanMovedPiece = null;
						}
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Table.get().moveMadeUpdate(PlayerType.HUMAN);
							boardPanel.drawBoard(gameBoard);
						}
					});
				}

			});
			validate();
		}

		private void assignTilePieceIcon(Board gameBoard) {
			if (gameBoard.getTile(this.titleId).isTileOccupied()) {
				setIcon(new ImageIcon(gameBoard.getTile(titleId).getPiece().getPieceAlliance().isBlue()
						? Image.BLUE.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH)
						: Image.RED.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH)));
			} else
				setIcon(null);

		}

		void drawTile(final Board board) {
			assignTilePieceIcon(board);
			validate();
			repaint();
		}

	}
}
