package pricing;

import annotation.PatternInfo;

/**
 * Factory for creating PricingStrategy instances.
 * Implements the Factory Method pattern to decouple strategy creation
 * from the client code.

 * Benefits:
 * - Centralizes object creation
 * - Easy to add new strategies
 * - Follows Open/Closed Principle
 */
@PatternInfo(
    patternName = "Factory Method",
    role = "Creator",
    description = "Creates PricingStrategy instances based on type parameter"
)
public class PricingStrategyFactory {
    
    /**
     * Enumeration of available pricing strategy types.
     */
    public enum StrategyType {
        STANDARD,
        DYNAMIC
    }
    
    /**
     * Creates a PricingStrategy based on the specified type.
    param type The type of strategy to create
    return A new PricingStrategy instance
     */
    public static PricingStrategy createStrategy(StrategyType type) {
        return switch (type) {
            case STANDARD -> new StandardPricingStrategy();
            case DYNAMIC -> new DynamicPricingStrategy();
        };
    }
    
    /**
     * Creates a dynamic pricing strategy with custom multiplier.

    param multiplier Price adjustment multiplier
    return A new DynamicPricingStrategy instance
     */
    public static PricingStrategy createDynamicStrategy(double multiplier) {
        return new DynamicPricingStrategy(multiplier);
    }
    
    /**
     * Creates the default pricing strategy (Standard).
    return A StandardPricingStrategy instance
     */
    public static PricingStrategy createDefaultStrategy() {
        return createStrategy(StrategyType.STANDARD);
    }
}
