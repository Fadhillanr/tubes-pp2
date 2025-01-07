package model;

public class WasteType {
    private int id;
    private int categoryId;
    private String name;
    private String description;
    private double price;
    private String unit;
    private boolean isActive;
    private String createdBy;
    private java.util.Date createdAt;
    
    // Constructors
    public WasteType() {}
    
    public WasteType(int categoryId, String name, String description, double price, String unit) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.isActive = true;
        this.createdAt = new java.util.Date();
    }
    
    // Getters
    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getUnit() { return unit; }
    public boolean isActive() { return isActive; }
    public String getCreatedBy() { return createdBy; }
    public java.util.Date getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }
}