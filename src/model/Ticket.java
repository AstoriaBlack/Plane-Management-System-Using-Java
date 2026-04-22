package model;

import annotation.FunctionalComponent;
import java.util.Objects;

/**
 * Immutable Ticket entity representing a plane ticket in the system.

 * Design principles:
 * - Immutability: All fields are final
 * - Encapsulation: Private fields with public getters
 * - Value object semantics: equals/hashCode based on ticket number
 */
public final class Ticket {

    private final String ticketNumber;
    private final String row;
    private final int seat;
    private final Passenger passenger;

    /**
     * Creates a new Ticket instance.
    param ticketNumber Unique identifier for the ticket
    param row Seat row (A, B, C, D)
    param seat Seat number within the row
    param passenger Passenger information
     */
    public Ticket(String ticketNumber, String row, int seat, Passenger passenger) {
        this.ticketNumber = Objects.requireNonNull(ticketNumber, "Ticket number cannot be null");
        this.row = Objects.requireNonNull(row, "Row cannot be null");
        this.seat = seat;
        this.passenger = Objects.requireNonNull(passenger, "Passenger cannot be null");
    }

    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public String getTicketNumber() { return ticketNumber; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public String getRow() { return row; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public int getSeat() { return seat; }
    
    @FunctionalComponent(isPure = true, description = "Pure getter - no side effects")
    public Passenger getPassenger() { return passenger; }
    
    /**
     * Creates a new Ticket with updated row (immutable update pattern).
     * Demonstrates functional programming principle of returning new instances.
    
     * @param newRow The new row
     * @return A new Ticket instance with the updated row
     */
    @FunctionalComponent(isPure = true, description = "Returns new instance - immutable update pattern")
    public Ticket withRow(String newRow) {
        return new Ticket(this.ticketNumber, newRow, this.seat, this.passenger);
    }
    
    /**
     * Creates a new Ticket with updated passenger (immutable update pattern).
    
    param newPassenger The new passenger
    return A new Ticket instance with the updated passenger
     */
    @FunctionalComponent(isPure = true, description = "Returns new instance - immutable update pattern")
    public Ticket withPassenger(Passenger newPassenger) {
        return new Ticket(this.ticketNumber, this.row, this.seat, newPassenger);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return ticketNumber.equalsIgnoreCase(ticket.ticketNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ticketNumber.toLowerCase());
    }
    
    @Override
    public String toString() {
        return String.format("Ticket{ticketNumber='%s', row='%s', seat=%d, class='%s'}", 
                           ticketNumber, row, seat, passenger.getTicketClass());
    }
}
