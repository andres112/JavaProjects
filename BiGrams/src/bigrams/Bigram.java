/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigrams;

/**
 *
 * @author andre
 */

// The Bi-gram abstraction class
public class Bigram {
    
    // Attributes of the class
    private String bigram;
    private int frequency;
    
    // Override the constructor by default
    Bigram(String bigram, int frequency){
        this.bigram = bigram;
        this.frequency = frequency;
    }
    
    // Methods of the class
    public void setBigram(String bigram){
        this.bigram = bigram;
    }
    
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }
    
    public String getBigram(){
        return this.bigram;
    }
    
    public int getFrequency(){
        return this.frequency;
    }
}
