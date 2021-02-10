package fr.istic.ia.tp1;

import java.util.*;
import java.util.function.Function;

import fr.istic.ia.tp1.Game.PlayerId;

import static fr.istic.ia.tp1.Game.PlayerId.ONE;
import static fr.istic.ia.tp1.Game.PlayerId.TWO;
import static java.util.Arrays.asList;

/**
 * Implementation of the English Draughts game.
 * @author vdrevell
 *
 */
public class EnglishDraughts extends Game {
	/**
	 * The checker board
	 */
	CheckerBoard board;

	/**
	 * The {@link PlayerId} of the current player
	 * {@link PlayerId#ONE} corresponds to the whites
	 * {@link PlayerId#TWO} corresponds to the blacks
	 */
	PlayerId playerId;

	/**
	 * The current game turn (incremented each time the whites play)
	 */
	int nbTurn;

	/**
	 * The number of consecutive moves played only with kings and without capture
	 * (used to decide equality)
	 */
	int nbKingMovesWithoutCapture;

	/**
	 * Class representing a move in the English draughts game
	 * A move is an ArrayList of Integers, corresponding to the successive tile numbers (Manouri notation)
	 * toString is overrided to provide Manouri notation output.
	 * @author vdrevell
	 *
	 */
	class DraughtsMove extends ArrayList<Integer> implements Move {

		private static final long serialVersionUID = -8215846964873293714L;

		@Override
		public String toString() {
			Iterator<Integer> it = this.iterator();
			Integer from = it.next();
			StringBuffer sb = new StringBuffer();
			sb.append(from);
			while (it.hasNext()) {
				Integer to = it.next();
				if (board.neighborDownLeft(from)==to || board.neighborUpLeft(from)==to
						|| board.neighborDownRight(from)==to || board.neighborUpRight(from)==to) {
					sb.append('-');
				}
				else {
					sb.append('x');
				}
				sb.append(to);
				from = to;
			}
			return sb.toString();
		}
	}

	/**
	 * The default constructor: initializes a game on the standard 8x8 board.
	 */
	public EnglishDraughts() {
		this(8);
	}

	/**
	 * Constructor with custom boardSize (to play on a boardSize x boardSize checkerBoard).
	 * @param boardSize See {@link CheckerBoard#CheckerBoard(int)} for valid board sizes.
	 */
	public EnglishDraughts(int boardSize) {
		this.board = new CheckerBoard(boardSize);
		this.playerId = ONE;
		this.nbTurn = 1;
		this.nbKingMovesWithoutCapture = 0;
	}

	/**
	 * Copy constructor
	 * @param d The game to copy
	 */
	EnglishDraughts(EnglishDraughts d) {
		this.board = d.board.clone();
		this.playerId = d.playerId;
		this.nbTurn = d.nbTurn;
		this.nbKingMovesWithoutCapture = d.nbKingMovesWithoutCapture;
	}

	@Override
	public EnglishDraughts clone() {
		return new EnglishDraughts(this);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(nbTurn);
		sb.append(". ");
		sb.append(this.playerId== ONE?"W":"B");
		sb.append(":");
		sb.append(board.toString());
		return sb.toString();
	}

	@Override
	public String playerName(PlayerId playerId) {
		switch (playerId) {
			case ONE:
				return "Player with the whites";
			case TWO:
				return "Player with the blacks";
			case NONE:
			default:
				return "Nobody";
		}
	}

	@Override
	public String view() {
		return board.boardView() + "Turn #" + nbTurn + ". " + playerName(playerId) + " plays.\n";
	}

	/**
	 * Check if a tile is empty
	 * @param square Tile number
	 * @return
	 */
	boolean isEmpty(int square) {
		return this.board.isEmpty(square);
	}

	/**
	 * Check if a tile is owned by adversary
	 * @param square Tile number
	 * @return
	 */
	boolean isAdversary(int square) {
		switch (playerId) {
			case ONE:
				if(this.board.isBlack(square)){
					return true;
				}
				else{
					return false;
				}
			case TWO:
				if(this.board.isWhite(square)){
					return true;
				}
				else{
					return false;
				}
			case NONE:
			default:
				return false;
		}
	}

	/**
	 * Check if a tile is owned by the current player
	 * @param square Tile number
	 * @return
	 */
	boolean isMine(int square) {
		switch (playerId) {
			case ONE:
				if(this.board.isWhite(square)){
					return true;
				}
				else{
					return false;
				}
			case TWO:
				if(this.board.isBlack(square)){
					return true;
				}
				else{
					return false;
				}
			case NONE:
			default:
				return false;
		}
	}

	/**
	 * Retrieve the list of positions of the pawns owned by the current player
	 * @return The list of current player pawn positions
	 */
	ArrayList<Integer> myPawns() {
		switch (playerId) {
			case ONE:
				return this.board.getWhitePawns();
			case TWO:
				return this.board.getBlackPawns();
			case NONE:
			default:
				return new ArrayList<>();
		}
	}


	/**
	 * Generate the list of possible moves
	 * - first check moves with captures
	 * - if no capture possible, return displacement moves
	 */

	@Override
	public List<Move> possibleMoves() {

		ArrayList<Move> jumpMoves = new ArrayList<>(32);
		for(int positionsOfMyPawns : this.myPawns()){
			DraughtsMove draughtsMove = new DraughtsMove();
			draughtsMove.add(positionsOfMyPawns);
			this.possibleJumpMoves(draughtsMove, jumpMoves, this.board.isKing(positionsOfMyPawns));
		}


		if(jumpMoves.isEmpty()){
			return this.possibleSimplesMoves();
		}

		return jumpMoves;
	}

	public List<Move> possibleSimplesMoves(){
		List<Move> result = new ArrayList<Move>(32);

		for(int positionsOfMyPawns : this.myPawns()){
			//Case WHITE or BLACK King
			if((this.playerId==ONE && !this.board.isKing(positionsOfMyPawns)) || ( this.playerId==TWO && this.board.isKing(positionsOfMyPawns))){
				//Case neighborUpLeft
				if(this.board.neighborUpLeft(positionsOfMyPawns) !=0 && this.board.isEmpty(this.board.neighborUpLeft(positionsOfMyPawns))){
					DraughtsMove tmpMove = new DraughtsMove();
					tmpMove.add(positionsOfMyPawns);
					tmpMove.add(this.board.neighborUpLeft(positionsOfMyPawns));
					result.add(tmpMove);
				}
				//Case neighborUpRight
				if(this.board.neighborUpRight(positionsOfMyPawns)!=0 && this.board.isEmpty(this.board.neighborUpRight(positionsOfMyPawns))){
					DraughtsMove tmpMove = new DraughtsMove();
					tmpMove.add(positionsOfMyPawns);
					tmpMove.add(this.board.neighborUpRight(positionsOfMyPawns));
					result.add(tmpMove);

				}
			}
			//Case BLACK or WHITE King
			else if((this.playerId==TWO && !this.board.isKing(positionsOfMyPawns)) || ( this.playerId==ONE && this.board.isKing(positionsOfMyPawns))){
				//Case neighborDownLeft
				if(this.board.neighborDownLeft(positionsOfMyPawns) !=0 && this.board.isEmpty(this.board.neighborDownLeft(positionsOfMyPawns))){
					DraughtsMove tmpMove = new DraughtsMove();
					tmpMove.add(positionsOfMyPawns);
					tmpMove.add(this.board.neighborDownLeft(positionsOfMyPawns));
					result.add(tmpMove);
				}
				//Case neighborDownRight
				if(this.board.neighborDownRight(positionsOfMyPawns)!=0 && this.board.isEmpty(this.board.neighborDownRight(positionsOfMyPawns))){
					DraughtsMove tmpMove = new DraughtsMove();
					tmpMove.add(positionsOfMyPawns);
					tmpMove.add(this.board.neighborDownRight(positionsOfMyPawns));
					result.add(tmpMove);
				}
			}
		}
		return result;
	}

	public void possibleJumpMoves(DraughtsMove lastMove, ArrayList<Move> resultJumpMoves, boolean king){

		int jumpPosition;
		int ennemyPosition;

		boolean placed = false;
		//Case WHITE or BLACK King

		if(this.playerId==ONE || king ){
			//Check if neighborUpLeft and neighborUpLeft(neighborUpLeft) are in the board and if neighborUpLeft(neighborUpLeft) is empty



			jumpPosition = this.board.neighborUpLeft(this.board.neighborUpLeft(lastMove.get(lastMove.size()-1)));
			ennemyPosition = this.board.neighborUpLeft(lastMove.get(lastMove.size()-1));

			if(ennemyPosition !=0 && jumpPosition!=0 && this.board.isEmpty(jumpPosition) && !lastMove.contains(jumpPosition) && this.isAdversary(ennemyPosition)){
				DraughtsMove tmpLastMove = new DraughtsMove();
				tmpLastMove.addAll(lastMove);
				tmpLastMove.add(jumpPosition);
				//resultJumpMoves.add(tmpLastMove);
				placed = true;
				this.possibleJumpMoves(tmpLastMove,resultJumpMoves,king);
			}

			//Check if neighborUpRight and neighborUpRight(neighborUpRight) are in the board and if neighborUpRight(neighborUpRight) is empty

			jumpPosition = this.board.neighborUpRight(this.board.neighborUpRight(lastMove.get(lastMove.size()-1)));
			ennemyPosition = this.board.neighborUpRight(lastMove.get(lastMove.size()-1));

			if(ennemyPosition !=0 && jumpPosition!=0 && this.board.isEmpty(jumpPosition) && !lastMove.contains(jumpPosition) && this.isAdversary(ennemyPosition)){
				DraughtsMove tmpLastMove = new DraughtsMove();
				tmpLastMove.addAll(lastMove);
				tmpLastMove.add(jumpPosition);
				//resultJumpMoves.add(tmpLastMove);
				placed = true;
				this.possibleJumpMoves(tmpLastMove,resultJumpMoves,king);
			}
		}
		if(this.playerId==TWO || king ){
			//Check if neighborDownLeft and neighborDownLeft(neighborDownLeft) are in the board and if neighborDownLeft(neighborDownLeft) is empty

			jumpPosition = this.board.neighborDownLeft(this.board.neighborDownLeft(lastMove.get(lastMove.size()-1)));
			ennemyPosition = this.board.neighborDownLeft(lastMove.get(lastMove.size()-1));

			if(ennemyPosition !=0 && jumpPosition!=0 && this.board.isEmpty(jumpPosition) && !lastMove.contains(jumpPosition) && this.isAdversary(ennemyPosition)){
				DraughtsMove tmpLastMove = new DraughtsMove();
				tmpLastMove.addAll(lastMove);
				tmpLastMove.add(jumpPosition);
				//resultJumpMoves.add(tmpLastMove);
				placed = true;
				this.possibleJumpMoves(tmpLastMove,resultJumpMoves,king);
			}
			//Check if neighborUpRight and neighborUpRight(neighborUpRight) are in the board and if neighborUpRight(neighborUpRight) is empty

			jumpPosition = this.board.neighborDownRight(this.board.neighborDownRight(lastMove.get(lastMove.size()-1)));
			ennemyPosition = this.board.neighborDownRight(lastMove.get(lastMove.size()-1));

			if(ennemyPosition !=0 && jumpPosition!=0 && this.board.isEmpty(jumpPosition) && !lastMove.contains(jumpPosition) && this.isAdversary(ennemyPosition)){
				DraughtsMove tmpLastMove = new DraughtsMove();
				tmpLastMove.addAll(lastMove);
				tmpLastMove.add(jumpPosition);
				//resultJumpMoves.add(tmpLastMove);
				placed = true;
				this.possibleJumpMoves(tmpLastMove,resultJumpMoves,king);
			}
		}

		if(!placed && lastMove.size()>1){
			resultJumpMoves.add(lastMove);
		}
	}






	@Override

	public void play(Move aMove) {
		// Player should be valid
		if (playerId == PlayerId.NONE)
			return;
		// We will cast Move to DraughtsMove (kind of ArrayList<Integer>
		if (!(aMove instanceof DraughtsMove))
			return;
		// Cast and apply the move
		DraughtsMove move = (DraughtsMove) aMove;


		System.out.println("Play "+move.toString());

		System.out.println("Move from "+move.get(0)+" to "+(move.get(move.size()-1)));

		this.board.movePawn(move.get(0),(move.get(move.size()-1)));
		// Move pawn and capture opponents

		if(move.size()==2 && this.board.squareBetween(move.get(0),move.get(1))==0){
			nbKingMovesWithoutCapture++;
		}
		else{
			nbKingMovesWithoutCapture=0;
		}

		for(int i = 1; i<move.size();i++){
			if(this.board.squareBetween(move.get(i-1),move.get(i))!=0){
				System.out.println("Square exist between "+(i-1)+" and "+i);

				this.board.removePawn(this.board.squareBetween(move.get(i-1),move.get(i)));
			}


			if(playerId==ONE && this.board.inTopRow(move.get(i))){
				System.out.println("Square at top of board, now he's a king");

				this.board.crownPawn(move.get(move.size()-1));
			}
			if(playerId==TWO && this.board.inBottomRow(move.get(i))){
				this.board.crownPawn(move.get(move.size()-1));
			}
		}

		System.out.println("Last player : " + playerId);
		if(playerId==TWO){
			this.playerId=PlayerId.ONE;
		}
		else{
			this.playerId=PlayerId.TWO;
		}

		System.out.println("New player : " + playerId);

		this.nbTurn++;




		// Promote to king if the pawn ends on the opposite of the board

		// Next player

		// Update nbTurn

		// Keep track of successive moves with kings wthout capture

	}
	@Override
	public PlayerId player() {
		return playerId;
	}

	/**
	 * Get the winner (or null if the game is still going)
	 * Victory conditions are :
	 * - adversary with no more pawns or no move possibilities
	 * Null game condition (return PlayerId.NONE) is
	 * - more than 25 successive moves of only kings and without any capture
	 */
	@Override
	public PlayerId winner() {
		//
		// TODO implement winner
		//
		if(this.board.getBlackPawns().isEmpty()){
			return playerId.ONE;
		}
		if(this.board.getWhitePawns().isEmpty()){
			return playerId.TWO;
		}
		else if(this.possibleMoves().isEmpty()){
			return this.player().other();
		}
		else if(nbKingMovesWithoutCapture>=25){
			return PlayerId.NONE;
		}
		return null;

		// return the winner ID if possible

		// return PlayerId.NONE if the game is null

		// Return null is the game has not ended yet
	}
}
