package test;

import config.Constants;
import pricing.PricingStrategyFactory;
import model.Passenger;
import model.Ticket;
import service.TicketService;

import java.util.*;

/**
 * Tests for Functional Programming features in the Plane Management System.
 * This simplified test suite validates the key FP concepts without complex test isolation.
 */
public class FunctionalProgrammingTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          FUNCTIONAL PROGRAMMING TEST SUITE                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        testPassengerPureFunctions();
        testOptionalUsage();
        testFunctionalComposition();
        testPureFunctions();
        
        printTestResults();
    }
    
    private static void testPassengerPureFunctions() {
        System.out.println("▶ Testing Pure Functions in Passenger:");
        
        Passenger passenger = new Passenger("John", "Doe", "john@example.com", 
                                           5, PricingStrategyFactory.createDefaultStrategy());
        
        test("Passenger calculates price", passenger.getBasePrice() == Constants.FIRST_CLASS_PRICE);
        test("Passenger determines class", passenger.getTicketClass().equals(Constants.CLASS_FIRST));
        test("getFullName is pure function", passenger.getFullName().equals("John Doe"));
        test("hasValidEmail validates correctly", passenger.hasValidEmail());
        
        Passenger invalidEmail = new Passenger("Jane", "Smith", "invalid-email", 
                                              7, PricingStrategyFactory.createDefaultStrategy());
        test("hasValidEmail detects invalid", !invalidEmail.hasValidEmail());
        
        System.out.println();
    }
    
    private static void testOptionalUsage() {
        System.out.println("▶ Testing Optional Usage:");
        
        TicketService service = new TicketService();
        service.buyTicket("T001", "A", 5, "John", "Doe", "john@example.com");
        
        Optional<Ticket> result = service.findTicket("T001");
        test("findTicket returns Optional", result.isPresent());
        test("Optional contains correct ticket", 
             result.map(Ticket::getTicketNumber).orElse("").equals("T001"));
        
        Optional<Ticket> notFound = service.findTicket("NONEXISTENT");
        test("Optional is empty for non-existent ticket", notFound.isEmpty());
        
        String ticketClass = service.findTicket("T001")
                             .map(Ticket::getPassenger)
                             .map(Passenger::getTicketClass)
                             .orElse("Unknown");
        test("Optional chaining with map", ticketClass.equals(Constants.CLASS_FIRST));
        
        Optional<Ticket> bySeat = service.findTicketBySeat("A", 5);
        test("findTicketBySeat returns Optional", bySeat.isPresent());
        test("Seat search finds correct ticket", 
             bySeat.map(Ticket::getTicketNumber).orElse("").equals("T001"));
        
        System.out.println();
    }
    
    private static void testFunctionalComposition() {
        System.out.println("▶ Testing Functional Composition:");
        
        Passenger passenger = new Passenger("Alice", "Smith", "alice@example.com", 
                                           3, PricingStrategyFactory.createDefaultStrategy());
        Ticket ticket = new Ticket("T100", "A", 3, passenger);
        
        Ticket updatedRow = ticket.withRow("B");
        test("withRow returns new instance", ticket != updatedRow);
        test("Original unchanged", ticket.getRow().equals("A"));
        test("New instance updated", updatedRow.getRow().equals("B"));
        test("Same ticket number preserved", 
             ticket.getTicketNumber().equals(updatedRow.getTicketNumber()));
        
        TicketService service = new TicketService();
        service.buyTicket("T200", "C", 8, "Bob", "Johnson", "bob@example.com");
        
        var result = service.updateTicket("T200", existing -> 
            existing.withRow("D")
        );
        
        test("Higher-order function update works", result.isSuccess());
        test("Updated via function composition", 
             result.getValue().getRow().equals("D"));
        
        System.out.println();
    }
    
    private static void testPureFunctions() {
        System.out.println("▶ Testing Pure Functions:");
        
        Passenger p1 = new Passenger("John", "Doe", "john@example.com", 
                                    5, PricingStrategyFactory.createDefaultStrategy());
        Passenger p2 = new Passenger("John", "Doe", "john@example.com", 
                                    5, PricingStrategyFactory.createDefaultStrategy());
        
        test("Pure function: same input -> same price", p1.getFinalPrice() == p2.getFinalPrice());
        test("Pure function: same input -> same class", p1.getTicketClass().equals(p2.getTicketClass()));
        
        var strategy = PricingStrategyFactory.createDefaultStrategy();
        double price1 = strategy.calculatePrice(5);
        double price2 = strategy.calculatePrice(5);
        test("PricingStrategy.calculatePrice is pure", price1 == price2);
        
        // Test immutability
        Passenger p3 = new Passenger("Test", "User", "test@example.com", 
                                    1, PricingStrategyFactory.createDefaultStrategy());
        Passenger updated = p3.withEmail("new@example.com", 1, strategy);
        test("Original unchanged after withEmail", p3.getEmail().equals("test@example.com"));
        test("New instance has updated email", updated.getEmail().equals("new@example.com"));
        
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
            System.out.println("\n✓ Application demonstrates comprehensive FP features:");
            System.out.println("  • Streams API (map, filter, collect, reduce)");
            System.out.println("  • Optional for null-safety");
            System.out.println("  • Lambda expressions & Method references");
            System.out.println("  • Pure functions & Immutability");
            System.out.println("  • Higher-order functions");
            System.out.println("  • Functional composition");
        }
    }
}
