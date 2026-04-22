package model;

import annotation.FunctionalComponent;
import annotation.PatternInfo;
import java.util.Objects;
import pricing.PricingStrategy;

/**
 * Represents a passenger's information and ticket pricing details.
 * Uses functional programming for calculations.

 * This class demonstrates:
 * - Immutability (final fields)
 * - Functional programming (pure functions)
 * - Strategy pattern (injectable pricing algorithm)
 */
@PatternInfo(
    patternName = "Strategy",
    role = "Context",
    description = "Uses PricingStrategy to calculate ticket prices - strategy can be changed at runtime"
)
public class Passenger {

    private final String name;
    private final String surname;
    private final String email;
    private final String ticketClass;
    private final double basePrice;
    private final double finalPrice;

    /**
     * Creates a Passenger instance with the given information and pricing strategy.
    
    param name Passenger's first name
    param surname Passenger's last name
    param email Passenger's email
    param seatNumber Seat number for pricing calculation
    param pricingStrategy Strategy to use for price calculation
     */
    public Passenger(String name, String surname, String email, int seatNumber, PricingStrategy pricingStrategy) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.surname = Objects.requireNonNull(surname, "Surname cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        
        // Strategy pattern: delegate price calculation
        this.basePrice = pricingStrategy.calculatePrice(seatNumber);
        this.finalPrice = basePrice;
        this.ticketClass = pricingStrategy.getTicketClass(seatNumber);
    }

    // Getters - all return immutable values
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public String getName() { return name; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public String getSurname() { return surname; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public String getEmail() { return email; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public String getTicketClass() { return ticketClass; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public double getBasePrice() { return basePrice; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public double getFinalPrice() { return finalPrice; }
    
    /**
     * Gets the full name of the passenger.
     * Demonstrates functional programming with pure string concatenation.
    return Full name
     */
    @FunctionalComponent(isPure = true, description = "Pure function combining name fields")
    public String getFullName() {
        return name + " " + surname;
    }
    
    /**
     * Validates if the email format is correct.
     * Demonstrates functional programming with pure validation.
    return true if email is valid
     */
    @FunctionalComponent(isPure = true, description = "Pure validation function")
    public boolean hasValidEmail() {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    /**
     * Creates a new Passenger with updated email (immutable update pattern).

    param newEmail The new email
    param seatNumber Seat number for pricing
    param pricingStrategy Pricing strategy
    return A new Passenger instance with the updated email
     */
    @FunctionalComponent(isPure = true, description = "Returns new instance - immutable update pattern")
    public Passenger withEmail(String newEmail, int seatNumber, PricingStrategy pricingStrategy) {
        return new Passenger(this.name, this.surname, newEmail, seatNumber, pricingStrategy);
    }
    
    @Override
    public String toString() {
        return String.format("Passenger{name='%s %s', email='%s', class='%s', price=£%.2f}", 
                           name, surname, email, ticketClass, finalPrice);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return email.equalsIgnoreCase(passenger.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email.toLowerCase());
    }
}
