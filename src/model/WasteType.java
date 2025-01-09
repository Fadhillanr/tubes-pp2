package model;

import java.sql.Timestamp;
import java.util.Date;

public class WasteType {

    private int id;
    private int categoryId;
    private String name;
    private String description;
    private String weight;
    private String unit;
    private Date createdAt;

    public WasteType() {
    }

    public WasteType(int categoryId, String name, String description, String weight, String unit) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.unit = unit;
        this.createdAt = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = new Date(createdAt.getTime());
    }
}
