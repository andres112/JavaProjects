/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sodoku;

/**
 *
 * @author andre
 */
public class Board {

    // Atrribute of the class 
    private int[][] matrix = new int[3][3];

    // Methods of the class
    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public void setNumber(int indexX, int indexY, int number) {
        if ((indexX < 3 && indexX >= 0) && (indexX < 3 && indexX >= 0)) {
            this.matrix[indexX][indexY] = number;
        } else {
            System.out.println("The position is incorrect");
        }
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    private int getNumber(int indexX, int indexY) {
        if ((indexX < 3 && indexX >= 0) && (indexX < 3 && indexX >= 0)) {
            return this.matrix[indexX][indexY];
        }
        System.out.println("The position is incorrect");
        return 0;
    }

    public boolean validateGoal() {
        for (int i = 0; i < this.getMatrix().length; i++) {
            for (int j = 0; j < this.getMatrix()[i].length; j++) {
                if (this.getNumber(i, j) == 0) {
                    return false;
                }
            }
        }

        if (validateRows() && validateColumns() && validateDiagonals()) {
            System.out.println("You have won. You rock!!!");
        } else {
            System.out.println("You have lost. Good luck to the next.");
        }
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < 3; i++) {
            System.out.println("[" + getNumber(i, 0) + "] [" + getNumber(i, 1) + "] [" + getNumber(i, 2) + "]\n");
        }
    }

    private boolean validateRows() {
        int sumRow = 0;
        for (int[] row : this.getMatrix()) {
            for (int j = 0; j < row.length; j++) {
                sumRow += row[j];
            }
            if (sumRow == 15) {
                sumRow = 0;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean validateColumns() {
        int sumColumn = 0;
        for (int i = 0; i < this.getMatrix().length; i++) {
            for (int j = 0; j < this.getMatrix()[i].length; j++) {
                sumColumn += this.getNumber(j, i);
            }
            if (sumColumn == 15) {
                sumColumn = 0;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean validateDiagonals() {
        int sumDiagonal_l = 0, sumDiagonal_r = 0;
        for (int i = 0; i < this.getMatrix().length; i++) {
            sumDiagonal_l += this.getNumber(i, i);
            
            // invert the index to get the sum of the another diagonal
            int inv = this.getMatrix().length - 1 - i;
            sumDiagonal_r += this.getNumber(i, inv);
        }
        return (sumDiagonal_r == 15 && sumDiagonal_l == 15);
    }
}
