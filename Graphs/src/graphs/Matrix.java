package graphs;

import java.util.ArrayList;
import java.util.List;

public class Matrix {

    private final int vertices;
    private final int edges;
    private final int[][] matrix;

    public Matrix(int vertices, int edges) {
        
        //Atributes of the matrix
        this.vertices = vertices;
        this.edges = edges;
        this.matrix = new int[vertices][vertices];
    }

    // Get quantity of vertices
    public int getVertices() {
        return this.vertices;
    }

    // Get quantity of edges
    public int getEdges() {
        return this.edges;
    }

    // Get the matrix
    public int[][] getMatrix() {
        return this.matrix;
    }

    // Set the representation fo edges matrix
    public void setEdges(int v1, int v2) {
        matrix[v1][v2] = 1;
        matrix[v2][v1] = 1;
    }

    // Funtion to print the matrix
    public void printMatrix() {
        System.out.println("*******************\nGraph Matrix");
        int counter = 0;
        for (int[] row : this.matrix) {
            System.out.print(counter + " -> ");
            for (int column : row) {
                System.out.print(column + " ");
            }
            System.out.println("");
            counter++;
        }

    }

}
