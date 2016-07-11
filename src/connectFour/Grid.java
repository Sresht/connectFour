package connectFour;

import java.util.HashMap;

/**
 * Connect4 Grid. Blank slots are denoted by spaces, while slots filled by players are denoted by one of two tokens.
 * @author srengesh
 *
 */
public class Grid {
	private Character[][] grid;
//	hashmap describing columns to the row that the coin would fall to using gravity
	private HashMap<Integer, Integer> nextIndexToInsert;
//	how many empty spots are left on the grid
	private int numEmptySpots;
	private final char PLAYER1TOKEN = 'X';
	private final char PLAYER2TOKEN = 'O';

	public Grid(int columns, int rows) {
		this.grid = new Character[rows][columns];
		this.nextIndexToInsert = new HashMap<Integer, Integer>();
		this.numEmptySpots = columns * rows;
		
		// initialize the grid with blank spaces
		for(int i = 0; i < rows; i++) {
			Character[] blankRow = new Character[columns];
			for(int j = 0; j < columns; j++) {
				blankRow[j] = ' ';
			}
			grid[i] = blankRow;
		}
		
		for (int i = 0; i < columns; i++) {
//			we're going from the last array down, so we're starting with rows - 1 at the bottom
			this.nextIndexToInsert.put(i, rows - 1);
		}
	}
	
	/**
	 * Enumeration of what different values could be a result of dropping a tile.
	 * @author srengesh
	 *
	 */
	public enum DropTokenReturnValue {
		ROWFULL, 
		TOKENDROPPED, 
		P1WIN,
		P2WIN,
		TIE,
	}

	/**
	 * This internal method is used exlusively to build a user-facing row for the user to interact with.
	 * @param row
	 * @return user-friendly string representation of the row.
	 */
	private String getSingleRow(Character[] row) {
		StringBuilder str = new StringBuilder("|");
		for (int i = 0; i < row.length; i++) {
			str.append(row[i]);
			str.append("|");
		}
		str.append("\n");
		return str.toString();
	}
	
	/**
	 * @return user-friendly string representation of the grid.
	 */
	public String getGrid() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.grid.length; i++) {
			str.append(getSingleRow(this.grid[i]));
		}
		str.append("---------------");
		return str.toString();
	}

	/**
	 * 
	 * @param isPlayerOne
	 * @param column - integer specifying which column to drop a token into
	 * @return the result of the drop as an enumerated value.
	 */
	public DropTokenReturnValue dropTokenIntoColumn(boolean isPlayerOne, int column) {
		char token = isPlayerOne ? PLAYER1TOKEN : PLAYER2TOKEN;
		int nextIndex = nextIndexToInsert.get(column);
		
//		if the index is -1, then we know that the row is filled to capacity
		if (nextIndex == -1) {
			return DropTokenReturnValue.ROWFULL;
		}
		
		grid[nextIndex][column] = token;
		
//		the next index that a coin would drop into would be one above the one that we just dropped to
		nextIndexToInsert.put(column, nextIndex - 1);
		
		int[] indices = new int[]{column, nextIndex};
		if(diagonalWin(indices) || horizontalWin(indices) || verticalWin(indices)) {
			return isPlayerOne ? DropTokenReturnValue.P1WIN : DropTokenReturnValue.P2WIN;
		}
		
//		if we have no more empty slots after inserting, then we know that there's a tie
		if (--this.numEmptySpots == 0) {
			return DropTokenReturnValue.TIE;
		}
		return DropTokenReturnValue.TOKENDROPPED;
	}
	/**
	 * 
	 * @param indices - coordinates of the new dropped token
	 * @return true if dropping this token results in a diagonal connect4
	 */
	private boolean diagonalWin(int[] indices) {
		return (
			diagonalWin(indices[0], indices[1], Direction.UP)
			|| diagonalWin(indices[0] - 1, indices[1] + 1, Direction.UP)
			|| diagonalWin(indices[0] - 2, indices[1] + 2, Direction.UP)
			|| diagonalWin(indices[0] - 3, indices[1] + 3, Direction.UP)
			|| diagonalWin(indices[0],     indices[1],     Direction.DOWN)
			|| diagonalWin(indices[0] - 1, indices[1] - 1, Direction.DOWN)
			|| diagonalWin(indices[0] - 2, indices[1] - 2, Direction.DOWN)
			|| diagonalWin(indices[0] - 3, indices[1] - 3, Direction.DOWN)
		);
	}
	
	/**
	 * @param x, y - coordinates of the start of a potential diagonal connect4.
	 * @param dir - up or down
	 * @return true if there's a diagonal connect4 (either going up or down) that STARTS at (x,y) on the grid
	 * and proceeds rightward
	 */
	private boolean diagonalWin(int x, int y, Direction dir) {
		if (x < 0 || y < 0 || x > grid[0].length - 1 || y > grid.length - 1) {
			return false;
		}
		if (dir == Direction.UP) {
			if (x > grid[0].length - 4 || y < 3) {
				return false;
			}

			char firstGridElement = getGridElement(x, y);
			return (
				firstGridElement == getGridElement(x + 1, y - 1)
				&& firstGridElement == getGridElement(x + 2, y - 2)
				&& firstGridElement == getGridElement(x + 3, y - 3)
			);
		}
		if (dir == Direction.DOWN) {
			if (x > grid[0].length - 4 || y > 2) {
				return false;
			}

			char firstGridElement = getGridElement(x, y);
			return (
				firstGridElement == getGridElement(x + 1, y + 1)
				&& firstGridElement == getGridElement(x + 2, y + 2)
				&& firstGridElement == getGridElement(x + 3, y + 3)
			);
		} 
		return false;
	}
	
	/**
	 * 
	 * @param indices - coordinates of the new dropped token
	 * @return true if dropping this token results in a horizontal connect4
	 */
	private boolean horizontalWin(int[] indices) {
		return (
			horizontalWin(indices[0] - 3, indices[1])
			|| horizontalWin(indices[0] - 2, indices[1])
			|| horizontalWin(indices[0] - 1, indices[1])
			|| horizontalWin(indices[0], indices[1])
		);
	}
	
	/**
	 * 
	 * @param x, y - coordinates of the start of a potential horizontal connect4.
	 * @return true if there's a horizontal connect4 that STARTS at (x,y) on the grid and proceeds rightward
	 */
	private boolean horizontalWin(int x, int y) {
		if (x < 0 || x > grid.length - 3) {
			return false;
		}
		char firstGridElement = getGridElement(x, y);
		return (
			firstGridElement == getGridElement(x + 1, y) 
			&& firstGridElement == getGridElement(x + 2, y)
			&& firstGridElement == getGridElement(x + 3, y)
		);
	}

	/**
	 * 
	 * @param x, y - coordinates of the start of a potential vertical connect4.
	 * @return true if there's a vertical connect4 that STARTS at (x,y) on the grid and proceeds downwards
	 * (because there's no way that we can have a vertically upwards connect4 from a newly dropped token)
	 */
	
	private boolean verticalWin(int[] indices) {
		if (indices[1] > grid.length - 4 || indices[1] < 0) {
			return false;
		}
		
		char currGridElement = getGridElement(indices[0], indices[1]);
		return (
			getGridElement(indices[0], indices[1] + 1) == currGridElement
			&& getGridElement(indices[0], indices[1] + 2) == currGridElement
			&& getGridElement(indices[0], indices[1] + 3) == currGridElement
		);
	}
	
	/**
	 * Enumeration of direction used for diagonals
	 * @author srengesh
	 *
	 */
	private enum Direction {
		UP,
		DOWN,
	}
	
	/**
	 * Helper method to return the value at a cell given x and y coordinates
	 */
	private char getGridElement(int x, int y) {
		return this.grid[y][x];
	}
}
