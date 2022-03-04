package com.hou.stream.predicate;

public class Bclass extends Aclass {
    public Bclass() {
        System.out.println("b conctruct");
    }
    public static void main(String[] args) {
        new Bclass() ;
        new Bclass() ;
    }
    {
        System.out.println("i am bclass");
    }
    static {
        System.out.println("static bclass");
    }


}
class Aclass {

    {
        System.out.println("i am aclass");
    }
    static {
        System.out.println("static aclass");
    }
}