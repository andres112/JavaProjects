package similaritydistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class SimilarityDistance {

    public static void main(String[] args) {
        
        // Instance creation
        List<DistanceMatrix> distanceMatrix = new ArrayList<>();
        Sentence sentence_1 = new Sentence();
        Sentence sentence_2 = new Sentence();
        int totalDistance = 0;
        
        System.out.println("This program calculates the similarity distance between 2 sentences.\n"
                + "Please avoid special characters for more accurate.\n");

        // Entry of information
        Scanner sc = new Scanner(System.in);
        System.out.println("Please write the correct sentence: ");
        sentence_2.setSentence(sc.nextLine());
        System.out.println("Please write the incorrecte sentence to compare: ");
        sentence_1.setSentence(sc.nextLine());

        // Instance to run the main activity
        SimilarityDistance similarityDistance = new SimilarityDistance();
        
        // Call to the main function to calculate the similarity Distance matrix, Return the total distance
        totalDistance = similarityDistance.calculateMatrices(sentence_1.getSentenceAsArray(),
                sentence_2.getSentenceAsArray(), distanceMatrix);

        // Print every similarity distance matrix comparing sentence_1 against sentence_2
        distanceMatrix.forEach(m -> {
            m.printMatrix();
        });
        
        // Print the information
        System.out.println("\n>>>>>The total similarity distance between the sentences is: " + totalDistance);

        // Only suggest correction if the sentence lengths are similar        
        if (sentence_1.getSentenceAsArray().length == sentence_2.getSentenceAsArray().length) {
            similarityDistance.printComparison(distanceMatrix);
        } 

    }

    // Main function: It calculate the similarity distance matrix
    public int calculateMatrices(String[] s_1, String[] s_2, List<DistanceMatrix> dm) {
        int totalDistance = 0;
        for (String word : s_1) {
            dm.add(new DistanceMatrix(word));
        }

        dm.forEach((matrix) -> {
            matrix.calculateMatrix(s_2);
        });

        for (DistanceMatrix matrix : dm) {
            totalDistance = totalDistance + (int) Collections.min(matrix.getMatrix().values());
        }

        return totalDistance;
    }

    // Calculate the match level if the light comparison is ambiguous
    private String deepCalculation(int distance, String word, List<String> dDV) {
        String replace = null;
        int match = 0;
        for(String w : dDV){
            int counter = 0;
            counter = this.letterLoad(word).entrySet().stream().filter((l) -> 
                    (Objects.equals(l.getValue(), this.letterLoad(w).get(l.getKey())))).
                    map((_item) -> 1).reduce(counter, Integer::sum);
            if (counter > match){
                replace = w;
            }
        }
        return replace;
    }

    // Calculate the quantity of times that a letter appear in the word
    private HashMap<Character, Integer> letterLoad(String word) {
        HashMap<Character, Integer> letters = new HashMap<>();
        for (char l : word.toCharArray()) {
            if (letters.containsKey(l)) {
                letters.replace(l, letters.get(l) + 1);
                continue;
            }
            letters.put(l, 1);
        }
        return letters;
    }

    // Function to print the correction suggested
    public void printComparison(List<DistanceMatrix> dm) {        
        System.out.println("\n**************************\n Maybe did you mean?");
        dm.forEach((matrix) -> {
            if (matrix.getMatrix().containsValue(0)) {
                System.out.print(matrix.getWord() + " ");
            } else {
                List<String> duplicatedDistanceVector = new ArrayList<>();
                String replace = null;
                int value = (int) Collections.min(matrix.getMatrix().values());
                int counter = 0;

                for (HashMap.Entry<String, Integer> entry : matrix.getMatrix().entrySet()) {
                    if (entry.getValue() == value) {
                        replace = entry.getKey();
                        counter++;
                        duplicatedDistanceVector.add(entry.getKey());
                    }
                }

                if (counter > 1) {
                    replace = this.deepCalculation(value, matrix.getWord(), duplicatedDistanceVector);
                }

                System.out.print(replace + " ");
            }
        });
        System.out.println("\n ... Or not?\n **************************");
    }
}
