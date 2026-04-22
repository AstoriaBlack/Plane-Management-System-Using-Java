package pricing;

import annotation.FunctionalComponent;
import annotation.PatternInfo;
import config.Constants;

/**
 * Standard airplane ticket pricing strategy implementation.
 * Implements the Strategy pattern as a Concrete Strategy.
 * Price ranges based on seat number:
 * - First Class (seats 1-5): £200
 * - Business (seats 6-9): £150
 * - Economy (seats 10+): £180
 */
@PatternInfo(
    patternName = "Strategy",
    role = "Concrete Strategy",
    description = "Implements standard airplane pricing based on seat classes"
)
public class StandardPricingStrategy implements PricingStrategy {

    /**
     * Calculates ticket price using standard seat-based pricing.
     * Pure function - deterministic with no side effects.
    param seatNumber The seat number (1-based)
    return Price in pounds
     */
    @Override
    @FunctionalComponent(
        isPure = true, 
        description = "Pure function mapping seat number to price using pattern matching style"
    )
    public double calculatePrice(int seatNumber) {
        // Functional approach using early returns (pattern-matching style)
        if (seatNumber >= 1 && seatNumber <= 5) return Constants.FIRST_CLASS_PRICE;
        if (seatNumber >= 6 && seatNumber <= 9) return Constants.BUSINESS_CLASS_PRICE;
        return Constants.ECONOMY_CLASS_PRICE;
    }
    
    /**
     * Determines ticket class based on seat number.
    param seatNumber The seat number
    return Ticket class name
     */
    @Override
    @FunctionalComponent(isPure = true, description = "Pure function for class determination")
    public String getTicketClass(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 5) return Constants.CLASS_FIRST;
        if (seatNumber >= 6 && seatNumber <= 9) return Constants.CLASS_BUSINESS;
        return Constants.CLASS_ECONOMY;
    }
    
    /**
     * Checks if a seat is in premium class.
     * Pure function for functional composition.

    param seatNumber The seat number
    return true if premium (First or Business), false otherwise
     */
    @FunctionalComponent(isPure = true, description = "Predicate function for premium class determination")
    public boolean isPremium(int seatNumber) {
        return seatNumber >= 1 && seatNumber <= 9;
    }
}
