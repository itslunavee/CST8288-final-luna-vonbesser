package model;

import java.sql.Timestamp;

// this is our main user model class it holds all the user data
public class User {

    private int id;
    private String email;
    private String password;
    private String name;
    private String userType;  // can be USER, SPONSOR, or MAINTAINER
    private double creditsBalance;
    private Timestamp createdAt;

    // private constructor so you have to use the builder
    private User(UserBuilder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.password = builder.password;
        this.name = builder.name;
        this.userType = builder.userType;
        this.creditsBalance = builder.creditsBalance;
        this.createdAt = builder.createdAt;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }

    public double getCreditsBalance() {
        return creditsBalance;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // setters 
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setCreditsBalance(double creditsBalance) {
        this.creditsBalance = creditsBalance;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // this is the builder class
    public static class UserBuilder {

        private int id;
        private String email;
        private String password;
        private String name;
        private String userType = "USER";  // default to regular user
        private double creditsBalance = 0.0;
        private Timestamp createdAt;

        // constructor with required fields
        public UserBuilder(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }

        // optional field setters
        public UserBuilder id(int id) {
            this.id = id;
            return this;
        }

        public UserBuilder userType(String userType) {
            this.userType = userType;
            return this;
        }

        public UserBuilder creditsBalance(double creditsBalance) {
            this.creditsBalance = creditsBalance;
            return this;
        }

        public UserBuilder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        // this creates the User object when we're done building
        public User build() {
            return new User(this);
        }
    }

    //method to display user info
    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", email='" + email + '\''
                + ", name='" + name + '\''
                + ", userType='" + userType + '\''
                + ", creditsBalance=" + creditsBalance
                + '}';
    }
}
