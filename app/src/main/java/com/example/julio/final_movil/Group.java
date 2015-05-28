package com.example.julio.final_movil;

/**
 * Created by JULIO on 28/05/2015.
 */
public class Group {

    public String name;
    public String procedure; //Procedure_id
    public String description;

    public Group(String name, String procedure, String description) {
        this.name = name;
        this.procedure = procedure;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getProcedure() {
        return procedure;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}

