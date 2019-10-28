/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sodoku;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author andre
 */
public class Sodoku {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Instance of the Board
        Board board = new Board();

        // Instance of the main App
        Sodoku player = new Sodoku();
        player.play(board);

    }

    // Method where the user plays
    public void play(Board board) {
        Scanner sc = new Scanner(System.in);
        int current;
        System.out.println("Welcome to Easiest Sodoku\n===========================");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println("Type a number between 1 and 9 for the position (" + i + " - " + j + ") or 0 to exit:");
                
                // Exceptions handling to avoid the system crash
                try {
                    current = sc.nextInt();
                    if (current == 0) {
                        System.out.println("Good bye!");
                        System.exit(0);
                    }
                    if (!verifyNumber(current, board)) {
                        j--;
                    } else {
                        board.setNumber(i, j, current);
                        board.printBoard();
                        if (board.validateGoal()) {
                            System.out.println("Game over!");
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("The data typed is incorrect: " +e);
                    sc.next();
                    j--;
                } catch (Exception e) {
                    System.out.println(e);
                    sc.next();
                    j--;
                }
            }
        }
    }

    // Method to validate some considerations
    private boolean verifyNumber(int number, Board board) {
        boolean duplicated = false;

        // Check if data is duplicated
        for (int[] row : board.getMatrix()) {
            for (int element : row) {
                if (number == element) {
                    duplicated = true;
                }
            }
        }
        if (number > 9 || number < 1) {
            System.out.println("The number should be between 1 and 9");
            return false;
        } else if (duplicated) {
            System.out.println("The number is already in the board, please select other");
            return false;
        }
        return true;
    }
}
