package pricing;

import annotation.FunctionalComponent;
import annotation.PatternInfo;

/**
 * Strategy interface for ticket price calculation.
 * Part of the Strategy Design Pattern - allows different pricing algorithms
 * to be used interchangeably at runtime.

 * Design Pattern: Strategy
 * - Defines a family of algorithms (pricing strategies)
 * - Encapsulates each algorithm
 * - Makes the algorithms interchangeable within the same family
 */
@PatternInfo(
    patternName = "Strategy",
    role = "Strategy Interface",
    description = "Defines the contract for pricing algorithms, allowing different implementations to be swapped at runtime"
)
public interface PricingStrategy {
    
    /**
     * Calculates the ticket price based on the seat number.
     * This is a pure function - same input always produces the same output.
    param seatNumber The seat number to evaluate
    return The calculated price
     */
    @FunctionalComponent(isPure = true, description = "Pure function: maps seat number to price")
    double calculatePrice(int seatNumber);
    
    /**
     * Determines the ticket class based on seat number.
    param seatNumber The seat number to evaluate
    return The ticket class (Economy, Business, First Class)
     */
    @FunctionalComponent(isPure = true, description = "Pure function: maps seat number to ticket class")
    String getTicketClass(int seatNumber);
    
    /**
     * Returns the name of this pricing strategy.
     * Used for reflection and logging purposes.
    
    return The strategy name
     */
    default String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}
