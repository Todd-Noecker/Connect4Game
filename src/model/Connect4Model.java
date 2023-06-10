package model;

import java.util.Observable;
import java.util.Observer;

/**
 * This Class functions as the model for the Connect4Game. It maintains the game
 * state with two arrays. a 2-d array which holds the current board state, and a
 * rowPos array which maintains the current column's row position. This model
 * will also maintain the current turn for the user.
 * 
 * @author Todd Noecker
 */

@SuppressWarnings("deprecation")
public class Connect4Model extends Observable {
	/*
	 * These class fields represent the game board given by an XLEN x YLEN size and
	 * will occupy an int 2-d array which will maintain the state of the current
	 * connect 4 game.
	 */
	private static int XLEN = 6;
	private static int YLEN = 7;

	private int rowPos[];
	private int gameboard[][];
	private boolean myTurn;

	public Connect4Model() {
		gameboard = genGameBoard();
		rowPos = genInitRowPos();
	}

	/**
	 * Makes a move based on the passed int value and color. 
	 * The x cord is determined by the rowPos array and will provide the correct x cord.
	 * This method still needs to handle being passed a full row cord, or it could
	 * be handled elsewhere.
	 * @param y     input cord y
	 * @param color input cord color
	 */
	public void move(int y, int color) {

		if (rowPos[y] >= 0) {
			gameboard[rowPos[y]][y] = color;
			this.setChanged();
			this.notifyObservers(new Connect4MoveMessage(y, rowPos[y], color));
			this.clearChanged();
			rowPos[y] = rowPos[y] - 1;
		} else {
			// Then the row is full.
		}
	}

	/**
	 * This method adds a passed observer to this model.
	 * 
	 * @param o Observer object passed from Observable.
	 * Adds a passed observer to this model.
	 * 
	 * @param o Observer
	 */
	public void addObs(Observer o) {
		this.addObserver(o);
	}

	/**
	 * This method will set if the current turn belongs to this Object.
	 * 
	 * @param val the boolean value containing the passed turn state.
	 * Sets if the current turn belongs to this Object.
	 * 
	 * @param val Current turn
	 */
	public void setMyTurn(boolean val) {
		myTurn = val;
	}
	
	/**
	 * Gets value set in setMyTurn.
	 * 
	 * @return myTurn Returns true if it is current objects turn
	 */
	public boolean getMyTurn() {
		return myTurn;
	}

	/**
	 * Ruturns the current turn boolean state.
	 * 
	 * 
	 */
	public boolean getTurn() {
		return myTurn;
	}

	/**
	 * This method will get the current value at the indices provided.
	 * 
	 * @param x cord and y cord respectively.
	 * @return the current val the given slot contains
	 * Gets the current value at the indices provided.
	 * 
	 * @param x X index
	 * @param y Y indsex
	 * @return gameboard[x][y] current value at provided indicies
	 */
	public int getSlot(int x, int y) {

		return gameboard[x][y];
	}

	/**
	 * Returns the current row position.
	 * 
	 * @param x Corresponds to a column
	 * @return rowPos[x] Current row position

	 */
	public int getRowPos(int y) {

		return rowPos[y];
	}

	/**

	 * This method will return the fixed X length used to establish the board.
	 * @return the fixed X length for the game board default 6.

	 * Returns the fixed X length used to establish the board.
	 * 
	 * @return XLEN
	 */
	public int getXLEN() {
		return XLEN;
	}

	/**
	 * This method will return the fixed Y length used to establish the board.
	 * 	 * @return the fixed Y length for the game board default 7.
	 * Returns the fixed Y length used to establish the board.
	 * 
	 * @return YLEN
	 */
	public int getYLEN() {
		return YLEN;
	}

	/**
	 * This method will check each row index for an open space, if none are found,
	 * then the game board has no open positions and it is full.
	 */
	public boolean isBoardFull() {
		for (int index = 0; index < YLEN; index++) {

			if (rowPos[index] >= 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method will check for a win in each valid direction, if no win in the
	 * previous direction was found the next will be checked. If a win in any
	 * This method will check for a win in each valid direction. 
	 * If no win in the previous direction was found the next will be checked. If a win in any
	 * direction is found the color searched is returned to indicate that the color
	 * has won. If no wins are detected the board is checked for remaining spaces to
	 * move a 3 is returned if the board is full. Finally a 0 is returned if the
	 * game is not over and can continue.
	 * 
	 * @param color the passed color to use in the win search.
	 * @return 1, player one wins 2, player 2 wins, 3,game board full 0, game not
	 *         over
	 */
	public int checkForWin(int color) {
		boolean wincheck1 = horWinCheck(color);
		boolean wincheck2 = false;
		boolean wincheck3 = false;
		if (wincheck1 == false) {
			wincheck2 = vertWinCheck(color);
		}
		if (wincheck1 == false && wincheck2 == false) {
			wincheck3 = diagonalChecks(color);
		}

		if (wincheck1 == true || wincheck2 == true || wincheck3 == true) {
			// return either 1 or 2 indicating a win for that player.
			return color;
		}
		if (isBoardFull() == true) {
			return 3;
		}
		return 0;
	}

	/**
	 * Checks each horizontal row in the game board for a win.
	 * Win is checked with current color and returns true if game is won
	 * 
	 * @param color Current color
	 */
	private boolean horWinCheck(int color) {
		for (int xIndex = 0; xIndex < XLEN; xIndex++) {
			int sameCount = 0;
			for (int yIndex = 0; yIndex < YLEN; yIndex++) {
				if (gameboard[xIndex][yIndex] == color) {
					sameCount++;
					if (sameCount == 4) {
						return true;
					}
				} else {
					sameCount = 0;
				}
			}
		}
		return false;
	}

	/**
	 * Checks each diagonal for a win.
	 * Validate if there is a valid end point to check for the direction in question, 
	 * if a valid end point is found all blocks in that direction are checked for a connect 4.
	 * 
	 * @param color Current color
	 */
	private boolean diagonalChecks(int color) {
		int[][] checkDir = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { 1, -1 } };
		int checkLen = 4;
		// Check each diagonal direction
		for (int[] ord : checkDir) {
			int dirX = ord[0];
			int dirY = ord[1];
			// Check all grid locations for a connect 4.
			for (int xIndex = 0; xIndex < XLEN; xIndex++) {
				for (int yIndex = 0; yIndex < YLEN; yIndex++) {
					int maxX = xIndex + checkLen * dirX;
					int maxY = yIndex + checkLen * dirY;
					// Checks to see if maximum included values in a connect 4 are valid.
					if (0 <= maxX && maxX < XLEN && 0 <= maxY && maxY < YLEN) {
						int compare = gameboard[xIndex][yIndex];
						// If comparison statement to see if all 4 indices contain the int color.
						// This if statement is the final check to see if a connect 4 was made.
						if (compare == color 
								&& compare == gameboard[xIndex + dirX][yIndex + dirY]
								&& compare == gameboard[xIndex + 2 * dirX][yIndex + 2 * dirY]
								&& compare == gameboard[xIndex + 3 * dirX][yIndex + 3 * dirY]
								&& compare == gameboard[maxX][maxY]) {
							return true;
						}
						// end if.
					}
				}
			}
		}
		return false;
	}

	/**
	*Checks vertical columns in the game board for a win for the given color.
	*
	*@param color Current Color
	*/
	private boolean vertWinCheck(int color) {
		for (int yIndex = 0; yIndex < YLEN; yIndex++) {
			int sameCount = 0;
			for (int xIndex = 0; xIndex < XLEN; xIndex++) {
				if (gameboard[xIndex][yIndex] == color) {
					sameCount++;
					if (sameCount == 4) {
						return true;
					}
				} else {
					sameCount = 0;
				}
			}
		}
		return false;
	}

	/**
	 * Generate initial emput game board.
	 * 0 is used for open spaces
	 */
	private static int[][] genGameBoard() {
		int[][] genBoard = new int[XLEN][YLEN];
		for (int xIndex = 0; xIndex < XLEN; xIndex++) {
			for (int yIndex = 0; yIndex < YLEN; yIndex++) {
				genBoard[xIndex][yIndex] = 0;
			}
		}
		return genBoard;
	}

	/**
	 * This method will generate an array of x positions to be used for keeping
	 * track of the x positions in the gameboard. These can then be incremented to
	 * indicate the next postions valid to place a game piece.
	 */
	private static int[] genInitRowPos() {
		int[] genPos = new int[YLEN];

		for (int index = 0; index < YLEN; index++) {
			genPos[index] = XLEN - 1;
		}
		return genPos;
	}
}
