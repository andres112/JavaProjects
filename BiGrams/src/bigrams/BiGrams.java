/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigrams;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author andre
 */
public class BiGrams {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String text;
        String[] bigramVector;

        // Data entered by the user
        Scanner sc = new Scanner(System.in);
        System.out.println("Please write some text: ");
        text = sc.nextLine();

        // Instance creation
        BiGrams bigramer = new BiGrams();
        bigramVector = bigramer.getBiagram(text);

        // Create the array of Bigram objects
        Bigram[] bigramArray = new Bigram[bigramVector.length];
        int i = 0;
        
        // Create the array of Bigrams
        for (String bigram : bigramVector) {
            bigramArray[i++] = new Bigram(bigram, bigramer.getFrequency(bigram, bigramVector));
        }

        // Remove the duplicate information
        bigramArray = bigramer.removeDuplicates(bigramArray);
        
        System.out.println("\nBi-Grams of the text: \n==============================\n");
        bigramer.printBigrams(bigramArray);
    }

    // Get the biagrams in the text
    public String[] getBiagram(String text) {
        String[] splitedText;
        splitedText = text.split(" ");
        String[] coupleText;
        coupleText = new String[splitedText.length - 1];
        for (int i = 0; i < splitedText.length - 1; i++) {
            coupleText[i] = splitedText[i] + " " + splitedText[i + 1];
        }
        return coupleText;
    }

    // Get the frequency of bigram whithin the text
    public int getFrequency(String biagram, String[] bigramVector) {
        int frequency = 0;
        for (String item : bigramVector) {
            if (item.equals(biagram)) {
                frequency++;
            }
        }
        return frequency;
    }

    // Remove duplicated information
    public Bigram[] removeDuplicates(Bigram[] bigramArray) {
        List<Bigram> list = new ArrayList<>();
        for (Bigram current : bigramArray) {
            if (!list.stream().anyMatch(x -> x.getBigram().equals(current.getBigram()))) {
                list.add(current);
            }
        }
        return list.toArray(new Bigram[list.size()]);
    }

    // Print de information
    public void printBigrams(Bigram[] bigramArray) {
        for (Bigram item : bigramArray) {
            System.out.println("Bigram: " + item.getBigram() + "- Frequency: " + item.getFrequency());
        }
    }
}
