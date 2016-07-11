package connectFour;

import java.util.Scanner;
import connectFour.Grid.DropTokenReturnValue;

/***
 * Connect4 Implementation for a 7x6 board.
 * @author srengesh
 *
 */
public class Main {
	private static final int NUMROWS = 6;
	private static final int NUMCOLUMNS = 7;
	private static Grid grid;
	private static boolean isP1Turn;

	public static void main(String[] args) {
		isP1Turn = true;		
		grid = new Grid(NUMCOLUMNS, NUMROWS);
		
		System.out.println("Welcome to Connect4!\n\n");
		System.out.println(grid.getGrid());
		System.out.println("Player " + (isP1Turn ? "1" : "2") + ", please enter a row to insert into.");

		int column = (int) Math.ceil(Math.random() * 7);

//		Scanner scanner = new Scanner(System.in);
//		column = readInput(scanner);
		while (true) {
			if (column > NUMCOLUMNS || column < 1) {
				System.out.println(Integer.toString(column) + " is not a valid column. "
						+ "Please enter a number between 1 and " + Integer.toString(NUMCOLUMNS) + ".");
			}
			else {
		//			subtract by one to convert from human-friendly numbers (1-indexed) to 
		//			computer-friendly numbers (0-indexed)
				DropTokenReturnValue retVal = grid.dropTokenIntoColumn(isP1Turn, column - 1);
				if (retVal== DropTokenReturnValue.TOKENDROPPED) {
					isP1Turn = !isP1Turn;
					System.out.println(grid.getGrid());
					System.out.println("Player " + (isP1Turn ? "1" : "2") + ", please enter a row to insert into.");
				}
				else if (retVal == DropTokenReturnValue.ROWFULL) {
					System.out.println(Integer.toString(column) + " is already full. "
							+ "Please try a different column.");	
				}
				else {
					// the game has ended in one of three ways: p1 wins, p2 wins, or a tie
//					scanner.close();
					System.out.println(grid.getGrid());
					if (retVal == DropTokenReturnValue.P1WIN) {
						System.out.println("Player 1 wins!");
					}
					else if (retVal == DropTokenReturnValue.P2WIN) {
						System.out.println("Player 2 wins!");
					}
					else if (retVal == DropTokenReturnValue.TIE) {
						System.out.println("The game ends in a tie!");
					}
					return;
				}
			}
			
//			Regardless of whether there's an error or if the user successfully dropped a token, 
//			go to the next iteration
//			column = readInput(scanner);
			column = (int) Math.ceil(Math.random() * 7);
		}
	}
	
	/**
	 * @param scanner - A scanner to read stdin
	 * @return the integer that the user inputs.
	 * Note: While the user doesn't specify a valid integer, this method continues to prompt the user.
	 */
	private static int readInput(Scanner scanner) {
		while (!scanner.hasNextInt()) {
			System.out.println("That is not a valid column. "
					+ "Please enter a number between 1 and " + Integer.toString(NUMCOLUMNS) + ".");
			scanner.nextLine();
		}
		return scanner.nextInt();
	}
}
