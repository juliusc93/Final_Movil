package com.example.julio.final_movil;

import org.json.JSONArray;

/**
 * Created by JULIO on 02/06/2015.
 */
public class Step {

    private String name;
    private String next;

    public Step(String name, String next) {
        this.name = name;
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    @Override
    public String toString(){ return name; }
}
