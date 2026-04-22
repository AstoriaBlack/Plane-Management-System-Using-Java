package pricing;

import annotation.FunctionalComponent;
import annotation.PatternInfo;

/**
 * Alternative dynamic pricing strategy with seasonal adjustments.
 * Demonstrates the Strategy pattern extensibility - new strategies
 * can be added without modifying existing code (Open/Closed Principle).
 */
@PatternInfo(
    patternName = "Strategy",
    role = "Concrete Strategy",
    description = "Dynamic pricing system with multipliers"
)
public class DynamicPricingStrategy implements PricingStrategy {

    private final double multiplier;

    /**
     * Creates a dynamic pricing strategy with a custom multiplier.
    
     * @param multiplier Price adjustment multiplier (e.g., 1.5 for 50% increase)
     */
    public DynamicPricingStrategy(double multiplier) {
        this.multiplier = multiplier;
    }
    
    /**
     * Default constructor with no multiplier.
     */
    public DynamicPricingStrategy() {
        this(1.0);
    }

    @Override
    @FunctionalComponent(
        isPure = true, 
        description = "Pure function mapping seat to dynamic price"
    )
    public double calculatePrice(int seatNumber) {
        double basePrice;
        if (seatNumber >= 1 && seatNumber <= 5) basePrice = 200;
        else if (seatNumber >= 6 && seatNumber <= 9) basePrice = 150;
        else basePrice = 180;
        
        return basePrice * multiplier;
    }
    
    @Override
    @FunctionalComponent(isPure = true, description = "Pure function for class determination")
    public String getTicketClass(int seatNumber) {
        if (seatNumber >= 1 && seatNumber <= 5) return "First Class";
        if (seatNumber >= 6 && seatNumber <= 9) return "Business";
        return "Economy";
    }
}
