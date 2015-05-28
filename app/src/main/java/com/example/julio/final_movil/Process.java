package com.example.julio.final_movil;

/**
 * Created by JULIO on 28/05/2015.
 */
public class Process {

    public String name;
    public String group;

    public Process(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return name;
    }
}
