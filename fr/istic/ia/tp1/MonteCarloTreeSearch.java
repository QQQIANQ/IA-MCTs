package fr.istic.ia.tp1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.istic.ia.tp1.Game.Move;
import fr.istic.ia.tp1.Game.PlayerId;

/**
 * A class implementing a Monte-Carlo Tree Search method (MCTS) for playing two-player games ({@link Game}).
 * @author vdrevell
 *
 */
public class MonteCarloTreeSearch {

	/**
	 * A class to represent an evaluation node in the MCTS tree.
	 * This is a member class so that each node can access the global statistics of the owning MCTS.
	 * @author vdrevell
	 *
	 */
	class EvalNode {
		/** The number of simulations run through this node */
		int n;
		
		/** The number of winning runs */
		double w;
		
		/** The game state corresponding to this node */
		Game game;
		
		/** The children of the node: the games states accessible by playing a move from this node state */
		ArrayList<EvalNode> children;
		
		/** 
		 * The only constructor of EvalNode.
		 * @param game The game state corresponding to this node.
		 */
		EvalNode(Game game) {
			this.game = game;
			children = new ArrayList<EvalNode>();
			w = 0.0;
			n = 0;
		}
		
		/**
		 * Compute the Upper Confidence Bound for Trees (UCT) value for the node.
		 * @return UCT value for the node
		 */
		// TODO
		double uct() {
			if (n == 0) {
				return Integer.MAX_VALUE;
			}
			return this.score() + Math.sqrt(2) * Math.sqrt(Math.log(nTotal) / (double) n);
		}
		
		/**
		 * "Score" of the node, i.e estimated probability of winning when moving to this node
		 * @return Estimated probability of win for the node
		 */
		// TODO
		double score() {
			return (w/n);
		}
		
		/**
		 * Update the stats (n and w) of the node with the provided rollout results
		 * @param res
		 */
		// TODO
		void updateStats(RolloutResults res) {
			n = res.nbSimulations();
			if(game.player()==PlayerId.ONE){
				w = res.win1;
			}
			else{
				w = res.win2;
			}
		}
	}
	
	/**
	 * A class to hold the results of the rollout phase
	 * Keeps the number of wins for each player and the number of simulations.
	 * @author vdrevell
	 *
	 */
	static class RolloutResults {
		/** The number of wins for player 1 {@link PlayerId#ONE}*/
		double win1;
		
		/** The number of wins for player 2 {@link PlayerId#TWO}*/
		double win2;
		
		/** The number of playouts */
		int n;
		
		/**
		 * The constructor
		 */
		public RolloutResults() {
			reset();
		}
		
		/**
		 * Reset results
		 */
		public void reset() {
			n = 0;
			win1 = 0.0;
			win2 = 0.0;
		}
		
		/**
		 * Add other results to this
		 * @param res The results to add
		 */
		public void add(RolloutResults res) {
			win1 += res.win1;
			win2 += res.win2;
			n += res.n;
		}
		
		/**
		 * Update playout statistics with a win of the player <code>winner</code>
		 * Also handles equality if <code>winner</code>={@link PlayerId#NONE}, adding 0.5 wins to each player
		 * @param winner
		 */
		// TODO
		public void update(PlayerId winner) {
			if(winner==PlayerId.ONE){
				win1 += 1;
			}
			else if(winner==PlayerId.TWO){
				win2 += 1;
			}
			else if(winner==PlayerId.NONE){
				win1 += 0.5;
				win2 += 0.5;
			}
		}
		
		/**
		 * Getter for the number of wins of a player
		 * @param playerId
		 * @return The number of wins of player <code>playerId</code>
		 */
		public double nbWins(PlayerId playerId) {
			switch (playerId) {
			case ONE: return win1;
			case TWO: return win2;
			default: return 0.0;
			}
		}
		
		/**
		 * Getter for the number of simulations
		 * @return The number of playouts
		 */
		public int nbSimulations() {
			return n;
		}
	}
	
	/**
	 * The root of the MCTS tree
	 */
	EvalNode root;
	
	/**
	 * The total number of performed simulations (rollouts)
	 */
	int nTotal;

	
	/**
	 * The constructor
	 * @param game
	 */
	public MonteCarloTreeSearch(Game game) {
		root = new EvalNode(game.clone());
		nTotal = 0;
	}
	
	/**
	 * Perform a single random playing rollout from the given game state
	 * @param game Initial game state. {@code game} will contain an ended game state when the function returns.
	 * @return The PlayerId of the winner (or NONE if equality or timeout).
	 */
	// TODO
	static PlayerId playRandomlyToEnd(Game game) {
		Random rand = new Random();

		while (game.winner()==null){
			List<Move> possibleMoves = game.possibleMoves();
			game.play(possibleMoves.get(rand.nextInt(possibleMoves.size())));
		}
		return game.winner();
	}
	
	/**
	 * Perform nbRuns rollouts from a game state, and returns the winning statistics for both players.
	 * @param game The initial game state to start with (not modified by the function)
	 * @param nbRuns The number of playouts to perform
	 * @return A RolloutResults object containing the number of wins for each player and the number of simulations
	 */
	// TODO
	static RolloutResults rollOut(final Game game, int nbRuns) {
		RolloutResults result = new RolloutResults();
		for(int i = 0; i<nbRuns; i++){
			Game gameCopy = game.clone();
			result.update(playRandomlyToEnd(gameCopy));
			result.n++;
		}
		return result;
	}
	
	/**
	 * Apply the MCTS algorithm during at most <code>timeLimitMillis</code> milliseconds to compute
	 * the MCTS tree statistics.
	 * @param timeLimitMillis Computation time limit in milliseconds
	 */
	public void evaluateTreeWithTimeLimit(int timeLimitMillis) {
		// Record function entry time
		long startTime = System.nanoTime();

		// Evaluate the tree until timeout
		while (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) < timeLimitMillis) {
			// Perform one MCTS step
			boolean canStop = evaluateTreeOnce();
			// Stop evaluating the tree if there is nothing more to explore
			if (canStop) {
				break;
			}
		}
		//1. Selection:depuis root, on cherche sur les node jusqua leaf L,avec UCT
		
		// Print some statistics
		System.out.println("Stopped search after " 
		       + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms. "
		       + "Root stats is " + root.w + "/" + root.n + String.format(" (%.2f%% loss)", 100.0*root.w/root.n));
	}
	
	/**
	 * Perform one MCTS step (selection, expansion(s), simulation(s), backpropagation
	 * @return <code>true</code> if there is no need for further exploration (to speed up end of games).
	 */
	// TODO
	public boolean evaluateTreeOnce() {
		ArrayList<EvalNode> visitedNodes = new ArrayList<>();
		// List of visited nodes

		// Start from the root
		EvalNode node = root;
		visitedNodes.add(root);
		// Selection (with UCT tree policy)
		while(node.children.size()>0) {
			//l'idee est a partir de root, on choisi chaque fois le bestNode,CAD la valeur UCT de ce node est
			//supérieur que les autre node, on fait ça jusqu'a la fin de arbre
			double uct = 0;
			double bestUct = 0;
			EvalNode currentNode;
			EvalNode bestNode = node.children.get(0);//on remplace par le node qui contient solution optimale

			for (int i = 0; i < node.children.size(); i++) {

				currentNode = node.children.get(i);
				uct = currentNode.uct();
				if(uct>bestUct){
					bestUct= uct;
					bestNode=currentNode;
				}
			}
			node = bestNode;
			visitedNodes.add(node);
		}

		//2.Expansion: si il y a un winnner au leaf L, gameover, sinon on crée plusieurs node apartir de bestNode
		//et on prends un node C


		if(node.game.winner()!=null){
			return true;//si il y a un winner, GG, sinon on lance expansion
		}
		// Expand node
		node = expandNode(node);
		// Simulate from new node(s)

		//3.Simulation: depuis C, on joue un rollOut aka jouer random
		RolloutResults rollout = rollOut(node.game,100);
		nTotal++;
		// Backpropagate results

		//4. Backpropagation: on utilise resultat de rollout pour mettre a jour les node entre racine
		//R et node C

		for(EvalNode evalNode : visitedNodes){
			evalNode.n += rollout.n;
			evalNode.w += rollout.nbWins(root.game.player());
		}
		// Return false if tree evaluation should continue
		return false;
	}
	private EvalNode expandNode(EvalNode node){
		Game game = node.game.clone();
		for(Move move : node.game.possibleMoves()){
			game = node.game.clone();
			game.play(move);
			node.children.add(new EvalNode(game));
		}
		return node;
	}


	/**
	 * @return The best move to play from the current MCTS tree state.
	 */
	public Move getBestMove() {
		// 
		// TODO Implement MCTS getBestMove
		//
		double uct = 0;
		double bestUct = 0;
		Move move = root.game.possibleMoves().get(0);
		
		for (int i = 0; i < root.children.size(); i++) {

			uct = root.children.get(i).uct();
			if(uct>bestUct){
				bestUct= uct;
				move=root.game.possibleMoves().get(i);
			}
		}
		System.out.println(move);
		return move;
	}
	
	
	/**
	 * Get a few stats about the MTS tree and the possible moves scores
	 * @return A string containing MCTS stats
	 */
	public String stats() {
		String str = "MCTS with " + nTotal + " evals\n";
		Iterator<Move> itMove = root.game.possibleMoves().iterator();
		for (EvalNode node : root.children) {
			Move move = itMove.next();
			double score = node.score();
			str += move + " : " + score + " (" + node.w + "/" + node.n + ")\n";
		}
		return str;
	}
}
