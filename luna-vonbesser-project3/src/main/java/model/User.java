package model;

import strategy.CreditStrategyInterface;
import java.sql.Timestamp;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String userType;  // USER, SPONSOR, MAINTAINER
    private double creditsBalance;
    private Timestamp createdAt;
    private CreditStrategyInterface creditStrategy;

    // private constructor for Builder
    private User(UserBuilder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.password = builder.password;
        this.name = builder.name;
        this.userType = builder.userType;
        this.creditsBalance = builder.creditsBalance;
        this.createdAt = builder.createdAt;
        this.creditStrategy = builder.creditStrategy;
    }

    // Calculate credits using the assigned strategy
    public double calculateEarnedCredits() {
        return creditStrategy != null ? creditStrategy.calculateCredit(this) : 0.0;
    }

    // getters and Setters (only showing key ones)
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getUserType() { return userType; }
    public double getCreditsBalance() { return creditsBalance; }
    public void setCreditsBalance(double balance) { this.creditsBalance = balance; }
    public void setCreditStrategy(CreditStrategyInterface strategy) { this.creditStrategy = strategy; }

    // builder Pattern
    public static class UserBuilder {
        private int id;
        private String email;
        private String password;
        private String name;
        private String userType = "USER";
        private double creditsBalance = 0.0;
        private Timestamp createdAt;
        private CreditStrategyInterface creditStrategy;

        public UserBuilder(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }

        public UserBuilder id(int id) { this.id = id; return this; }
        public UserBuilder userType(String type) { this.userType = type; return this; }
        public UserBuilder creditsBalance(double balance) { this.creditsBalance = balance; return this; }
        public UserBuilder createdAt(Timestamp time) { this.createdAt = time; return this; }
        public UserBuilder creditStrategy(CreditStrategyInterface strategy) { this.creditStrategy = strategy; return this; }

        public User build() {
            return new User(this);
        }
    }

    // additional helper methods
    public boolean isSponsor() { return "SPONSOR".equals(userType); }
    public boolean isMaintainer() { return "MAINTAINER".equals(userType); }
}
