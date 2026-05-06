package Tfast_Rmoney.Givvy.core;

public class User {
    private String userId;
    private String name;
    private String email;
    private String passwordHash; 
    private String phone;
    private String createdAt;
    private double satisfactionScore;

    // Getters and setters
    
    public String getUserId() { return userId; }
    
    public void setUserId(String userId) {this.userId = userId;}
    
    public String getName() { return name; }
    
    public void setName(String name) { this.name = name;}
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public double getSatisfactionScore() {
        return satisfactionScore;
    }
    
    public void setSatisfactionScore(double satisfactionScore) {
        this.satisfactionScore = satisfactionScore;
    }

}