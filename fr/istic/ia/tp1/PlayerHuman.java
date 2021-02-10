package fr.istic.ia.tp1;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import fr.istic.ia.tp1.Game.Move;

/**
 * An implementation of {@link Player} that asks the user to enter a move.
 * Entry is either from a list, or by typing the move (see constructor).
 * @author vdrevell
 *
 */
public class PlayerHuman implements Player {
	boolean displayChoices;
	
	/**
	 * Default constructor
	 * Human player with display of feasible moves
	 */
	public PlayerHuman() {
		this(true);
	}
	
	/**
	 * Constructor
	 * @param displayChoices : if true, displays the list of choices, otherwise, the user has to type the move.
	 */
	public PlayerHuman(boolean displayChoices) {
		this.displayChoices = displayChoices;
	}

	@Override
	public Move play(Game game) {
		List<Move> moves = game.possibleMoves();
		
		if (moves.isEmpty()) {
			System.out.println("No possible move! Press enter to continue...");
			try {
				System.in.read();
			} catch (IOException e) {
			}
			return null;
		}
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		if (displayChoices) {
			Iterator<Move> it = moves.iterator();
			for (int i=0; it.hasNext(); ++i) {
				System.out.println(String.format("%2d. ",i) + it.next());
			}
			int chosenNum = -1;
			while (chosenNum < 0 || chosenNum >= moves.size()) {
				System.out.print("Please enter your choice: ");
				if (sc.hasNextInt()) {
					chosenNum = sc.nextInt();
				}
				else {
					if (sc.hasNext()) sc.next();
				}
			}
			
			it = moves.iterator();
			for (int i=0; i<chosenNum; ++i) {
				it.next();
			}
			return it.next();
		}
		else {
			for (int i=0; i<25; ++i) {
				System.out.print("Please enter your move: ");
				if (sc.hasNextLine()) {
					String moveString = sc.nextLine().trim(); // better use .strip() if java 11
					
					Iterator<Move> it = moves.iterator();
					while (it.hasNext()) {
						Move move = it.next();
						if (moveString.equals(move.toString().trim())) { // use .strip() if java 11
							return move;
						}
					}
				}
				else {
					if (sc.hasNext()) sc.next();
				}
			}
			System.out.println("Exceeded 25 input tries, abort...");
			return null;
		}
	}

}
