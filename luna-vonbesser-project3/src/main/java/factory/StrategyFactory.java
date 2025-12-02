package factory;

import business.strategies.CreditStrategyInterface;
import business.strategies.MaintainerCreditStrategy;
import business.strategies.SponsorCreditStrategy;

public class StrategyFactory {

    public static CreditStrategyInterface createCreditStrategy(String userType) {
        switch (userType.toUpperCase()) {
            case "SPONSOR" -> {
                return new SponsorCreditStrategy();
            }
            case "MAINTAINER" -> {
                return new MaintainerCreditStrategy();
            }
            default -> throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }
}
 
