import config.Constants;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import model.Ticket;
import reflection.ReflectionInspector;
import service.TicketService;
import service.TicketService.Result;
import ui.PlaneConsoleUI;

/**
 * Main entry point for the Plane Management System.
 * 
 * Refactored to follow SOLID principles:
 * - Single Responsibility: Main only handles menu flow
 * - Open/Closed: Easy to extend with new menu options
 * - Dependency Inversion: Depends on abstractions (Service layer)
 * 
 * Design improvements:
 * - Removed static mutable state (Scanner)
 * - Separated UI logic to PlaneConsoleUI class
 * - Separated business logic to TicketService class
 * - No magic numbers (using Constants class)
 * - Functional programming where appropriate
 * - Clean method organization
 */
public class PlaneManagement {

    private final PlaneConsoleUI ui;
    private final TicketService service;

    /**
     * Creates PlaneManagement with dependency injection for testability.
     * param ui User interface handler
     * param service Ticket service layer
     */
    public PlaneManagement(PlaneConsoleUI ui, TicketService service) {
        this.ui = ui;
        this.service = service;
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        // Display comprehensive reflection inspection on startup
        ReflectionInspector.performFullInspection();
        
        // Create dependencies
        try (Scanner scanner = new Scanner(System.in)) {
            PlaneConsoleUI ui = new PlaneConsoleUI(scanner);
            TicketService service = new TicketService();
            
            // Run application
            PlaneManagement app = new PlaneManagement(ui, service);
            app.run();
        }
    }

    /**
     * Main application loop.
     * Clean and focused - delegates to handler methods.
     */
    public void run() {
        boolean running = true;
        
        while (running) {
            ui.displayMenu();
            int choice = ui.getIntInput("\nEnter your choice: ");

            switch (choice) {
                case Constants.MENU_BUY_TICKET -> handleBuyTicket();
                case Constants.MENU_VIEW_ALL -> handleViewAllTickets();
                case Constants.MENU_CANCEL_TICKET -> handleCancelTicket();
                case Constants.MENU_FIND_AVAILABLE -> handleFindAvailable();
                case Constants.MENU_VIEW_DETAILS -> handleViewTicketDetails();
                case Constants.MENU_VIEW_STATISTICS -> handleViewStatistics();
                case Constants.MENU_EXIT -> running = handleExit();
                default -> ui.displayError("Invalid choice. Please select 1-7.");
            }
        }
    }

    /**
     * Handles buying a new ticket.
     * Clean separation of UI input, validation, and business logic.
     */
    private void handleBuyTicket() {
        ui.displayHeader("Buy a Ticket");
        
        String ticketNumber = ui.getWordInput("Enter ticket number: ");
        String row = ui.getRowInput();
        int seat = ui.getSeatInput(row);
        
        ui.displayInfo("\n--- Passenger Information ---");
        String passengerName = ui.getStringInput("Enter passenger first name: ");
        String passengerSurname = ui.getStringInput("Enter passenger surname: ");
        String passengerEmail = ui.getEmailInput();

        Result<Ticket> result = service.buyTicket(ticketNumber, row, seat, 
                                                    passengerName, passengerSurname, passengerEmail);
        
        if (result.isSuccess()) {
            Ticket ticket = result.getValue();
            ui.displaySuccess("Ticket purchased successfully!");
            ui.displayInfo("  Seat: " + ticket.getRow() + ticket.getSeat());
            ui.displayInfo("  Class: " + ticket.getPassenger().getTicketClass());
            ui.displayInfo("  Price: £" + String.format("%.2f", ticket.getPassenger().getFinalPrice()));
            ui.displayInfo("  Available seats: " + service.getAvailableSeats());
        } else {
            ui.displayError(result.getError());
        }
    }

    /**
     * Handles viewing all tickets.
     * Demonstrates functional approach with stream operations in service layer.
     */
    private void handleViewAllTickets() {
        List<Ticket> tickets = service.getAllTicketsSorted();
        ui.displayTicketList(tickets);
        
        if (!tickets.isEmpty()) {
            ui.displayInfo("\nTotal Tickets: " + service.getTotalTickets());
            ui.displayInfo("Available Seats: " + service.getAvailableSeats());
            ui.displayInfo("Total Revenue: £" + String.format("%.2f", service.getTotalRevenue()));
        }
    }

    /**
     * Handles canceling a ticket.
     * Uses Optional for null-safe operations.
     */
    private void handleCancelTicket() {
        ui.displayHeader("Cancel a Ticket");
        
        String ticketNumber = ui.getWordInput("Enter ticket number to cancel: ");
        
        Optional<Ticket> existing = service.findTicket(ticketNumber);
        
        if (existing.isEmpty()) {
            ui.displayError("Ticket with number " + ticketNumber + " not found.");
            return;
        }

        Ticket ticket = existing.get();
        ui.displayInfo("\nTicket to be cancelled:");
        ui.displayTicket(ticket);
        
        if (!ui.getConfirmation("\nAre you sure?")) {
            ui.displayInfo("Cancellation cancelled.");
            return;
        }
        
        if (service.cancelTicket(ticketNumber)) {
            ui.displaySuccess("Ticket cancelled successfully!");
            ui.displayInfo("Refund amount: £" + String.format("%.2f", ticket.getPassenger().getFinalPrice()));
            ui.displayInfo("Available seats: " + service.getAvailableSeats());
        } else {
            ui.displayError("Could not cancel ticket.");
        }
    }

    /**
     * Handles finding available seats.
     * Demonstrates functional programming with stream operations.
     */
    private void handleFindAvailable() {
        ui.displayHeader("Find Available Seat");
        
        // Get occupied seats for each row
        Map<String, List<Integer>> occupiedSeats = Map.of(
            "A", service.getTicketsByRow("A").stream().map(Ticket::getSeat).collect(Collectors.toList()),
            "B", service.getTicketsByRow("B").stream().map(Ticket::getSeat).collect(Collectors.toList()),
            "C", service.getTicketsByRow("C").stream().map(Ticket::getSeat).collect(Collectors.toList()),
            "D", service.getTicketsByRow("D").stream().map(Ticket::getSeat).collect(Collectors.toList())
        );
        
        ui.displaySeatingPlan(occupiedSeats);
        
        // Find first available seat
        String firstAvailable = findFirstAvailableSeat(occupiedSeats);
        if (firstAvailable != null) {
            ui.displaySuccess("\nFirst available seat: " + firstAvailable);
        } else {
            ui.displayInfo("\nNo available seats.");
        }
    }
    
    /**
     * Finds the first available seat.
     * 
     * @param occupiedSeats Map of occupied seats by row
     * @return First available seat or null if full
     */
    private String findFirstAvailableSeat(Map<String, List<Integer>> occupiedSeats) {
        String[] rows = {"A", "B", "C", "D"};
        int[] rowSizes = {Constants.SEATS_ROW_A, Constants.SEATS_ROW_B, 
                         Constants.SEATS_ROW_C, Constants.SEATS_ROW_D};
        
        for (int i = 0; i < rows.length; i++) {
            List<Integer> occupied = occupiedSeats.get(rows[i]);
            for (int seat = 1; seat <= rowSizes[i]; seat++) {
                if (!occupied.contains(seat)) {
                    return rows[i] + seat;
                }
            }
        }
        return null;
    }

    /**
     * Handles viewing ticket details.
     */
    private void handleViewTicketDetails() {
        ui.displayHeader("View Ticket Details");
        
        ui.displayInfo("Search by:");
        ui.displayInfo("1. Ticket number");
        ui.displayInfo("2. Seat location");
        
        int searchType = ui.getIntInput("Enter choice (1-2): ");
        
        if (searchType == 1) {
            String ticketNumber = ui.getWordInput("Enter ticket number (must be minimal 2 digits | ex:01): ");
            service.findTicket(ticketNumber).ifPresentOrElse(
                ui::displayTicket,
                () -> ui.displayError("Ticket with number " + ticketNumber + " not found.")
            );
        } else if (searchType == 2) {
            String row = ui.getRowInput();
            int seat = ui.getSeatInput(row);
            service.findTicketBySeat(row, seat).ifPresentOrElse(
                ui::displayTicket,
                () -> ui.displayError("No ticket found for seat " + row + seat)
            );
        } else {
            ui.displayError("Invalid search type.");
        }
    }

    /**
     * Handles viewing statistics.
     * Demonstrates functional programming with method references.
     */
    private void handleViewStatistics() {
        ui.displayStatistics(
            service.getTotalTickets(),
            service.getAvailableSeats(),
            service.getTotalRevenue(),
            service.getClassDistribution()
        );
    }

    /**
     * Handles exit.
     * return false to terminate the main loop
     */
    private boolean handleExit() {
        ui.displayGoodbye();
        return false;
    }
}
