package similaritydistance;

// class to handle the sentences entered by user
public class Sentence {
    
    private String sentence;
    
    public void setSentence (String sentence){
        this.sentence = sentence;
    }
    
    public String getSentence (){
        return this.sentence;
    }
    
    public String[] getSentenceAsArray(){
        return this.sentence.split(" ");
    }
}
