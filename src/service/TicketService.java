package service;

import annotation.FunctionalComponent;
import annotation.PatternInfo;
import config.Constants;
import pricing.PricingStrategy;
import pricing.PricingStrategyFactory;
import model.Passenger;
import model.Ticket;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Service layer for ticket operations.
 * Implements business logic separately from UI and data access.
 * 
 * This class demonstrates:
 * - Service Layer pattern
 * - Dependency Injection
 * - Functional programming with higher-order functions
 */
@PatternInfo(
    patternName = "Service Layer",
    role = "Service",
    description = "Encapsulates business logic for ticket operations"
)
public class TicketService {

    private final ITicketRepository repository;
    private final PricingStrategy pricingStrategy;

    /**
     * Creates TicketService with default dependencies.
     */
    public TicketService() {
        this.repository = new TicketRepository();
        this.pricingStrategy = PricingStrategyFactory.createDefaultStrategy();
    }
    
    /**
     * Creates TicketService with custom dependencies (Dependency Injection).
     * 
     * @param repository Custom repository implementation
     * @param pricingStrategy Custom pricing strategy
     */
    public TicketService(ITicketRepository repository, PricingStrategy pricingStrategy) {
        this.repository = repository;
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Creates and purchases a new ticket.
     * 
     * @param ticketNumber Ticket number
     * @param row Seat row (A, B, C, D)
     * @param seat Seat number
     * @param passengerName Passenger's first name
     * @param passengerSurname Passenger's surname
     * @param passengerEmail Passenger's email
     * @return Result containing the ticket or error message
     */
    @FunctionalComponent(description = "Returns Result monad-style object")
    public Result<Ticket> buyTicket(String ticketNumber, String row, int seat, 
                                     String passengerName, String passengerSurname, String passengerEmail) {
        // Validation
        if (ticketNumber == null || ticketNumber.trim().isEmpty()) {
            return Result.failure("Ticket number cannot be empty");
        }
        if (row == null || row.trim().isEmpty()) {
            return Result.failure("Row cannot be empty");
        }
        if (!isValidRow(row)) {
            return Result.failure("Invalid row. Must be A, B, C, or D");
        }
        if (!isValidSeat(row, seat)) {
            return Result.failure("Invalid seat number for row " + row);
        }
        if (passengerName == null || passengerName.trim().isEmpty()) {
            return Result.failure("Passenger name cannot be empty");
        }
        if (passengerSurname == null || passengerSurname.trim().isEmpty()) {
            return Result.failure("Passenger surname cannot be empty");
        }
        if (!isValidEmail(passengerEmail)) {
            return Result.failure("Invalid email address");
        }
        
        // Check capacity
        if (!repository.hasCapacity()) {
            return Result.failure("No available seats. Flight is full!");
        }
        
        // Check for duplicate ticket
        if (repository.findTicketByNumber(ticketNumber).isPresent()) {
            return Result.failure("Ticket with number " + ticketNumber + " already exists");
        }
        
        // Check if seat is taken
        if (repository.findTicketBySeat(row, seat).isPresent()) {
            return Result.failure("Seat " + row + seat + " is already booked");
        }
        
        // Create ticket
        Passenger passenger = new Passenger(passengerName.trim(), passengerSurname.trim(), 
                                           passengerEmail.trim(), seat, pricingStrategy);
        Ticket ticket = new Ticket(ticketNumber.trim(), row.toUpperCase(), seat, passenger);
        
        if (repository.addTicket(ticket)) {
            return Result.success(ticket);
        }
        return Result.failure("Failed to purchase ticket");
    }

    /**
     * Gets all tickets sorted by seat.
     * 
     * @return List of tickets
     */
    @FunctionalComponent(isPure = true, description = "Delegates to repository")
    public List<Ticket> getAllTicketsSorted() {
        return ((TicketRepository) repository).getTicketsSortedBySeat();
    }

    /**
     * Finds a ticket by ticket number.
     * 
     * @param ticketNumber Ticket number
     * @return Optional containing ticket if found
     */
    @FunctionalComponent(isPure = true, description = "Returns Optional for null-safety")
    public Optional<Ticket> findTicket(String ticketNumber) {
        return repository.findTicketByNumber(ticketNumber);
    }
    
    /**
     * Finds a ticket by seat location.
     * 
     * @param row Seat row
     * @param seat Seat number
     * @return Optional containing ticket if found
     */
    @FunctionalComponent(isPure = true, description = "Returns Optional for null-safety")
    public Optional<Ticket> findTicketBySeat(String row, int seat) {
        return repository.findTicketBySeat(row, seat);
    }

    /**
     * Updates a ticket.
     * Demonstrates functional transformation pattern.
     * 
     * @param ticketNumber Ticket number to update
     * @param transformer Function to transform the ticket
     * @return Result containing updated ticket or error
     */
    @FunctionalComponent(description = "Higher-order function accepting transformer")
    public Result<Ticket> updateTicket(String ticketNumber, Function<Ticket, Ticket> transformer) {
        return repository.findTicketByNumber(ticketNumber)
                .map(existing -> {
                    Ticket updated = transformer.apply(existing);
                    if (repository.updateTicket(ticketNumber, updated)) {
                        return Result.success(updated);
                    }
                    return Result.<Ticket>failure("Failed to update ticket");
                })
                .orElse(Result.failure("Ticket with number " + ticketNumber + " not found"));
    }
    
    /**
     * Updates ticket with new passenger information.
     * 
     * @param ticketNumber Ticket number
     * @param newPassengerEmail New passenger email (null to keep existing)
     * @return Result containing updated ticket or error
     */
    public Result<Ticket> updateTicketPassenger(String ticketNumber, String newPassengerEmail) {
        return updateTicket(ticketNumber, existing -> {
            if (newPassengerEmail != null && !newPassengerEmail.trim().isEmpty() && isValidEmail(newPassengerEmail)) {
                Passenger updatedPassenger = existing.getPassenger()
                    .withEmail(newPassengerEmail.trim(), existing.getSeat(), pricingStrategy);
                return existing.withPassenger(updatedPassenger);
            }
            return existing;
        });
    }

    /**
     * Cancels a ticket.
     * 
     * @param ticketNumber Ticket number to cancel
     * @return true if cancelled successfully
     */
    public boolean cancelTicket(String ticketNumber) {
        return repository.removeTicket(ticketNumber);
    }

    /**
     * Gets total ticket count.
     * 
     * @return Number of tickets sold
     */
    @FunctionalComponent(isPure = true)
    public int getTotalTickets() {
        return repository.getTotalTickets();
    }

    /**
     * Gets available seats.
     * 
     * @return Number of available seats
     */
    @FunctionalComponent(isPure = true)
    public int getAvailableSeats() {
        return repository.availableSeats();
    }

    /**
     * Gets total revenue.
     * 
     * @return Total revenue from all tickets
     */
    @FunctionalComponent(isPure = true)
    public double getTotalRevenue() {
        return ((TicketRepository) repository).getTotalRevenue();
    }

    /**
     * Gets class distribution.
     * 
     * @return Map of ticket class to count
     */
    @FunctionalComponent(isPure = true, description = "Returns immutable map")
    public Map<String, Long> getClassDistribution() {
        return ((TicketRepository) repository).countByClass();
    }
    
    /**
     * Gets tickets by row.
     * 
     * @param row Row letter
     * @return List of tickets in the row
     */
    @FunctionalComponent(isPure = true)
    public List<Ticket> getTicketsByRow(String row) {
        return ((TicketRepository) repository).getTicketsByRow(row);
    }
    
    /**
     * Gets the current pricing strategy.
     * 
     * @return The pricing strategy in use
     */
    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }
    
    /**
     * Validates row letter.
     * 
     * @param row Row letter
     * @return true if valid
     */
    private boolean isValidRow(String row) {
        return row != null && (row.equalsIgnoreCase("A") || row.equalsIgnoreCase("B") || 
                              row.equalsIgnoreCase("C") || row.equalsIgnoreCase("D"));
    }
    
    /**
     * Validates seat number for given row.
     * 
     * @param row Row letter
     * @param seat Seat number
     * @return true if valid
     */
    private boolean isValidSeat(String row, int seat) {
        if (row.equalsIgnoreCase("A") || row.equalsIgnoreCase("D")) {
            return seat >= 1 && seat <= Constants.SEATS_ROW_A;
        } else if (row.equalsIgnoreCase("B") || row.equalsIgnoreCase("C")) {
            return seat >= 1 && seat <= Constants.SEATS_ROW_B;
        }
        return false;
    }
    
    /**
     * Validates email format.
     * 
     * @param email Email address
     * @return true if valid
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * Result class for functional error handling.
     * Demonstrates Monad pattern for clean error handling.
     * 
     * @param <T> Type of successful result
     */
    @PatternInfo(patternName = "Monad", role = "Result Type", 
                 description = "Functional error handling without exceptions")
    public static class Result<T> {
        private final T value;
        private final String error;
        private final boolean success;

        private Result(T value, String error, boolean success) {
            this.value = value;
            this.error = error;
            this.success = success;
        }

        public static <T> Result<T> success(T value) {
            return new Result<>(value, null, true);
        }

        public static <T> Result<T> failure(String error) {
            return new Result<>(null, error, false);
        }

        public boolean isSuccess() { return success; }
        public T getValue() { return value; }
        public String getError() { return error; }
        
        /**
         * Maps the result value if successful.
         * Demonstrates functor pattern.
         */
        @FunctionalComponent(isPure = true, description = "Functor map operation")
        public <U> Result<U> map(Function<T, U> mapper) {
            return success ? Result.success(mapper.apply(value)) : Result.failure(error);
        }
    }
}
