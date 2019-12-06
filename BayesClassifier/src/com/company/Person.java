package com.company;

public class Person {
    private int age;
    private String firstname, lastname, id, type;

    public Person(String id, String firstname, String lastname, int age, String type){
        this.age = age;
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.type = type;
    }

    public String getId(){
        return this.id;
    }

    public int getAge(){
        return this.age;
    }

    public String getFirstName(){
        return this.firstname;
    }

    public String getLastName(){
        return this.lastname;
    }
}
