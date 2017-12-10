package game.ai;

import game.manager.Board;
import game.manager.Move;

public interface MoveTrategy {
	
	long getNumBoardsEvaluated();

	Move execute(Board board);

}
