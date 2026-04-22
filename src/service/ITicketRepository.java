package service;

import model.Ticket;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Repository interface for Ticket data access.
 * Part of the Repository Design Pattern - abstracts data storage mechanism.
 * 
 * Benefits:
 * - Decouples business logic from data access
 * - Makes the application testable with mock implementations
 * - Easy to swap storage mechanisms (JSON, Database, etc.)
 */
public interface ITicketRepository {
    
    // Create
    boolean addTicket(Ticket ticket);
    
    // Read
    List<Ticket> getAllTickets();
    Optional<Ticket> findTicketByNumber(String ticketNumber);
    Optional<Ticket> findTicketBySeat(String row, int seat);
    List<Ticket> findTicketsBy(Predicate<Ticket> predicate);
    
    // Update
    boolean updateTicket(String ticketNumber, Ticket updatedTicket);
    
    // Delete
    boolean removeTicket(String ticketNumber);
    
    // Utility
    int getTotalTickets();
    int availableSeats();
    boolean hasCapacity();
}
