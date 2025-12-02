package strategy;

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
 
