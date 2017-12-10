package game.ai;

import java.util.Observable;
import java.util.Random;

import game.manager.Board;
import game.manager.BoardUtils;
import game.manager.Move;
import game.manager.MoveTransition;
import game.model.Alliance;

@SuppressWarnings("deprecation")
public class MiniMax extends Observable implements MoveTrategy {

	private final int searchDepth;
	private long boardsEvaluated;
	private long executionTime;
	private long count;

	public MiniMax(final int searchDepth) {
		this.searchDepth = searchDepth;
		this.count = 0;
	}

	@Override
	public String toString() {
		return "MiniMax";
	}

	@Override
	public long getNumBoardsEvaluated() {
		return this.boardsEvaluated;
	}

	public Move execute(final Board board) {

		final long startTime = System.currentTimeMillis();
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;
		int moveCounter = 1;		
		final int numMoves = board.currentPlayer().getLegalMoves().size();
		Move bestMove = Move.MoveFactory.getNullMove();

		System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
		System.out.println("\tmoves! : " + board.currentPlayer().getLegalMoves());

		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final long candidateMoveStartTime = System.nanoTime();
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			
			currentValue = board.currentPlayer().getAlliance().isBlue()
					? min(moveTransition.getToBoard(), this.searchDepth - 1)
					: max(moveTransition.getToBoard(), this.searchDepth - 1);

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

			System.out.println("\t" + toString() + " analyzing move (" + moveCounter + "/" + numMoves + "):\t" + move
					+ "\tbest:" + bestMove + "\tscores " + currentValue + "\tt: "
					+ calculateTimeTaken(candidateMoveStartTime, System.nanoTime()));

			moveCounter++;
		}

		this.executionTime = System.currentTimeMillis() - startTime;
		System.out.printf("%s SELECTS %s [ time taken = %d ms] [buttons: %d]\n", board.currentPlayer().toString(),
				bestMove, this.executionTime, count);
		return bestMove;
	}

	private int min(final Board board, final int depth) {
		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.count++;
			return BoardUtils.evaluate(board, Alliance.BLUE);
		}
		int lowestSeenValue = Integer.MAX_VALUE;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			final int currentValue = max(moveTransition.getToBoard(), depth - 1);
			this.count++;
			if (currentValue <= lowestSeenValue) {
				lowestSeenValue = currentValue;
			}

		}
		return lowestSeenValue;
	}

	private int max(final Board board, final int depth) {
		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.count++;
			return BoardUtils.evaluate(board, Alliance.BLUE);
		}
		int highestSeenValue = Integer.MIN_VALUE;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			final int currentValue = min(moveTransition.getToBoard(), depth - 1);
			this.count++;
			if (currentValue >= highestSeenValue) {
				highestSeenValue = currentValue;
			}

		}
		return highestSeenValue;
	}

	private String calculateTimeTaken(final long start, final long end) {
		final long timeTaken = (end - start) / 1000000;
		return timeTaken + " ms";
	}
}
