package com.thomas.movementnotation;

/**
 * Created by thomasoropeza on 1/19/16.
 */
public class Flow {
    private String name;
    private String description;
    private long flowID;

    public Flow(String name, String description, long flowID) {
        this.name = name;
        this.description = description;
        this.flowID = flowID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getFlowID() {
        return flowID;
    }

    public void setFlowID(long flowID) {
        this.flowID = flowID;
    }
}
