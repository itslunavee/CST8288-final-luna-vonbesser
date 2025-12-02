package strategy;

import model.User;

// interface for different credit calculation strategies
// each user type has its own way of earning credits
public interface CreditStrategyInterface {

    double calculateCredit(User user);

    String getStrategyName();
}
