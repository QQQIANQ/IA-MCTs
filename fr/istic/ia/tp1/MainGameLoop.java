package fr.istic.ia.tp1;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Supplier;

import fr.istic.ia.tp1.Game.Move;
import fr.istic.ia.tp1.Game.PlayerId;

/**
 * Main class for two-player gameplay.
 * Can be used to play any game with the {@link Game} interface, with players classes 
 * implementing the {@link Player} interface.
 * 
 * The class provides menu building facilities to instanciate the game and players.
 * 
 * @author vdrevell
 *
 */

public class MainGameLoop {
	/**
	 * A named Supplier of type T
	 * @author vdrevell
	 *
	 * @param <T>
	 */
	static class NamedSupplier<T> implements Supplier<T> {
		String name;
		Supplier<T> supplier;
		public NamedSupplier(String name, Supplier<T> generator) {
			this.name = name;
			this.supplier = generator;
		}
		@Override
		public String toString() { return name; };
		@Override
		public T get() { return supplier.get(); }
	}
	
	/**
	 * Display a menu and let the user chose an item from a list.
	 * @param list
	 * @return The list element chosen by the user
	 */
	static <T> T chooseInList(ArrayList<T> list) {
		if (list.isEmpty()) {
			System.out.println("No choice.");
			return null;
		}
		
		if (list.size() == 1) {
			System.out.println("> " + list.get(0));
			return list.get(0);
		}
		
		for (int i=0; i<list.size(); ++i) {
			System.out.println(" " + i + ". " + list.get(i));
		}
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		int chosenNum = -1;
		
		while (chosenNum < 0 || chosenNum >= list.size()) {
			System.out.print("Please enter your choice: ");
			if (sc.hasNextInt()) {
				chosenNum = sc.nextInt();
			}
			else {
				if (sc.hasNext()) sc.next();
			}
		}
		return list.get(chosenNum);
	}
	
	/**
	 * A {@link Player} factory, asking the user to chose the object to create from a list
	 * @param name Name of the player to create
	 * @return A new {@link Player} instance
	 */
	static Player chooseAndCreatePlayer(String name) {
		ArrayList<NamedSupplier<Player>> list = new ArrayList<NamedSupplier<Player>>();
		list.add( new NamedSupplier<Player>("Human",     () -> new PlayerHuman(false)) );
		list.add( new NamedSupplier<Player>("Human (with list)", () -> new PlayerHuman(true)) );
		list.add( new NamedSupplier<Player>("Random",    () -> new PlayerRandom())     );
		list.add( new NamedSupplier<Player>("MCTS 1 s",  () -> new PlayerMCTS(1000))   );
		list.add( new NamedSupplier<Player>("MCTS 2 s",  () -> new PlayerMCTS(2000))   );
		list.add( new NamedSupplier<Player>("MCTS 5 s",  () -> new PlayerMCTS(5000))   );
		list.add( new NamedSupplier<Player>("MCTS 10 s", () -> new PlayerMCTS(10000))  );

		System.out.println("Select player type for " + name + ":");
		return chooseInList(list).get();
	}
	
	/**
	 * A {@link Game} factory, asking the user to chose the object to create from a list
	 * @return A new {@link Game} instance
	 */
	static Game chooseAndCreateGame() {
		ArrayList<NamedSupplier<Game>> list = new ArrayList<NamedSupplier<Game>>();
		list.add( new NamedSupplier<Game>("English Draughts (8x8)",    () -> new EnglishDraughts())   );
		list.add( new NamedSupplier<Game>("English Draughts on 10x10", () -> new EnglishDraughts(10)) );
		list.add( new NamedSupplier<Game>("English Draughts on 6x6",   () -> new EnglishDraughts(6))  );
		
		System.out.println("Select the game to play:");
		return chooseInList(list).get();
	}

	/**
	 * Two-player gameplay program entry point (main)
	 *
	 * - The program first instanciates a Game from user input, and then two Players based on user choice.
	 * - Then the game loop starts, letting both players play until the end of the game
	 * - The winner (or equality) is finally displayed before the program quits.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("English Draughts - M1 IL ISTIC - TP IA");
		
		// Create a new game
		Game game = chooseAndCreateGame();
		
		// Create the two players
		Player player1 = chooseAndCreatePlayer(game.playerName(PlayerId.ONE));
		Player player2 = chooseAndCreatePlayer(game.playerName(PlayerId.TWO));
		
		// Game loop until the end of the game
		while (game.winner() == null) {
			System.out.print(game.view());

			// Get the move from the Player object
			Move move = null;
			switch (game.player()) {
			case ONE:
				move = player1.play(game);
				break;
			case TWO:
				move = player2.play(game);
				break;
			default:
				move = null;
			}
			
			// Exit if something went wrong
			if (move == null) {
				System.out.println("Error, " + game.playerName(game.player())+  " cannot play. Abort.");
				return;
			}

			// Display the chosen move
			System.out.println("> " + game.playerName(game.player()) + " played " + move);
			System.out.println();
			
			// Update the game state with the chosen move
			game.play(move);
		}
		
		// Display the winner
		System.out.println(game.view());
		System.out.println(game.playerName(game.winner()) + " wins!");
	}
}
