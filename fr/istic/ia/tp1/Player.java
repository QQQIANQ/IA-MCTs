package fr.istic.ia.tp1;

/**
 * Interface for a {@link Game} player.
 * Used to implement different playing algorithms, or user input methods.
 * @author vdrevell
 *
 */
public interface Player {
	/**
	 * Asks the user or computes the next move to play from the provided game state.
	 * @param game The game state from which to play
	 * @return The move ({@link Game.Move}) chosen by the user or the algorithm
	 */
	public Game.Move play(Game game);
}
