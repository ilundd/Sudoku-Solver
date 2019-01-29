// Ian Lundberg (IDL3)
// Solves sudoku puzzles with backtracking

import java.util.List;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sudoku {
	
	private static int[] recentNumber = {0, 0};
	private static int   printCount = 0;
	
	static boolean isFullSolution(int[][] board) {
		
		// looks for empty cells
		for (int y = 0; y < 9; y++)
			for (int x = 0; x < 9; x++)
				if (board[y][x] == 0)
					return false;
				
		// looks for duplicates
		for (int y = 0; y < 9; y++)
			for (int x = 0; x < 9; x++)
				if (reject(board, new int[]{y, x}))
					return false;
		
		return true;
	}

	static boolean reject(int[][] board, int[] coords) {
		
		// checks if the row already contains the number
		for (int x = 0; x < 9; x++)
			if (board[coords[0]][coords[1]] == board[coords[0]][x] && coords[1] != x)
				return true;
		
		// checks if the column already contains the number
		for (int y = 0; y < 9; y++)
			if (board[coords[0]][coords[1]] == board[y][coords[1]] && coords[0] != y)
				return true;

			
		int colIndex = 0, rowIndex = 0;
		// moves to the specified subgrid
		switch	(coords[0]){
			case 0: case 1: case 2:
				rowIndex = 0;
				break;
			case 3: case 4: case 5:
				rowIndex = 1;
				break;
			case 6: case 7: case 8:
				rowIndex = 2;
		}	
		
		switch (coords[1]){
			case 0: case 1: case 2:
				colIndex = 0;
				break;
			case 3: case 4: case 5:
				colIndex = 1;
				break;
			case 6: case 7: case 8:
				colIndex = 2;
		}
		// loops through and checks the selected subgrid for duplicates
		for	(int y = 0; y < 3; y++){
			int row = (rowIndex * 3) + y;
			for (int x = 0; x < 3; x++){
				int column = (colIndex * 3) + x;
				if (board[coords[0]][coords[1]] == board[row][column] && coords[0] != row && coords[1] != column)
					return true;
			}
		}
		
		return false;
	}

	static int[][] extend(int[][] board, int[] coords) {
		
		// creates new 2d array to hold the extended partial solution
		int[][] partialSolution = new int[9][9];
		
		// copies the board into a new 2d array
		for (int y = 0; y < 9; y++)
			for (int x = 0; x < 9; x++)
				partialSolution[y][x] = board[y][x];
		
		// incremements the empty cell
		for (int y = 0; y < 9; y++)
			for (int x = 0; x < 9; x++)
				if (partialSolution[y][x] == 0){
					partialSolution[y][x] = 1;
					
					recentNumber[0] = y;
					recentNumber[1] = x;
					
					coords[0] = y;
					coords[1] = x;
					
					return partialSolution;
				}
				
		return null;
	}

	static int[][] next(int[][] board, int[] coords) {
		
		// creates new 2d array to hold the incremented partial solution
		int[][] partialSolution = new int[9][9];
		
		// copies the board into the new partial solution
		for (int y = 0; y < 9; y++)
			for (int x = 0; x < 9; x++)
				partialSolution[y][x] = board[y][x];
		
		// increments the recently placed number
		if (partialSolution[coords[0]][coords[1]] < 9){
			partialSolution[coords[0]][coords[1]]++;
			return partialSolution;
		}
		return null;
	}

	static void testIsFullSolution() {
		
		System.out.println("--------------------------");
		System.out.println("Testing isFullSolution()");
		System.out.println("--------------------------");

        // test 1
        int[][] fullSolution = readBoard("fs-solved-test-board.su");

        System.out.println("Test 1:");
        printBoard(fullSolution);
		
		// should return true
        System.out.println("The first test case should be a full solution. Result of isFullSolution(): "
			+ isFullSolution(fullSolution));
        System.out.println();
		
		System.out.println("--------------------------");

        // test 2
        int[][] testSolution = readBoard("ps-board-with-empty-cells.su");
		
		System.out.println("Test 2:");
		printBoard(testSolution);
		
		System.out.println("The second test case should NOT be a full solution; contains empty cells. Result of isFullSolution(): "
			+ isFullSolution(testSolution));
		System.out.println();
		
		System.out.println("--------------------------");
		
		// test 3
		testSolution = readBoard("fs-duplicate-in-row.su");
		
		System.out.println("Test 3:");
		printBoard(testSolution);
		
		System.out.println("The third test case should NOT be a full solution; contains repeats in row 8. Result of isFullSolution(): "
			+ isFullSolution(testSolution));
		System.out.println();
		
		System.out.println("--------------------------");
		
		// test 4
		testSolution = readBoard("fs-duplicate-in-column.su");
		
		System.out.println("Test 4:");
		printBoard(testSolution);
		
		System.out.println("The fourth test case should NOT be a full solution; contains repeats in column 7. Result of isFullSolution(): "
			+ isFullSolution(testSolution));
		System.out.println();
		
		System.out.println("--------------------------");
	}

	static void testReject() {
		
		System.out.println("Testing reject()");
		System.out.println("--------------------------");

        // test 1
        int[][] rejected = readBoard("ps-duplicate-in-subgrid.su");

        System.out.println("Test 1:");
        printBoard(rejected);
         //Reject because two numbers in the same 3x3 box
        System.out.println("The first test case should be rejected at (6,6). Result of reject(): " 
			+ reject(rejected, new int[]{6, 6}));
        System.out.println();
		
		System.out.println("--------------------------");

        // test 2
        int[][] fullSolution = readBoard("fs-solved-test-board.su");

        System.out.println("Test 2:");
        printBoard(fullSolution);
        System.out.println("The second test case should NOT be rejected anywhere. Result of reject(): "
			+ reject(fullSolution, new int[]{8, 8}));
        System.out.println();
		
		System.out.println("--------------------------");

		// test 3
        fullSolution = readBoard("3-medium.su");

        System.out.println("Test 2 (3-medium.su):");
        printBoard(fullSolution);
        System.out.println("The third test case should NOT be rejected anywhere. Result of reject(): "
			+ reject(fullSolution, new int[]{8, 8}));
        System.out.println();
		
		System.out.println("--------------------------");

		// test 4
        rejected = readBoard("ps-duplicate-in-row.su");

        System.out.println("Test 4:");
        printBoard(rejected);
         //Reject because two numbers in the same row
        System.out.println("The fourth test case should be rejected at (5,2). Result of reject(): " 
			+ reject(rejected, new int[]{5, 2}));
        System.out.println();
		
		System.out.println("--------------------------");

		// test 5
        rejected = readBoard("ps-duplicate-in-column.su");

        System.out.println("Test 5:");
        printBoard(rejected);
         //Reject because two numbers in the same column
        System.out.println("The fifth test case should be rejected at (4,4). Result of reject(): " 
			+ reject(rejected, new int[]{4, 4}));
        System.out.println();
		
		System.out.println("--------------------------");
	}

	static void testExtend() {
		
		System.out.println("Testing extend()");
		System.out.println("--------------------------");

        // test 1
        int[][] board = readBoard("1-trivial.su");

        System.out.println("Test 1 Initial Board: ");
        printBoard(board);
        board = extend(board, new int[]{recentNumber[0], recentNumber[1]});
        System.out.println("After extend(), the cell at (0,1) should be 1. \n\nExtended Board:");
        printBoard(board);
        System.out.println();
		
		System.out.println("--------------------------");

		// test 2
        board = readBoard("2-easy.su");

        System.out.println("Test 2 Initial Board: ");
        printBoard(board);
        board = extend(board, new int[]{recentNumber[0], recentNumber[1]});
        System.out.println("After extend(), the cell at (0,1) should be 1. \n\nExtended Board:");
        printBoard(board);
        System.out.println();
		
		System.out.println("--------------------------");
		
		// test 3
		board = readBoard("3-medium.su");

        System.out.println("Test 3 Initial Board: ");
        printBoard(board);
        board = extend(board, new int[]{recentNumber[0], recentNumber[1]});
        System.out.println("After extend(), the cell at (0,1) should be 1. \n\nExtended Board:");
        printBoard(board);
        System.out.println();
		
		System.out.println("--------------------------");
		
		// test 4
		board = readBoard("4-hard.su");

        System.out.println("Test 4 Initial Board: ");
        printBoard(board);
        board = extend(board, new int[]{recentNumber[0], recentNumber[1]});
        System.out.println("After extend(), the cell at (0,0) should be 1. \n\nExtended Board:");
        printBoard(board);
        System.out.println();
		
		System.out.println("--------------------------");
		
		// test 5
		board = readBoard("5-evil.su");

        System.out.println("Test 5 Initial Board: ");
        printBoard(board);
        board = extend(board, new int[]{recentNumber[0], recentNumber[1]});
        System.out.println("After extend(), the cell at (0,1) should be 1. \n\nExtended Board:");
        printBoard(board);
        System.out.println();
		
		System.out.println("--------------------------");
	}

	static void testNext() {
		System.out.println("Testing next()");
		System.out.println("--------------------------");

        // test 1
        int[][] board = readBoard("1-trivial.su");

        System.out.println("Test 1 Initial Board:");
        printBoard(board);
        board = next(board, new int[]{0, 3});
        System.out.println("The current cell is at (0,3) and is 7. After next(), it should be 8. \n\nNext Board:");
        printBoard(board);
        System.out.println();
		
		System.out.println("--------------------------");
		
        // test 2
        board = readBoard("2-easy.su");

        System.out.println("Test 2 Initial Board:");
        printBoard(board);
        board = next(board, new int[]{1, 0});
        System.out.println("The current cell is at (1,0) and is 6. After next(), it should be 7. \n\nNext Board:");
        printBoard(board);
        System.out.println();

		System.out.println("--------------------------");
		
        // test 3
        board = readBoard("3-medium.su");

        System.out.println("Test 3 Initial Board:");
        printBoard(board);
        board = next(board, new int[]{0, 0});
        System.out.println("The current cell is at (0,0) and is 4. After next(), it should be 5. \n\nNext Board:");
        printBoard(board);
        System.out.println();

		System.out.println("--------------------------");
		
        // test 4
        board = readBoard("4-hard.su");

        System.out.println("Test 4 Initial Board:");
        printBoard(board);
        board = next(board, new int[]{8, 8});
        System.out.println("The current cell is at (8,8) and is 0. After next(), it should be 1. \n\nNext Board:");
        printBoard(board);
        System.out.println();

		System.out.println("--------------------------");
		
        // test 5
        board = readBoard("5-evil.su");

        System.out.println("Test 5 Initial Board:");
        printBoard(board);
        board = next(board, new int[]{7, 3});
        System.out.println("The current cell is at (7,3) and is already 9. After next(), the Sudoku should be No assignment (null). \n\nNext Board:");
        printBoard(board);
        System.out.println();
		
		System.out.println("--------------------------");

        // test 6
        board = board = readBoard("3-medium.su");

        System.out.println("Test 6 Initial Board:");
        printBoard(board);
        board = next(board, new int[]{2, 8});
        board = next(board, new int[]{2, 8});
        System.out.println("The current cell is at (2,8) and is 6. After two calls of next(), it should be 8. \n\nNext Board:");
        printBoard(board);

        board = next(board, new int[]{2, 8});
        System.out.println("After another call of next(), the value at (2,8) should be 9. \n\nNext Board:");
        printBoard(board);

	}

	static void printBoard(int[][] board) {
		if(board == null) {
			System.out.println("No assignment");
			return;
		}
		for(int i = 0; i < 9; i++) {
			if(i == 3 || i == 6) {
				System.out.println("----+-----+----");
			}
			for(int j = 0; j < 9; j++) {
				if(j == 2 || j == 5) {
					System.out.print(board[i][j] + " | ");
				} else {
					System.out.print(board[i][j]);
				}
			}
			System.out.print("\n");
		}
	}

	static int[][] readBoard(String filename) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
		} catch (IOException e) {
			return null;
		}
		int[][] board = new int[9][9];
		int val = 0;
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				try {
					val = Integer.parseInt(Character.toString(lines.get(i).charAt(j)));
				} catch (Exception e) {
					val = 0;
				}
				board[i][j] = val;
			}
		}
		return board;
	}

	static int[][] solve(int[][] board, int[] coords) {
		// starts extending if the first cell is 0

		if (board[0][0] == 0){
			coords = new int[2];
			int[][] attempt = extend(board, coords);
			while (attempt != null) {
				int[][] solution = solve (attempt, coords);
				if (solution != null)
					return solution;
				attempt = next(attempt, coords);
			}
		}
		
		if(reject(board, coords)) return null;
		if(isFullSolution(board)) return board;
		coords = new int[2];
		int[][] attempt = extend(board, coords);
		while (attempt != null) {
			int[][] solution = solve(attempt, coords);
			if(solution != null) return solution;
			attempt = next(attempt, coords);
		}
		return null;
	}

	public static void main(String[] args) {
		if(args[0].equals("-t")) {
			testIsFullSolution();
			testReject();
			testExtend();
			testNext();
		} else {
			int[] coords = new int[2];
			int[][] board = readBoard(args[0]);
			printBoard(board);
			System.out.println("Solution:");
			int[][] solvedBoard = solve(board, coords);
			if (solvedBoard == null)
				System.out.println("Board not solvable!");
			else
				printBoard(solvedBoard);
		}
	}
}

