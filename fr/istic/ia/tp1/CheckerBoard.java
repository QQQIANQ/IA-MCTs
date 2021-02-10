/**
 * 
 */
package fr.istic.ia.tp1;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A class representing a checker board, with black and white pawns (checkers and kings)
 * @author vdrevell
 *
 */
public class CheckerBoard {
	final byte size;
	private byte[] state;
	
	/** An ID indicating a point was not on the checker board. */
	public static final byte INVALID = -1;

	/** The ID of an empty checker board tile. */
	public static final byte EMPTY = 0;

	/** The ID of a white checker in the checker board. */
	public static final byte BLACK_CHECKER = 4 * 1 + 2 * 1 + 1 * 0;
	
	/** The ID of a white checker in the checker board. */
	public static final byte WHITE_CHECKER = 4 * 1 + 2 * 0 + 1 * 0;

	/** The ID of a black checker that is also a king. */
	public static final byte BLACK_KING = 4 * 1 + 2 * 1 + 1 * 1;
	
	/** The ID of a white checker that is also a king. */
	public static final byte WHITE_KING = 4 * 1 + 2 * 0 + 1 * 1;
	
	/** 
	 *  Default constructor, create a 64-tile (8x8) checker board.
	 */
	public CheckerBoard() {
		this(8);
	}
	
	/**
	 * Constructor with configurable size. Create a <code>size</code> x <code>size</code> checker-board.
	 * @param size The size of one side of the checker board. Valid sizes are 4, 6, 8, 10 and 12.
	 */
	public CheckerBoard(int size) {
		assert size >= 3 && size <= 12 : "Cannot create board with size <3 or >12";
		assert size % 2 == 0 : "Cannot create a board with odd size";
		this.size = (byte)size;
		int nbPlaces = (size * size) / 2;
		this.state = new byte[nbPlaces];
		int nbPawnsPerPlayer = (size/2 - 1) * (size/2);
		for (int k = 0; k<nbPawnsPerPlayer; ++k) {
			set(nbPlaces - k, WHITE_CHECKER);
			set(k + 1, BLACK_CHECKER);
		}
	}
	
	/**
	 * Copy constructor
	 * @param board
	 */
	protected CheckerBoard(CheckerBoard board) {
		this.size = board.size;
		this.state = board.state.clone();
	}
	
	@Override
	public CheckerBoard clone() {
		return new CheckerBoard(this);
	}
	
	/**
	 * Get the number of playable (black) tiles in the checker board
	 * @return The number of black tiles
	 */
	public int nbPlayableTiles() {
		return state.length;
	}
	
	
	/**
	 * Check if the board is empty
	 * @return
	 */
	public boolean isEmpty() {
		for (int i=1; i<=nbPlayableTiles(); ++i)
			if (get(i) != EMPTY)
				return false;
		return true;
	}
	
	
	/**
	 * Auxiliary function for toString
	 * @param sb
	 * @param colorTest
	 */
	private void buildPawnsList(StringBuffer sb, Predicate<Integer> colorTest) {
		boolean firstItem = true;
		for (int k = 1; k<=nbPlayableTiles(); ++k) {
			if (colorTest.test(k)) {
				if (firstItem) {
					firstItem = false;
				} else {
					sb.append(',');
				}
				if (isKing(k)) {
					sb.append('K');
				}
				sb.append(k);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("W");
		buildPawnsList(sb, this::isWhite);
		sb.append(":B");
		buildPawnsList(sb, this::isBlack);
		return sb.toString();
	}
	
	/**
	 * Get a string representation for drawing a tile containing a given pawnID
	 * @param pawnID
	 * @return A string representing the given pawnID
	 */
	String pawnReresentation(byte pawnID) {
		switch (pawnID) {
		case BLACK_CHECKER:	return " x";
		case BLACK_KING:	return " X";
		case WHITE_CHECKER: return " o";
		case WHITE_KING:	return " O";
		default:			return " .";
		}
	}
	
	/**
	 * Compute the number of a board tile from its coordinates
	 * @param x Column ID, starting from 0 for the left-most column
	 * @param y Row ID, stating from 0 for the bottom row (whites side of the board).
	 * @return Tile number if the tile is black (playable), 0 if the tile is white, -1 if out of board.
	 */
	public int coordsToNumber(int x, int y) {
		if (x<0 || y<0 || x>=size || y>=size) {
			return -1;
		}
		int n0 = (size - y - 1) * size/2;
		int validModulo = y % 2;
		return (x%2==validModulo) ? n0 + (x%size)/2 + 1 : 0;
	}
	
	/**
	 * Utility function to draw an ASCII-art line of the checker board
	 * @param y Row ID
	 * @param formatter Function taking a tile number and returning a two-char string representation.
	 * @return A string representing row y of the board, or a line if y is an invalid row number
	 */
	String boardLineFormatString(int y, Function<Integer, String> formatter) {
		StringBuilder sb = new StringBuilder();
		sb.ensureCapacity(size * 2 + 4);
		if (y<0 || y>=size) {
			sb.append(" ");
			for (int i=0; i<size; ++i) sb.append("--");
			sb.append("- ");
		}
		else {
			sb.append("|");
			for (int x=0; x<size; ++x) {
				int i = coordsToNumber(x,y);
				if (i != 0) {
					sb.append(formatter.apply(i));
				} else {
					sb.append("  ");
				}
			}
			sb.append(" |");
		}
		return sb.toString();
	}
	
	/**
	 * Get an ASCII-art view of the board
	 * The view represents on the left a board with tile numbers reference, 
	 * and on the right a view of the pawns on the board.
	 * @return A String containing ASCII-art representation of the board state.
	 */
	public String boardView() {
		String str = "";
		for (int y=size; y>=-1; --y) {
			str += boardLineFormatString(y, (Integer i) -> String.format("%2d", i)) 
				+ "   "
				+ boardLineFormatString(y, (Integer i) -> pawnReresentation(get(i)))
				+ "\n";
		}
		return str;
	}
	
	/**
	 * Set the contents of the given square of the board
	 * @param square Tile number
	 * @param value ID of the pawn type (e.g {@link CheckerBoard#BLACK_CHECKER}, {@link CheckerBoard#BLACK_KING}... or {@link CheckerBoard#EMPTY})
	 */
	 void set(int square, byte value) {
		state[square-1] = value;
	}
	
	/**
	 * Retrieve the contents of a given square of the board
	 * @param square Tile number
	 * @return ID of the present pawn type (or {@link CheckerBoard#EMPTY})
	 */
	public byte get(int square) {
		return state[square-1];
	}
	
	/**
	 * Get the row ID of a given square number
	 * @param square Tile number
	 * @return zero-based row ID (from the whites side)
	 */
	public int lineOfSquare(int square) {
		return size - (square - 1) / (size / 2) - 1;
	}
	
	/**
	 * Check if the square is in the top row (black side)
	 * @param square
	 * @return
	 */
	public boolean inTopRow(int square) {
		return square > 0 && square <= size/2 ; //lineOfSquare(square) == size-1;
	}
	
	/**
	 * Check if the square is in the bottom row (white side)
	 * @param square
	 * @return
	 */
	public boolean inBottomRow(int square) {
		return square > nbPlayableTiles()-size/2 && square <= nbPlayableTiles() ; //lineOfSquare(square) == 0;
	}
	
	/**
	 * Check if the square is in the left column
	 * @param square
	 * @return
	 */
	public boolean inLeftRow(int square) {
		return (square % (size / 2) == 1)
				&& (lineOfSquare(square) % 2 == 0) ;
	}
	
	/**
	 * Check if the square is in the right column
	 * @param square
	 * @return
	 */
	public boolean inRightRow(int square) {
		return (square % (size / 2) == 0)
				&& (lineOfSquare(square) % 2 == 1) ;
	}
	
	/**
	 * Get the tile-number of the top-left neighbor (i.e towards the blacks side) of a given tile
	 * @param square Number of the given tile
	 * @return Top-left neighbor of the given square number, 0 if no neighbor
	 */
	public int neighborUpLeft(int square) {
		if (!inTopRow(square) && !inLeftRow(square))
			return square - (size / 2) - (lineOfSquare(square)+1) % 2;
		else
			return 0;
	}
	
	/**
	 * Get the tile-number of the bottom-left neighbor (i.e towards the whites side) of a given tile
	 * @param square Number of the given tile
	 * @return Bottom-left neighbor of the given square number, 0 if no neighbor
	 */
	public int neighborDownLeft(int square) {
		if (!inBottomRow(square) && !inLeftRow(square))
			return square + (size / 2) - (lineOfSquare(square)+1) % 2;
		else
			return 0;
	}
	
	/**
	 * Get the tile-number of the top-right neighbor (i.e towards the blacks side) of a given tile
	 * @param square Number of the given tile
	 * @return Top-right neighbor of the given square number, 0 if no neighbor
	 */
	public int neighborUpRight(int square) {
		if (!inTopRow(square) && !inRightRow(square))
			return square - (size / 2) - (lineOfSquare(square)+1) % 2 + 1;
		else
			return 0;
	}
	
	/**
	 * Get the tile-number of the bottom-right neighbor (i.e towards the whites) of a given tile
	 * @param square Number of the given tile
	 * @return Bottom-right neighbor of the given square number, 0 if no neighbor
	 */
	public int neighborDownRight(int square) {
		if (!inBottomRow(square) && !inRightRow(square))
			return square + (size / 2) - (lineOfSquare(square)+1) % 2 + 1;
		else
			return 0;
	}
	
	/**
	 * The number of the square between the two squares <code>square1</code> and <code>square2</code>.
	 * Only valid if <code>square1</code> and <code>square2</code> are in the same diagonal.
	 * @param square1
	 * @param square2
	 * @return
	 */
	public int squareBetween(int square1, int square2) {
		if ((square1 - square2 < size/2 + 2) && (square2 - square1 < size/2 + 2))
			return 0;
		else
			return (square1 + square2) / 2 + 1 - (lineOfSquare(square1)+1) % 2;
	}
	
	/**
	 * Check if a square is empty
	 * @param square
	 * @return
	 */
	public boolean isEmpty(int square) {
		return get(square) == EMPTY;
	}
	
	/**
	 * Check if a square contains a black checker or king
	 * @param square
	 * @return
	 */
	public boolean isBlack(int square) {
		byte type = get(square);
		return type == BLACK_CHECKER || type == BLACK_KING;
	}
	
	/**
	 * Check if a square contains a white checker or king
	 * @param square
	 * @return
	 */
	public boolean isWhite(int square) {
		byte type = get(square);
		return type == WHITE_CHECKER || type == WHITE_KING;
	}
	
	/**
	 * Check if a square contains a king (black or white)
	 * @param square
	 * @return
	 */
	public boolean isKing(int square) {
		byte type = get(square);
		return type == WHITE_KING || type == BLACK_KING;
	}
	
	/**
	 * Move a pawn on the checker board
	 * @param from Origin tile number
	 * @param to Destination tile number
	 */
	public void movePawn(int from, int to) {
		set(to, get(from));
		set(from, EMPTY);
	}
	
	/**
	 * Remove a pawn from the checker board
	 * @param from Tile number of the pawn
	 */
	public void removePawn(int from) {
		set(from, EMPTY);
	}
	
	/**
	 * Promote a pawn to become a king
	 * @param square Tile number of the pawn to crown
	 */
	public void crownPawn(int square) {
		byte currentPawn = get(square);
		if (currentPawn == WHITE_CHECKER)
			set(square, WHITE_KING);
		else if (currentPawn == BLACK_CHECKER)
			set(square, BLACK_KING);
	}

	/**
	 * Retrieve the positions of all white pawns in the board
	 * @return The list of white pawns
	 */
	public ArrayList<Integer> getWhitePawns() {
		ArrayList<Integer> myPawns = new ArrayList<Integer>();
		myPawns.ensureCapacity((size/2 - 1) * (size/2));
		for (int pawn = 1; pawn <= nbPlayableTiles(); ++pawn) {
			if ( isWhite(pawn) ) {
				myPawns.add( pawn );
			}
		}
		return myPawns;
	}
	
	/**
	 * Retrieve the positions of all black pawns in the board
	 * @return The list of black pawns
	 */
	public ArrayList<Integer> getBlackPawns() {
		ArrayList<Integer> myPawns = new ArrayList<Integer>();
		myPawns.ensureCapacity((size/2 - 1) * (size/2));
		for (int pawn = 1; pawn <= nbPlayableTiles(); ++pawn) {
			if ( isBlack(pawn) ) {
				myPawns.add( pawn );
			}
		}
		return myPawns;
	}
}
