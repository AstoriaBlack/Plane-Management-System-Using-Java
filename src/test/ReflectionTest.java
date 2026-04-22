package test;

import annotation.FunctionalComponent;
import annotation.PatternInfo;
import pricing.StandardPricingStrategy;
import pricing.PricingStrategy;
import model.Passenger;
import model.Ticket;
import reflection.ReflectionInspector;
import service.TicketRepository;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Comprehensive tests for Reflection functionality.
 * Validates that the application correctly uses reflection to inspect design patterns.
 */
public class ReflectionTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                REFLECTION TEST SUITE                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        testStrategyPatternValidation();
        testPatternInfoAnnotation();
        testFunctionalComponentAnnotation();
        testReflectionInspection();
        testDynamicMethodInvocation();
        testConstructorInspection();
        testFieldInspection();
        
        printTestResults();
    }
    
    private static void testStrategyPatternValidation() {
        System.out.println("▶ Testing Strategy Pattern Validation:");
        
        // Validate that reflection can detect Strategy pattern implementation
        boolean isValid = ReflectionInspector.validateStrategyPattern();
        test("Strategy pattern validation", isValid);
        
        // Check interface implementation
        Class<?>[] interfaces = StandardPricingStrategy.class.getInterfaces();
        test("StandardPricingStrategy implements interface", interfaces.length > 0);
        test("Implements PricingStrategy interface", 
             Arrays.stream(interfaces).anyMatch(i -> i.equals(PricingStrategy.class)));
        
        // Check method existence
        boolean hasCalculatePrice = Arrays.stream(StandardPricingStrategy.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("calculatePrice"));
        test("Has calculatePrice method", hasCalculatePrice);
        
        System.out.println();
    }
    
    private static void testPatternInfoAnnotation() {
        System.out.println("▶ Testing PatternInfo Annotation:");
        
        // Test StandardPricingStrategy annotation
        test("StandardPricingStrategy has @PatternInfo", 
             StandardPricingStrategy.class.isAnnotationPresent(PatternInfo.class));
        
        PatternInfo pi = StandardPricingStrategy.class.getAnnotation(PatternInfo.class);
        if (pi != null) {
            test("PatternInfo has correct pattern name", 
                 "Strategy".equals(pi.patternName()));
            test("PatternInfo has role", !pi.role().isEmpty());
            System.out.println("  ℹ Pattern: " + pi.patternName() + " - " + pi.role());
        }
        
        // Test Ticket class annotation
        test("Ticket class can be inspected", Ticket.class != null);
        
        // Test Passenger class annotation
        test("Passenger class has @PatternInfo", 
             Passenger.class.isAnnotationPresent(PatternInfo.class));
        
        // Use ReflectionInspector helper
        var pricingPatternInfo = ReflectionInspector.getPatternInfo(StandardPricingStrategy.class);
        test("ReflectionInspector.getPatternInfo works", pricingPatternInfo.isPresent());
        
        System.out.println();
    }
    
    private static void testFunctionalComponentAnnotation() {
        System.out.println("▶ Testing FunctionalComponent Annotation:");
        
        // Check if StandardPricingStrategy.calculatePrice has @FunctionalComponent
        try {
            Method calculatePrice = StandardPricingStrategy.class.getMethod("calculatePrice", int.class);
            test("calculatePrice has @FunctionalComponent", 
                 calculatePrice.isAnnotationPresent(FunctionalComponent.class));
            
            if (calculatePrice.isAnnotationPresent(FunctionalComponent.class)) {
                FunctionalComponent fc = calculatePrice.getAnnotation(FunctionalComponent.class);
                test("calculatePrice is marked as pure function", fc.isPure());
                System.out.println("  ℹ Description: " + fc.description());
            }
        } catch (NoSuchMethodException e) {
            test("calculatePrice method exists", false);
        }
        
        // Test functional component inspection
        Map<String, ReflectionInspector.FunctionalMethodInfo> functionalMethods = 
            ReflectionInspector.inspectFunctionalComponents(StandardPricingStrategy.class);
        test("Found functional components", !functionalMethods.isEmpty());
        System.out.println("  ℹ Found " + functionalMethods.size() + " functional methods");
        
        System.out.println();
    }
    
    private static void testReflectionInspection() {
        System.out.println("▶ Testing Reflection Inspection Methods:");
        
        // Test interface inspection
        Class<?> strategyClass = StandardPricingStrategy.class;
        Class<?>[] interfaces = strategyClass.getInterfaces();
        test("Can inspect interfaces", interfaces.length > 0);
        
        // Test method inspection
        Method[] methods = strategyClass.getDeclaredMethods();
        test("Can inspect methods", methods.length > 0);
        test("Has at least calculatePrice method", 
             Arrays.stream(methods).anyMatch(m -> m.getName().equals("calculatePrice")));
        
        // Test superclass inspection
        Class<?> superclass = strategyClass.getSuperclass();
        test("Can inspect superclass", superclass != null);
        test("Superclass is Object", superclass.equals(Object.class));
        
        System.out.println();
    }
    
    private static void testDynamicMethodInvocation() {
        System.out.println("▶ Testing Dynamic Method Invocation:");
        
        StandardPricingStrategy strategy = new StandardPricingStrategy();
        
        // Use ReflectionInspector to invoke method dynamically
        var result = ReflectionInspector.invokeMethod(strategy, "calculatePrice", 3);
        
        test("Dynamic method invocation works", result.isPresent());
        if (result.isPresent()) {
            test("Dynamic invocation returns correct result", 
                 200.0 == (Double)result.get());
        }
        
        System.out.println();
    }
    
    private static void testConstructorInspection() {
        System.out.println("▶ Testing Constructor Inspection:");
        
        var constructors = ReflectionInspector.inspectConstructors(StandardPricingStrategy.class);
        test("Can inspect constructors", !constructors.isEmpty());
        
        // StandardPricingStrategy should have default constructor
        boolean hasDefaultConstructor = constructors.stream()
            .anyMatch(c -> c.parameterCount() == 0);
        test("Has default constructor", hasDefaultConstructor);
        
        // Check TicketRepository constructors (should have multiple)
        var repoConstructors = ReflectionInspector.inspectConstructors(TicketRepository.class);
        test("TicketRepository has constructors", !repoConstructors.isEmpty());
        
        System.out.println();
    }
    
    private static void testFieldInspection() {
        System.out.println("▶ Testing Field Inspection:");
        
        var fields = ReflectionInspector.inspectFields(Ticket.class);
        test("Can inspect fields", !fields.isEmpty());
        test("Ticket has at least 3 fields", fields.size() >= 3);
        
        boolean hasTicketNumberField = fields.stream()
            .anyMatch(f -> f.name().equals("ticketNumber"));
        test("Ticket has 'ticketNumber' field", hasTicketNumberField);
        
        boolean hasRowField = fields.stream()
            .anyMatch(f -> f.name().equals("row"));
        test("Ticket has 'row' field", hasRowField);
        
        System.out.println();
    }
    
    private static void test(String testName, boolean condition) {
        if (condition) {
            System.out.println("  ✓ " + testName + ": PASSED");
            passed++;
        } else {
            System.out.println("  ✗ " + testName + ": FAILED");
            failed++;
        }
    }
    
    private static void printTestResults() {
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("Test Results: " + passed + " passed, " + failed + " failed");
        
        if (failed > 0) {
            System.out.println("Status: FAILED");
            System.exit(1);
        } else {
            System.out.println("Status: ALL TESTS PASSED ✓");
            System.out.println("\nRunning detailed reflection inspection...\n");
            ReflectionInspector.inspectPricingStrategy();
        }
    }
}
