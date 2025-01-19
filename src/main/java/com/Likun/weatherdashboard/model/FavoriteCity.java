package com.Likun.weatherdashboard.model;

// FavoriteCity.java


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "favorite_city")

public class FavoriteCity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_name", nullable = false)  // Use 'city_name' to match the database column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // Many favorites can belong to one user
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User table
    @JsonIgnore // Prevent infinite recursion
    private User user;

    // Constructors
    public FavoriteCity() {
    }

    public FavoriteCity(String name, User user) {
        this.name = name;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}