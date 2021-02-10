package fr.istic.ia.tp1;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import fr.istic.ia.tp1.Game.Move;

/**
 * An implementation of {@link Player} that randomly plays a valid move.
 * @author vdrevell
 *
 */
public class PlayerRandom implements Player {

	@Override
	public Move play(Game game) {
		List<Move> moves = game.possibleMoves();
		
		if (moves.isEmpty())
			return null;
		
		int randomNum = ThreadLocalRandom.current().nextInt(0, moves.size());
		Iterator<Move> it = moves.iterator();
		for (int i=0; i<randomNum; ++i) {
			it.next();
		}
		return it.next();
	}

}
