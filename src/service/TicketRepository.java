package service;

import annotation.FunctionalComponent;
import annotation.PatternInfo;
import config.Constants;
import model.Ticket;
import util.TicketDataManager;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository implementation for Ticket data management.
 * Implements the Repository Design Pattern with functional programming features.
 * 
 * Design Pattern: Repository
 * - Mediates between domain and data mapping layers
 * - Provides collection-like interface for domain objects
 * - Abstracts data storage mechanism
 * 
 * Functional Programming Features:
 * - Uses streams for filtering, mapping, and collecting
 * - Supports predicate-based queries
 * - Uses Optional for null-safe operations
 */
@PatternInfo(
    patternName = "Repository",
    role = "Concrete Repository",
    description = "Implements data access logic with JSON persistence"
)
public class TicketRepository implements ITicketRepository {

    private final List<Ticket> tickets;
    private final TicketDataManager dataManager;

    public TicketRepository() {
        this.dataManager = TicketDataManager.getInstance();
        this.tickets = new ArrayList<>(dataManager.loadTickets());
    }
    
    /**
     * Constructor for dependency injection (useful for testing).
     * 
     * @param dataManager Custom data manager implementation
     */
    public TicketRepository(TicketDataManager dataManager) {
        this.dataManager = dataManager;
        this.tickets = new ArrayList<>(dataManager.loadTickets());
    }

    private void saveTickets() {
        dataManager.saveTickets(tickets);
    }

    // Create
    @Override
    public boolean addTicket(Ticket ticket) {
        if (!hasCapacity()) {
            return false;
        }
        
        // Functional check for existing ticket using anyMatch
        boolean exists = tickets.stream()
                .anyMatch(t -> t.getTicketNumber().equalsIgnoreCase(ticket.getTicketNumber()));
        
        if (exists) {
            return false;
        }
        
        tickets.add(ticket);
        saveTickets();
        return true;
    }

    // Read operations using functional programming
    
    @Override
    @FunctionalComponent(isPure = true, description = "Returns defensive copy of ticket list")
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    @Override
    @FunctionalComponent(isPure = true, description = "Functional stream-based search with Optional")
    public Optional<Ticket> findTicketByNumber(String ticketNumber) {
        return tickets.stream()
                .filter(t -> t.getTicketNumber().equalsIgnoreCase(ticketNumber))
                .findFirst();
    }
    
    @Override
    @FunctionalComponent(isPure = true, description = "Functional stream-based search by seat location")
    public Optional<Ticket> findTicketBySeat(String row, int seat) {
        return tickets.stream()
                .filter(t -> t.getRow().equalsIgnoreCase(row) && t.getSeat() == seat)
                .findFirst();
    }
    
    /**
     * Finds tickets matching a custom predicate.
     * Demonstrates higher-order functions - accepts function as parameter.
     * 
     * @param predicate Condition to filter tickets
     * @return List of matching tickets
     */
    @Override
    @FunctionalComponent(isPure = true, description = "Higher-order function accepting predicate")
    public List<Ticket> findTicketsBy(Predicate<Ticket> predicate) {
        return tickets.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Gets tickets sorted by ticket number using functional approach.
     * 
     * @return List of tickets sorted by ticket number
     */
    @FunctionalComponent(isPure = true, description = "Functional sorting using method reference")
    public List<Ticket> getTicketsSortedByNumber() {
        return tickets.stream()
                .sorted(Comparator.comparing(Ticket::getTicketNumber))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets tickets sorted by row and seat.
     * 
     * @return List of tickets sorted by seating position
     */
    @FunctionalComponent(isPure = true, description = "Functional sorting with comparator")
    public List<Ticket> getTicketsSortedBySeat() {
        return tickets.stream()
                .sorted(Comparator.comparing(Ticket::getRow)
                        .thenComparingInt(Ticket::getSeat))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets tickets filtered by row.
     * 
     * @param row The row to filter by
     * @return List of tickets in the specified row
     */
    @FunctionalComponent(isPure = true, description = "Functional filtering with predicate")
    public List<Ticket> getTicketsByRow(String row) {
        return findTicketsBy(t -> t.getRow().equalsIgnoreCase(row));
    }
    
    /**
     * Gets tickets filtered by class.
     * 
     * @param ticketClass The ticket class to filter by
     * @return List of tickets with matching class
     */
    @FunctionalComponent(isPure = true, description = "Functional filtering for class")
    public List<Ticket> getTicketsByClass(String ticketClass) {
        return findTicketsBy(t -> t.getPassenger().getTicketClass().equalsIgnoreCase(ticketClass));
    }

    // Update
    @Override
    public boolean updateTicket(String ticketNumber, Ticket updatedTicket) {
        return tickets.stream()
                .filter(t -> t.getTicketNumber().equalsIgnoreCase(ticketNumber))
                .findFirst()
                .map(t -> {
                    int index = tickets.indexOf(t);
                    tickets.set(index, updatedTicket);
                    saveTickets();
                    return true;
                })
                .orElse(false);
    }

    // Delete
    @Override
    public boolean removeTicket(String ticketNumber) {
        boolean removed = tickets.removeIf(t -> t.getTicketNumber().equalsIgnoreCase(ticketNumber));
        if (removed) {
            saveTickets();
        }
        return removed;
    }

    // Statistics using functional programming
    
    @Override
    @FunctionalComponent(isPure = true, description = "Pure function returning count")
    public int getTotalTickets() {
        return tickets.size();
    }
    
    @Override
    @FunctionalComponent(isPure = true, description = "Pure function calculating available seats")
    public int availableSeats() {
        return Constants.MAX_TICKETS - tickets.size();
    }
    
    @Override
    @FunctionalComponent(isPure = true, description = "Pure predicate function")
    public boolean hasCapacity() {
        return tickets.size() < Constants.MAX_TICKETS;
    }
    
    /**
     * Calculates total revenue using functional reduce.
     * 
     * @return Total revenue from all tickets, or 0 if no tickets
     */
    @FunctionalComponent(isPure = true, description = "Functional aggregation using stream sum")
    public double getTotalRevenue() {
        return tickets.stream()
                .mapToDouble(t -> t.getPassenger().getFinalPrice())
                .sum();
    }
    
    /**
     * Groups tickets by class using Collectors.groupingBy.
     * 
     * @return Map of ticket class to list of tickets
     */
    @FunctionalComponent(isPure = true, description = "Functional grouping using Collectors")
    public Map<String, List<Ticket>> groupByClass() {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                    t -> t.getPassenger().getTicketClass(),
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }
    
    /**
     * Counts tickets per class.
     * 
     * @return Map of class to count
     */
    @FunctionalComponent(isPure = true, description = "Functional counting using Collectors")
    public Map<String, Long> countByClass() {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                    t -> t.getPassenger().getTicketClass(),
                    LinkedHashMap::new,
                    Collectors.counting()
                ));
    }
    
    /**
     * Groups tickets by row.
     * 
     * @return Map of row to list of tickets
     */
    @FunctionalComponent(isPure = true, description = "Functional grouping by row")
    public Map<String, List<Ticket>> groupByRow() {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                    Ticket::getRow,
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }
}
