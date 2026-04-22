package test;

import config.Constants;
import pricing.StandardPricingStrategy;
import pricing.DynamicPricingStrategy;
import pricing.PricingStrategyFactory;
import pricing.PricingStrategy;

/**
 * Unit tests for PricingStrategy implementations.
 * Tests both StandardPricingStrategy and DynamicPricingStrategy.
 */
public class PricingStrategyTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              PRICING STRATEGY TEST SUITE                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        testStandardPricingStrategy();
        testDynamicPricingStrategy();
        testPricingStrategyFactory();
        testBoundaryValues();
        
        printTestResults();
    }
    
    private static void testStandardPricingStrategy() {
        System.out.println("▶ Testing StandardPricingStrategy:");
        
        StandardPricingStrategy strategy = new StandardPricingStrategy();
        
        // Test First Class (seats 1-5)
        test("First Class seat 1", 
             strategy.calculatePrice(1) == Constants.FIRST_CLASS_PRICE);
        test("First Class seat 5", 
             strategy.calculatePrice(5) == Constants.FIRST_CLASS_PRICE);
        
        // Test Business Class (seats 6-9)
        test("Business seat 6", 
             strategy.calculatePrice(6) == Constants.BUSINESS_CLASS_PRICE);
        test("Business seat 9", 
             strategy.calculatePrice(9) == Constants.BUSINESS_CLASS_PRICE);
        
        // Test Economy Class (seats 10+)
        test("Economy seat 10", 
             strategy.calculatePrice(10) == Constants.ECONOMY_CLASS_PRICE);
        test("Economy seat 14", 
             strategy.calculatePrice(14) == Constants.ECONOMY_CLASS_PRICE);
        
        // Test class determination
        test("First Class label", 
             Constants.CLASS_FIRST.equals(strategy.getTicketClass(3)));
        test("Business Class label", 
             Constants.CLASS_BUSINESS.equals(strategy.getTicketClass(7)));
        test("Economy Class label", 
             Constants.CLASS_ECONOMY.equals(strategy.getTicketClass(12)));
        
        // Test isPremium method
        test("isPremium First Class (3)", strategy.isPremium(3));
        test("isPremium Business (8)", strategy.isPremium(8));
        test("Not premium Economy (12)", !strategy.isPremium(12));
        
        System.out.println();
    }
    
    private static void testDynamicPricingStrategy() {
        System.out.println("▶ Testing DynamicPricingStrategy:");
        
        DynamicPricingStrategy strategy = new DynamicPricingStrategy(1.5);
        
        test("Dynamic First Class with 1.5x", 
             strategy.calculatePrice(1) == 300.0);
        test("Dynamic Business with 1.5x", 
             strategy.calculatePrice(6) == 225.0);
        test("Dynamic Economy with 1.5x", 
             strategy.calculatePrice(10) == 270.0);
        
        DynamicPricingStrategy defaultStrategy = new DynamicPricingStrategy();
        test("Default multiplier (1.0)", 
             defaultStrategy.calculatePrice(1) == 200.0);
        
        System.out.println();
    }
    
    private static void testPricingStrategyFactory() {
        System.out.println("▶ Testing PricingStrategyFactory:");
        
        PricingStrategy standard = PricingStrategyFactory.createDefaultStrategy();
        test("Default strategy is StandardPricingStrategy", 
             standard instanceof StandardPricingStrategy);
        
        PricingStrategy standardExplicit = PricingStrategyFactory.createStrategy(
            PricingStrategyFactory.StrategyType.STANDARD);
        test("STANDARD strategy type", 
             standardExplicit instanceof StandardPricingStrategy);
        
        PricingStrategy dynamic = PricingStrategyFactory.createStrategy(
            PricingStrategyFactory.StrategyType.DYNAMIC);
        test("DYNAMIC strategy type", 
             dynamic instanceof DynamicPricingStrategy);
        
        PricingStrategy customDynamic = PricingStrategyFactory.createDynamicStrategy(2.0);
        test("Custom dynamic strategy", 
             customDynamic.calculatePrice(1) == 400.0);
        
        System.out.println();
    }
    
    private static void testBoundaryValues() {
        System.out.println("▶ Testing Boundary Values:");
        
        StandardPricingStrategy strategy = new StandardPricingStrategy();
        
        // Boundary between First and Business
        test("Boundary: seat 5 (First)", 
             strategy.calculatePrice(5) == Constants.FIRST_CLASS_PRICE);
        test("Boundary: seat 6 (Business)", 
             strategy.calculatePrice(6) == Constants.BUSINESS_CLASS_PRICE);
        
        // Boundary between Business and Economy
        test("Boundary: seat 9 (Business)", 
             strategy.calculatePrice(9) == Constants.BUSINESS_CLASS_PRICE);
        test("Boundary: seat 10 (Economy)", 
             strategy.calculatePrice(10) == Constants.ECONOMY_CLASS_PRICE);
        
        // Extreme values
        test("Seat 1 (First)", 
             strategy.calculatePrice(1) == Constants.FIRST_CLASS_PRICE);
        test("High seat number (Economy)", 
             strategy.calculatePrice(100) == Constants.ECONOMY_CLASS_PRICE);
        
        System.out.println();
    }
    
    private static void test(String testName, boolean condition) {
        if (condition) {
            System.out.println("  ✓ PASS: " + testName);
            passed++;
        } else {
            System.out.println("  ✗ FAIL: " + testName);
            failed++;
        }
    }
    
    private static void printTestResults() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                      TEST RESULTS                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Tests:   %-42d  ║%n", passed + failed);
        System.out.printf("║  Passed:        %-42d  ║%n", passed);
        System.out.printf("║  Failed:        %-42d  ║%n", failed);
        System.out.printf("║  Success Rate:  %.1f%%                                    ║%n", 
                         (passed * 100.0 / (passed + failed)));
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        if (failed == 0) {
            System.out.println("\n🎉 All tests passed! Pricing strategies working correctly.");
        } else {
            System.out.println("\n⚠ Some tests failed. Please review the pricing logic.");
        }
    }
}
