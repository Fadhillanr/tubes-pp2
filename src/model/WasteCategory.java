package model;

public class WasteCategory {
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private String createdBy;
    private java.util.Date createdAt;
    
    // Constructors
    public WasteCategory() {}
    
    public WasteCategory(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
        this.createdAt = new java.util.Date();
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public String getCreatedBy() { return createdBy; }
    public java.util.Date getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }
}