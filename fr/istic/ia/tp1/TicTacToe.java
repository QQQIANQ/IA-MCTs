package fr.istic.ia.tp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.istic.ia.tp1.Game.PlayerId;

public class TicTacToe extends Game {
	
	private PlayerId playerId;
	private char[] board;
	
	class Move implements Game.Move {
		int pos;
		
		public Move(int pos) {
			this.pos = pos;
		}

		@Override
		public String toString() {
			return "" + pos;
		}
	}
	
	public TicTacToe() {
		playerId = PlayerId.ONE;
		board = new char[3*3];
	}

	@Override
	public List<Game.Move> possibleMoves() {
		// TODO Auto-generated method stub
		ArrayList<Game.Move> moves = new ArrayList<Game.Move>();
		for (int i=0; i<board.length; ++i) {
			if (board[i] == 0) {
				moves.add(new Move(i));
			}
		}
		return moves;
	}

	@Override
	public void play(Game.Move move) {
		Move m = (Move) move;
		char mark;
		switch (playerId) {
		case ONE:
			mark = 'o';
			break;
		case TWO:
			mark = 'x';
			break;
		default:
			mark = 0;
		}
		board[m.pos] = mark;
		
		playerId = playerId.other();
	}

	@Override
	public PlayerId player() {
		return playerId;
	}

	static char markFromPlayerId(PlayerId playerId) {
		switch (playerId) {
		case ONE:
			return 'o';
		case TWO:
			return 'x';
		default:
			return 0;
		}
	}
	
	static PlayerId playerIdFromMark(char mark) {
		switch (mark) {
		case 'o':
			return PlayerId.ONE;
		case 'x':
			return PlayerId.TWO;
		default:
			return null;
		}
	}
	
	@Override
	public PlayerId winner() {
		// Horizontal / vertical
		for (int i=0; i<3; ++i) {
			int off = 3*i;
			if (board[i+off] == 0)
				continue;
			else if (board[i] == board[i+3] && board[i] == board[i+6])
				return playerIdFromMark(board[i]);
			else if (board[off] == board[off+1] && board[off] == board[off+2])
				return playerIdFromMark(board[off]);
		}
		// Diagonal
		char center = board[1+3*1];
		if (center != 0) {
			if ((board[0] == center && board[2+3*2] == center) ||
			    (board[2] == center && board[0+3*2] == center))
				return playerIdFromMark(center);
		}
		// Unfinished game
		for (int i=0; i<board.length; ++i) {
			if (board[i] == 0)
				return null;
		}
		// Equality
		return PlayerId.NONE;
	}

	@Override
	public String playerName(PlayerId playerId) {
		switch (playerId) {
		case ONE:
		case TWO:
			return "Player with the '" + markFromPlayerId(playerId) + "'";
		case NONE:
		default:
			return "Nobody";
		}
	}
	
	@Override
	public String view() {
		StringBuffer sb = new StringBuffer();
		sb.append("The ");
		sb.append(markFromPlayerId(playerId));
		sb.append(" are playing...\n");
		sb.append("    moves     board");
		for (int off=0; off<9; off+=3) {
			sb.append("\n   ");
			for (int i=off; i<off+3; ++i) {
				sb.append(' ');
				sb.append(board[i] == 0 ? i : "X");
			}
			sb.append("    ");
			for (int i=off; i<off+3; ++i) {
				sb.append(' ');
				sb.append(board[i] != 0 ? board[i] : '.');
			}
		}
		sb.append('\n');
		return sb.toString();
	}
	
	@Override
	public Game clone() {
		TicTacToe newGame = new TicTacToe();
		newGame.playerId = playerId;
		for (int i=0; i<board.length; ++i) {
			newGame.board[i] = board[i];
		}
		return newGame;
	}

}
