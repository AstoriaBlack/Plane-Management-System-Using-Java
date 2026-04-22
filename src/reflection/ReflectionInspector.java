package reflection;

import annotation.FunctionalComponent;
import annotation.PatternInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import model.Passenger;
import model.Ticket;
import pricing.PricingStrategy;
import pricing.StandardPricingStrategy;
import service.TicketRepository;
import service.TicketService;


/**
 * Comprehensive reflection utility for inspecting the application's design patterns
 * and structure at runtime.
 * This class demonstrates advanced Java Reflection API usage:
 * - Class inspection (Class.forName, getInterfaces, getSuperclass)
 * - Method inspection (getDeclaredMethods, getModifiers, getParameterTypes)
 * - Annotation inspection (getAnnotations, isAnnotationPresent)
 * - Constructor inspection (getConstructors, getParameters)
 * - Field inspection (getDeclaredFields)
 * Functional programming features:
 * - Streams for processing reflection results
 * - Optional for null-safe operations
 * - Method references and lambdas
 */
@PatternInfo(
    patternName = "Utility Class",
    role = "Reflection Inspector",
    description = "Provides comprehensive reflection capabilities for design pattern analysis"
)
public class ReflectionInspector {

    /**
     * Inspects the PricingStrategy implementation and displays pattern analysis.
     */
    public static void inspectPricingStrategy() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        REFLECTION INSPECTION: Design Pattern Analysis       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        Class<?> clazz = StandardPricingStrategy.class;
        
        // Basic class information
        System.out.println("\n▶ Class Information:");
        System.out.println("  Name: " + clazz.getSimpleName());
        System.out.println("  Full Name: " + clazz.getName());
        System.out.println("  Package: " + clazz.getPackage().getName());
        System.out.println("  Superclass: " + clazz.getSuperclass().getSimpleName());
        
        // Inspect interfaces (Strategy Pattern evidence)
        Class<?>[] interfaces = clazz.getInterfaces();
        System.out.println("\n▶ Implemented Interfaces (Strategy Pattern):");
        Arrays.stream(interfaces)
              .forEach(iface -> System.out.println("  • " + iface.getSimpleName()));
        
        if (interfaces.length > 0) {
            Class<?> strategyInterface = interfaces[0];
            inspectInterface(strategyInterface);
        }
        
        // Inspect class methods
        System.out.println("\n▶ Declared Methods:");
        Arrays.stream(clazz.getDeclaredMethods())
              .forEach(ReflectionInspector::printMethodDetails);
        
        // Inspect annotations
        inspectAnnotations(clazz);
        
        System.out.println("\n════════════════════════════════════════════════════════════");
    }
    
    /**
     * Inspects an interface's declared methods.

    param iface The interface to inspect
     */
    private static void inspectInterface(Class<?> iface) {
        System.out.println("\n▶ Strategy Interface Methods (" + iface.getSimpleName() + "):");
        Arrays.stream(iface.getDeclaredMethods())
              .forEach(method -> {
                  String params = Arrays.stream(method.getParameterTypes())
                                       .map(Class::getSimpleName)
                                       .collect(Collectors.joining(", "));
                  System.out.printf("  • %s(%s): %s%n", 
                                  method.getName(), params, method.getReturnType().getSimpleName());
              });
    }
    
    /**
     * Prints detailed method information using reflection.

    param method The method to inspect
     */
    private static void printMethodDetails(Method method) {
        String modifiers = Modifier.toString(method.getModifiers());
        String params = Arrays.stream(method.getParameterTypes())
                             .map(Class::getSimpleName)
                             .collect(Collectors.joining(", "));
        
        System.out.printf("  • %s %s %s(%s)%n",
                         modifiers,
                         method.getReturnType().getSimpleName(),
                         method.getName(),
                         params);
        
        // Check for FunctionalComponent annotation
        if (method.isAnnotationPresent(FunctionalComponent.class)) {
            FunctionalComponent fc = method.getAnnotation(FunctionalComponent.class);
            System.out.printf("    @FunctionalComponent(isPure=%b, description=\"%s\")%n",
                            fc.isPure(), fc.description());
        }
    }
    
    /**
     * Inspects annotations on a class.
    param clazz The class to inspect for annotations
     */
    private static void inspectAnnotations(Class<?> clazz) {
        System.out.println("\n▶ Class Annotations:");
        Annotation[] annotations = clazz.getAnnotations();
        
        if (annotations.length == 0) {
            System.out.println("  (none)");
            return;
        }
        
        Arrays.stream(annotations)
              .forEach(annotation -> {
                  if (annotation instanceof PatternInfo pi) {
                      System.out.println("  @PatternInfo:");
                      System.out.println("    Pattern: " + pi.patternName());
                      System.out.println("    Role: " + pi.role());
                      System.out.println("    Description: " + pi.description());
                  } else {
                      System.out.println("  @" + annotation.annotationType().getSimpleName());
                  }
              });
    }
    
    /**
     * Validates that the Strategy pattern is correctly implemented.
     * Uses reflection to verify:
     * 1. StandardPricingStrategy implements PricingStrategy interface
     * 2. calculatePrice method exists and has correct signature
     return true if Strategy pattern is valid
     */
    @FunctionalComponent(isPure = true, description = "Pure validation using reflection")
    public static boolean validateStrategyPattern() {
        Class<?> clazz = StandardPricingStrategy.class;
        Class<?>[] interfaces = clazz.getInterfaces();
        
        // Validate that StandardPricingStrategy implements PricingStrategy interface
        boolean implementsInterface = Arrays.stream(interfaces)
                .anyMatch(iface -> iface.equals(PricingStrategy.class));
        
        // Validate that calculatePrice method exists with correct signature
        boolean hasCalculatePrice = Arrays.stream(clazz.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("calculatePrice") 
                          && m.getParameterCount() == 1
                          && m.getParameterTypes()[0] == int.class
                          && m.getReturnType() == double.class);
        
        return implementsInterface && hasCalculatePrice;
    }
    
    /**
     * Inspects a class for functional programming annotations.
     * Returns a map of method names to their functional characteristics.
    param clazz The class to inspect
    return Map of method names to functional info
     */
    @FunctionalComponent(isPure = true, description = "Functional inspection using streams")
    public static Map<String, FunctionalMethodInfo> inspectFunctionalComponents(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(FunctionalComponent.class))
                .collect(Collectors.toMap(
                    Method::getName,
                    m -> {
                        FunctionalComponent fc = m.getAnnotation(FunctionalComponent.class);
                        return new FunctionalMethodInfo(m.getName(), fc.isPure(), fc.description());
                    }
                ));
    }
    
    /**
     * Inspects a class for PatternInfo annotation.

    param clazz The class to inspect
    return Optional containing PatternInfo if present
     */
    @FunctionalComponent(isPure = true, description = "Returns Optional for null-safety")
    public static Optional<PatternInfo> getPatternInfo(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(PatternInfo.class));
    }
    
    /**
     * Gets all classes with PatternInfo annotation from a list of classes.
     * Demonstrates functional approach to reflection.

    param classes Array of classes to inspect
    return List of classes annotated with PatternInfo
     */
    @FunctionalComponent(isPure = true, description = "Filters classes using stream and annotation check")
    public static List<Class<?>> getAnnotatedClasses(Class<?>... classes) {
        return Arrays.stream(classes)
                .filter(c -> c.isAnnotationPresent(PatternInfo.class))
                .collect(Collectors.toList());
    }
    
    /**
     * Inspects constructor parameters for dependency injection analysis.
    param clazz The class to inspect
    return List of constructor parameter information
     */
    @FunctionalComponent(isPure = true, description = "Analyzes constructors for DI patterns")
    public static List<ConstructorInfo> inspectConstructors(Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors())
                .map(c -> new ConstructorInfo(
                    c.getParameterCount(),
                    Arrays.stream(c.getParameterTypes())
                          .map(Class::getSimpleName)
                          .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Dynamically invokes a method using reflection.
    param obj The object instance
    param methodName Name of the method to invoke
    param args Method arguments
    return Result of the method invocation
     */
    public static Optional<Object> invokeMethod(Object obj, String methodName, Object... args) {
        try {
            // Handle primitive types properly
            Class<?>[] argTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = getPrimitiveType(args[i].getClass());
            }
            
            Method method = obj.getClass().getMethod(methodName, argTypes);
            return Optional.ofNullable(method.invoke(obj, args));
        } catch (Exception e) {
            System.err.println("Reflection error: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Converts wrapper classes to their primitive types.
     */
    private static Class<?> getPrimitiveType(Class<?> wrapperType) {
        if (wrapperType == Integer.class) return int.class;
        if (wrapperType == Long.class) return long.class;
        if (wrapperType == Double.class) return double.class;
        if (wrapperType == Float.class) return float.class;
        if (wrapperType == Boolean.class) return boolean.class;
        if (wrapperType == Character.class) return char.class;
        if (wrapperType == Byte.class) return byte.class;
        if (wrapperType == Short.class) return short.class;
        return wrapperType;
    }
    
    /**
     * Creates an instance of a class dynamically.
    param className Fully qualified class name
    return Optional containing the created instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> createInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return Optional.of((T) constructor.newInstance());
        } catch (Exception e) {
            System.err.println("Failed to create instance: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Inspects all fields of a class.
    param clazz The class to inspect
    return List of field information
     */
    @FunctionalComponent(isPure = true, description = "Inspects fields using reflection")
    public static List<FieldInfo> inspectFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(f -> new FieldInfo(
                    f.getName(),
                    f.getType().getSimpleName(),
                    Modifier.toString(f.getModifiers())
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Performs comprehensive application structure analysis.
     */
    public static void performFullInspection() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           COMPREHENSIVE APPLICATION INSPECTION              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        // Classes to inspect
        Class<?>[] classes = {
            StandardPricingStrategy.class,
            Passenger.class,
            Ticket.class,
            TicketRepository.class,
            TicketService.class
        };
        
        for (Class<?> clazz : classes) {
            System.out.println("\n┌── " + clazz.getSimpleName() + " ──");
            
            // Pattern info
            getPatternInfo(clazz).ifPresent(pi -> {
                System.out.println("│ Pattern: " + pi.patternName() + " (" + pi.role() + ")");
            });
            
            // Field count
            System.out.println("│ Fields: " + clazz.getDeclaredFields().length);
            System.out.println("│ Methods: " + clazz.getDeclaredMethods().length);
            System.out.println("│ Constructors: " + clazz.getConstructors().length);
            
            // Functional methods count
            long functionalMethods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(FunctionalComponent.class))
                .count();
            System.out.println("│ @FunctionalComponent methods: " + functionalMethods);
            
            System.out.println("└────────────────────────────");
        }
    }

    // Inner classes for structured reflection data
    
    /**
     * Record for functional method information.
     */
    public record FunctionalMethodInfo(String name, boolean isPure, String description) {}
    
    /**
     * Record for constructor information.
     */
    public record ConstructorInfo(int parameterCount, List<String> parameterTypes) {}
    
    /**
     * Record for field information.
     */
    public record FieldInfo(String name, String type, String modifiers) {}
}
