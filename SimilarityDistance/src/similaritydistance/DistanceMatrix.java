package similaritydistance;

import java.util.HashMap;

// Class to handle the similarity distance matrix
public class DistanceMatrix {

    private String word;
    private HashMap<String, Integer> matrix = new HashMap<>();

    DistanceMatrix(String word) {
        this.word = word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return this.word;
    }
    
    public HashMap<String, Integer> getMatrix() {
        return this.matrix;
    }

    public HashMap calculateMatrix(String[] sentence) {
        char[] wordAsArray = this.word.toCharArray();
        for (String correctWord : sentence) {
            correctWord = correctWord.toLowerCase();
            char[] correctWordAsArray = correctWord.toCharArray();
            
            // If the words are equals the distance is directly zero
            if (correctWord.equalsIgnoreCase(this.word)) {
                matrix.put(correctWord, 0);
                continue;
            }

            // Delete or insert characters add 1 to the distance
            int extralength = 0;
            extralength = correctWordAsArray.length - wordAsArray.length;
            for (int i = 0; i < (correctWordAsArray.length < wordAsArray.length ? correctWordAsArray.length : wordAsArray.length); i++) {
                if(correctWordAsArray[i] != wordAsArray[i]){
                    extralength = Math.abs(extralength) + 1;
                }
            }            
            matrix.put(correctWord, extralength);
        }
        return matrix;
    }

    public void printMatrix(){
        System.out.println(" Word compared: " + this.word);
        System.out.println(this.matrix);
    }
}
