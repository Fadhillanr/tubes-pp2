package model;

import java.sql.Timestamp;
import java.util.Date;

public class WasteCategory {

    private int id;
    private String name;
    private java.util.Date createdAt;

    public WasteCategory() {
        this.createdAt = new java.util.Date();
    }

    public WasteCategory(String name) {
        this();
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = new Date(createdAt.getTime());
    }

    @Override
    public String toString() {
        return name;
    }
}
