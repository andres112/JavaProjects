package graphs;

import java.util.Scanner;

public class Graphs {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int v = 1, e= 0;

        // Creation of instance of the main class
        Graphs graph = new Graphs();

        // System asks to for data
        boolean isdata = false;
        while (!isdata) {
            System.out.println("Please insert the number bigger than one for Graph´s Vertices: ");
            v = scan.nextInt();
            System.out.println("Please insert the number bigger than zero for Graph´s Edges: ");
            e = scan.nextInt();
            if (v > 1 && e > 0) {
                isdata = !isdata;
            }
        }

        // Instance of Matrix inicialized with number of vertices and edges
        Matrix matrix = new Matrix(v, e);

        //Execute the main functionality
        graph.generateMatrix(matrix);
        matrix.printMatrix();

        boolean isroute = false;
        int src = 0, des = 0;
        while (!isroute) {
            System.out.println("Please insert the <source> <destination> to get the best route: ");
            src = scan.nextInt();
            des = scan.nextInt();
            if (src >= 0 && src < v && des >= 0 && des < v) {
                if (des != src) {
                    isroute = !isroute;
                }
            }
        }

        // Instance of the ShortPath instance
        ShortPath path = new ShortPath(v);
        int[] dist = path.shortestPath(matrix.getMatrix(), src, des);

        path.printNodes(dist, des, src, matrix.getMatrix());

    }

    public void generateMatrix(Matrix matrix) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the edges <Vertice 1> <Vertice2>: ");
        for (int i = 0; i < matrix.getEdges(); i++) {
            System.out.println("Vertice " + i);
            try {
                int v1 = scan.nextInt();
                int v2 = scan.nextInt();
                if (v1 == v2) {
                    i--;
                    System.out.println("The edge should not set to the vertice itself");
                } else if (matrix.getVertices() <= v2 || matrix.getVertices() <= v1 || v1 < 0 || v2 < 0) {
                    i--;
                    System.out.println("Some data does not correspond to a valid vertice");
                } else {
                    matrix.setEdges(v1, v2);
                }
            } catch (Exception e) {
                System.out.println("The data typed is incorrect: " + e);
                scan.next();
                i--;
            }

        }
    }
}
