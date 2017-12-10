package game.manager;

import javax.swing.SwingWorker;

public class MoveTransition {
	private final Board fromBoard;
	private final Board toBoard;
	private final Move transitionMove;

	public MoveTransition(final Board fromBoard, final Board toBoard, final Move transitionMove) {
		this.fromBoard = fromBoard;
		this.toBoard = toBoard;
		this.transitionMove = transitionMove;

	}

	public Board getFromBoard() {
		return this.fromBoard;
	}

	public Board getToBoard() {
		return this.toBoard;
	}

	public Move getTransitionMove() {
		return this.transitionMove;
	}

	public SwingWorker<Move, String> getMoveStatus() {
		// TODO Auto-generated method stub
		return null;
	}


}
