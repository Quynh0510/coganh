package game.ai;

import java.util.Observable;
import java.util.Random;

import game.manager.Board;
import game.manager.BoardUtils;
import game.manager.Move;
import game.manager.Move.MoveFactory;
import game.manager.MoveTransition;
import game.model.Alliance;

@SuppressWarnings("deprecation")
public class AlphaBeta extends Observable implements MoveTrategy {

	private final int searchDepth;
	private long boardsEvaluated;
	private long executionTime;
	private int count;

	public AlphaBeta(final int searchDepth) {
		this.searchDepth = searchDepth;
		this.count = 0;
	}

	@Override
	public String toString() {
		return "AlphaBeta";
	}

	@Override
	public long getNumBoardsEvaluated() {
		return this.boardsEvaluated;
	}

	@Override
	public Move execute(final Board board) {

		final long startTime = System.currentTimeMillis();
		Move bestMove = MoveFactory.getNullMove();
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;
		int moveCounter = 1;
		final int numMoves = board.currentPlayer().getLegalMoves().size();

		System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
		System.out.println("\tmoves! : " + board.currentPlayer().getLegalMoves());

		for (final Move move : board.currentPlayer().getLegalMoves()) {

			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			final String s;
			final long candidateMoveStartTime = System.nanoTime();
			currentValue = board.currentPlayer().getAlliance().isBlue()
					? (min(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue))
					: (max(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue));

			if (board.currentPlayer().getAlliance().isBlue()) {
				if (currentValue > highestSeenValue) {
					highestSeenValue = currentValue;
					bestMove = move;
				} else if (currentValue == highestSeenValue) {
					Random random = new Random();
					if ((random.nextBoolean() && move.getScore() == bestMove.getScore())
							|| move.getScore() > bestMove.getScore()) {
						bestMove = move;
					}
				}

			} else if (board.currentPlayer().getAlliance().isRed()) {
				if (currentValue < lowestSeenValue) {
					lowestSeenValue = currentValue;
					bestMove = move;
				} else if (currentValue == lowestSeenValue) {
					Random random = new Random();
					if ((random.nextBoolean() && move.getScore() == bestMove.getScore())
							|| move.getScore() < bestMove.getScore()) {
						bestMove = move;
					}
				}
			}
			final String quiescenceInfo = " [h: " + highestSeenValue + " l: " + lowestSeenValue + "]";
			s = "\t" + toString() + "(" + this.searchDepth + "), m: (" + moveCounter + "/" + numMoves + ") " + move
					+ ", best:  " + bestMove

					+ quiescenceInfo + ", t: " + calculateTimeTaken(candidateMoveStartTime, System.nanoTime());
			System.out.println(s);
			setChanged();
			notifyObservers(s);
			moveCounter++;
		}
		this.executionTime = System.currentTimeMillis() - startTime;
		System.out.printf( 
				"%s SELECTS %s [time taken = %d ms], [buttons = %d]",
				board.currentPlayer(), bestMove, this.executionTime, this.count);
		return bestMove;
	}

	private int max(final Board board, final int depth, final int highest, final int lowest) {
		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.count++;
			return BoardUtils.evaluate(board, Alliance.BLUE);
		}

		int currentHighest = highest;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			this.count++;
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			currentHighest = Math.max(currentHighest,
					min(moveTransition.getToBoard(), depth - 1, currentHighest, lowest));
			if (lowest <= currentHighest)
				break;
		}
		return currentHighest;
	}

	private int min(final Board board, final int depth, final int highest, final int lowest) {

		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.count++;
			return BoardUtils.evaluate(board, Alliance.BLUE);
		}

		int currentLowest = lowest;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			this.count++;
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			currentLowest = Math.min(currentLowest,
					max(moveTransition.getToBoard(), depth - 1, highest, currentLowest));
			if (currentLowest <= highest)
				break;
		}
		return currentLowest;
	}

	private String calculateTimeTaken(final long start, final long end) {
		final long timeTaken = (end - start) / 1000000;
		return timeTaken + " ms";
	}

}
