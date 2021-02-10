package fr.istic.ia.tp1;

import fr.istic.ia.tp1.Game.PlayerId;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class TestEnglishDraughts {
	static void clearBoard(CheckerBoard board) {
		for (int i=1; i<=board.nbPlayableTiles(); ++i) {
			board.removePawn(i);
		}
	}
	
	static void setBoard(CheckerBoard board, List<Integer> whites,  List<Integer> whiteKings, 
			 List<Integer> blacks,  List<Integer> blackKings) {
		clearBoard(board);
		for (int i : whites) { board.set(i, CheckerBoard.WHITE_CHECKER); }
		for (int i : whiteKings) { board.set(i, CheckerBoard.WHITE_KING); }
		for (int i : blacks) { board.set(i, CheckerBoard.BLACK_CHECKER); }
		for (int i : blackKings) { board.set(i, CheckerBoard.BLACK_KING); }
	}
	
	static EnglishDraughts.DraughtsMove newMove(EnglishDraughts game, List<Integer> steps) {
		EnglishDraughts.DraughtsMove move = game.new DraughtsMove();
		for (int i : steps) {
			move.add(i);
		}
		return move;
	}
	
	@Test
	public void testPossibleMovesInitWhite() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		List<Game.Move> initMoves = asList(
				newMove(draughts, asList(21,17)),
				newMove(draughts, asList(22,17)),
				newMove(draughts, asList(22,18)),
				newMove(draughts, asList(23,18)),
				newMove(draughts, asList(23,19)),
				newMove(draughts, asList(24,19)),
				newMove(draughts, asList(24,20))
			);
		List<Game.Move> moves = draughts.possibleMoves();
		System.out.println(moves);
		assertEquals("Init moves white", new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals("Duplicate moves", initMoves.size(), moves.size());
	}

	@Test
	public void testPossibleMovesInitBlack() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		draughts.play(newMove(draughts, asList(21,17)));
		assertEquals("Black should play after white", PlayerId.TWO, draughts.player());
		
		List<Game.Move> initMoves = asList(
				newMove(draughts, asList(9,13)),
				newMove(draughts, asList(9,14)),
				newMove(draughts, asList(10,14)),
				newMove(draughts, asList(10,15)),
				newMove(draughts, asList(11,15)),
				newMove(draughts, asList(11,16)),
				newMove(draughts, asList(12,16))
			);
		List<Game.Move> moves = draughts.possibleMoves();

		assertEquals("Init moves black", new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals("Duplicate moves", initMoves.size(), moves.size());
	}
	
	@Test
	public void testPossibleMovesSimpleTake() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(16,18,19), asList(7), asList(11,15), asList(24));
		
		List<Game.Move> initMoves = asList( newMove(draughts, asList(19,10)) );
		List<Game.Move> moves = draughts.possibleMoves();

		assertEquals("Simple take: 19x10", new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals("Duplicate moves", initMoves.size(), moves.size());
	}
	
	@Test
	public void testPossibleMovesSimpleTakeKing() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(16,18,19), asList(7), asList(10,15), asList(24));
		
		List<Game.Move> initMoves = asList( 
				newMove(draughts, asList(18,11)),
				newMove(draughts, asList(7,14)) );
		List<Game.Move> moves = draughts.possibleMoves();
		
		assertEquals("Simple take: pawn and king", new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals("Duplicate moves", initMoves.size(), moves.size());
	}

	
	@Test
	public void testPossibleMovesMutipleTake() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(18,19), asList(10), asList(6,8,15), asList(7));

		List<Game.Move> initMoves = asList( 
				newMove(draughts, asList(10,1)),
				newMove(draughts, asList(10,3,12)),
				newMove(draughts, asList(18,11,2)),
				newMove(draughts, asList(18,11,4)) );
		List<Game.Move> moves = draughts.possibleMoves();
		assertEquals("Multiple take", new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals("Duplicate moves", initMoves.size(), moves.size());
	}
	
	@Test
	public void testPossibleMovesMutipleTake2() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(18,19), asList(1), asList(6,8,15), asList(7));
		
		List<Game.Move> initMoves = asList( 
				newMove(draughts, asList(1,10,3,12)),
				newMove(draughts, asList(19,10,3)),
				newMove(draughts, asList(18,11,2)),
				newMove(draughts, asList(18,11,4)) );
		List<Game.Move> moves = draughts.possibleMoves();
		/* for (Game.Move move : moves) {
			System.out.println( move );
		}*/
		assertEquals("Multiple take", new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals("Duplicate moves", initMoves.size(), moves.size());
	}
	
	@Test
	public void testWinner() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(), asList(), asList(18,19), asList(1));
		assertEquals("Only blacks on board", PlayerId.TWO, draughts.winner());
	}
	
	@Test
	public void test25MovesEquality() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(), asList(22), asList(), asList(10));
		assertEquals("Game is not finished yet", null, draughts.winner());
		for (int i=0; i<6; ++i) {
			draughts.play(newMove(draughts, asList(22, 25))); // white king
			draughts.play(newMove(draughts, asList(10, 7))); // black king
			draughts.play(newMove(draughts, asList(25, 22))); // white king
			draughts.play(newMove(draughts, asList(7, 10))); // black king
		}
		draughts.play(newMove(draughts, asList(22, 25))); // white king

		System.out.println(draughts.possibleMoves());
		assertEquals("Equality", PlayerId.NONE, draughts.winner());
	}
	
	@Test
	public void testPlaySimpleMoves() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		
		draughts.play(newMove(draughts, asList(21, 17)));
		assertTrue("Leave 21", draughts.board.isEmpty(21));
		assertEquals("Go to 17", CheckerBoard.WHITE_CHECKER, draughts.board.get(17));
		
		draughts.play(newMove(draughts, asList(10, 14)));
		assertTrue("Leave 10", draughts.board.isEmpty(10));
		assertEquals("Go to 14", CheckerBoard.BLACK_CHECKER, draughts.board.get(14));
	}
	
	@Test
	public void testPlaySimpleCapture() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(16,18,19), asList(7), asList(11,15), asList(24));
		
		draughts.play(newMove(draughts, asList(19, 10)));
		assertTrue("Leave 19", draughts.board.isEmpty(19));
		assertEquals("Jump to 10", CheckerBoard.WHITE_CHECKER, draughts.board.get(10));
		assertTrue("Remove adversary from 15", draughts.board.isEmpty(15));
	}
	
	@Test
	public void testPlayMutipleTakeCrown() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(18,19), asList(10), asList(6,8,15), asList(7));
		draughts.play(newMove(draughts, asList(18,11,4)));
		assertTrue("Leave 18", draughts.board.isEmpty(18));
		assertTrue("Remove adversary from 15", draughts.board.isEmpty(15));
		assertTrue("Pass by 11", draughts.board.isEmpty(11));
		assertTrue("Remove adversary from 8", draughts.board.isEmpty(8));
		assertEquals("Finish in 4 and get crowned", CheckerBoard.WHITE_KING, draughts.board.get(4));
	}
}
