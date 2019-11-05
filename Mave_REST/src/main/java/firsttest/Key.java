package firsttest;

public class Key {
    private final String id;
    private int weight;

    public Key(String id, int weight){
        this.id = id;
        this.weight = weight;
    }

    public int getWeight(){
        return this.weight;
    }

    public String getId(){
        return this.id;
    }

    public void updateWeight(int weight){
        this.weight = weight;
    }
}