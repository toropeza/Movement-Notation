package com.thomas.movementnotation;

/**
 * Created by toropeza on 11/3/15.
 */
public class Technique {
    private String name;
    private int id;
    private String description;
    private String body;

    public Technique() {
    }

    public Technique(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Technique(String name, int id, String description, String body) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
