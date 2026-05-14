package Tfast_Rmoney.Givvy.interfaces.dtos;

import java.time.LocalDateTime;

import Tfast_Rmoney.Givvy.entities.User;

public class UserResponse {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private double satisfactionScore;

    public UserResponse(User user) {
        this.userId = user.getUserId().toString();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.createdAt = user.getCreatedAt();
        this.satisfactionScore = user.getSatisfactionScore();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

	public String getEmail() {
		return email;
	}

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getSatisfactionScore() {
        return satisfactionScore;
    }
}
