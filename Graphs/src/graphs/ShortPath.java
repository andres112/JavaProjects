package graphs;

import java.util.ArrayList;
import java.util.List;

class ShortPath {

    final int vertices;

    public ShortPath(int v) {
        this.vertices = v;
    }

    // Find min distance between vertices
    int minDistance(int dist[], Boolean verticesUsed[]) {
        int min = Integer.MAX_VALUE, min_vertice = -1;

        for (int v = 0; v < vertices; v++) {
            if (verticesUsed[v] == false && dist[v] <= min) {
                min = dist[v];
                min_vertice = v;
            }
        }
        return min_vertice;
    }

    // funtion to calculate the shortest path between two vertices
    public int[] shortestPath(int graph[][], int src, int des) {
        
        // varibles to control the distances between vertices
        int dist[] = new int[vertices];
        Boolean verticesUsed[] = new Boolean[vertices];

        for (int i = 0; i < vertices; i++) {
            dist[i] = Integer.MAX_VALUE;
            verticesUsed[i] = false;
        }
        dist[src] = 0;
        
        for (int count = 0; count < vertices - 1; count++) {
            int u = minDistance(dist, verticesUsed);
            
            verticesUsed[u] = true;

            for (int v = 0; v < vertices; v++) {
                if (!verticesUsed[v] && graph[u][v] != 0
                        && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }
        return dist;
    }

    // function to print the vertices of the best route
    public void printNodes(int[] dist, int des, int src, int[][] graph) {
        if (dist[des] != Integer.MAX_VALUE) {
            List<Integer> path = new ArrayList<>();
            path.add(des);
            int better = Integer.MAX_VALUE, currentNode = des;
            while (!path.contains(src)) {
                for (int i = 0; i < graph.length; i++) {
                    if (graph[des][i] == 1 && dist[i] < better) {
                        currentNode = i;
                        better = dist[i];
                    }
                }
                path.add(currentNode);
                des = currentNode;
            }

            System.out.println("***********\nBest Route found! ");
            for (int node = path.size() - 1; node >= 0; node--) {
                System.out.print(path.get(node) + " -> ");
            }
        }
        else
            System.out.println("***********\nThe Vertices are in different graphs ");
    }
}
