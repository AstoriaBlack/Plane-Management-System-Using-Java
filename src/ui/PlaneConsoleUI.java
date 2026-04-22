package ui;

import config.Constants;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.Ticket;

/**
 * Handles all user interface operations for the Plane Management System.
 * Separates UI concerns from business logic (Single Responsibility Principle).
 * This class demonstrates:
 * - Clean separation of concerns
 * - Functional programming with Consumer interface
 * - Immutable input handling
 */
public class PlaneConsoleUI {

    private final Scanner scanner;
    
    /**
     * Creates a PlaneConsoleUI with the provided scanner.
     * Uses dependency injection for testability.
    param scanner Scanner for user input
     */
    public PlaneConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Displays the main menu.
     */
    public void displayMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     PLANE MANAGEMENT SYSTEM            ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. Buy a Ticket                       ║");
        System.out.println("║  2. View All Tickets                   ║");
        System.out.println("║  3. Cancel a Ticket                    ║");
        System.out.println("║  4. Find Available Seat                ║");
        System.out.println("║  5. View Ticket Details                ║");
        System.out.println("║  6. View Statistics                    ║");
        System.out.println("║  7. Exit                               ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    /**
     * Gets integer input from user with validation.
    param prompt Message to display
    return Valid integer input
     */
    public int getIntInput(String prompt) {
        if (!prompt.isEmpty()) {
            System.out.print(prompt);
        }
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return value;
    }

    /**
     * Gets string input from user.
    param prompt Message to display
    return User input string
     */
    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Gets a single word input.
    param prompt Message to display
    return User input word
     */
    public String getWordInput(String prompt) {
        System.out.print(prompt);
        String word = scanner.next();
        scanner.nextLine(); // Consume remaining
        return word.toUpperCase();
    }

    /**
     * Gets validated row input.
     * return Valid row letter (A, B, C, D)
     */
    public String getRowInput() {
        String row;
        do {
            row = getWordInput("Enter row letter (A-D): ");
            if (!row.matches("[A-D]")) {
                System.out.println("Invalid row. Must be A, B, C, or D.");
            }
        } while (!row.matches("[A-D]"));
        return row;
    }
    
    /**
     * Gets validated seat number input.
     * param row Row letter to validate against
     * return Valid seat number
     */
    public int getSeatInput(String row) {
        int maxSeat = (row.equals("A") || row.equals("D")) ? Constants.SEATS_ROW_A : Constants.SEATS_ROW_B;
        int seat;
        do {
            seat = getIntInput("Enter seat number (1-" + maxSeat + "): ");
            if (seat < 1 || seat > maxSeat) {
                System.out.println("Invalid seat number for row " + row);
            }
        } while (seat < 1 || seat > maxSeat);
        return seat;
    }
    
    /**
     * Gets validated email input. return Valid email address
     */
    public String getEmailInput() {
        String email;
        do {
            email = getStringInput("Enter passenger email: ");
            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Invalid email format. Must contain @ and .");
            }
        } while (!email.contains("@") || !email.contains("."));
        return email;
    }

    /**
     * Displays a section header. param title Section title
     */
    public void displayHeader(String title) {
        System.out.println("\n=== " + title + " ===");
    }

    /**
     * Displays a success message. param message Message to display
     */
    public void displaySuccess(String message) {
        System.out.println("✓ " + message);
    }

    /**
     * Displays an error message. param message Error message
     */
    public void displayError(String message) {
        System.out.println("✗ Error: " + message);
    }

    /**
     * Displays an info message. param message Info message
     */
    public void displayInfo(String message) {
        System.out.println(message);
    }

    /**
     * Displays ticket details.
     * Uses functional approach with method chaining. param ticket Ticket to display
     */
    public void displayTicket(Ticket ticket) {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.printf("│ Ticket #: %-28s │%n", ticket.getTicketNumber());
        System.out.printf("│ Seat:     %-28s │%n", ticket.getRow() + ticket.getSeat());
        System.out.printf("│ Passenger: %-27s │%n", ticket.getPassenger().getFullName());
        System.out.printf("│ Email:    %-28s │%n", truncate(ticket.getPassenger().getEmail(), 28));
        System.out.printf("│ Class:    %-28s │%n", ticket.getPassenger().getTicketClass());
        System.out.printf("│ Price:    £%-27.2f │%n", ticket.getPassenger().getFinalPrice());
        System.out.println("└────────────────────────────────────────┘");
    }
    
    /**
     * Displays a brief ticket summary. param ticket Ticket to display
     */
    public void displayTicketBrief(Ticket ticket) {
        System.out.printf("  %-10s | %3s%-2d | %-22s | %-12s | £%-7.2f%n",
            ticket.getTicketNumber(),
            ticket.getRow(),
            ticket.getSeat(),
            truncate(ticket.getPassenger().getFullName(), 22),
            ticket.getPassenger().getTicketClass(),
            ticket.getPassenger().getFinalPrice()
        );
    }
    
    /**
     * Truncates a string to max length.
    param str String to truncate
    param maxLength Maximum length
    return Truncated string
     */
    private String truncate(String str, int maxLength) {
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Displays list of tickets.
     * Uses functional forEach with method reference. param tickets List of tickets
     */
    public void displayTicketList(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }
        
        displayHeader("Ticket List");
        System.out.println("  ──────────┼──────┼────────────────────────┼──────────────┼─────────");
        System.out.printf("  %-10s │ %4s │ %-22s │ %-12s │ %-8s%n", 
                         "Ticket #", "Seat", "Passenger", "Class", "Price");
        System.out.println("  ──────────┼──────┼────────────────────────┼──────────────┼─────────");
        
        // Functional: using forEach with method reference
        tickets.forEach(this::displayTicketBrief);
        
        System.out.println("  ──────────┴──────┴────────────────────────┴──────────────┴─────────");
    }

    /**
     * Displays statistics.
    param totalTickets Total tickets sold
    param availableSeats Available seats
    param totalRevenue Total revenue
    param classDistribution Class distribution map
     */
    public void displayStatistics(int totalTickets, int availableSeats, double totalRevenue,
                                   Map<String, Long> classDistribution) {
        displayHeader("Flight Statistics");
        System.out.println("┌────────────────────────────────────────┐");
        System.out.printf("│ Total Tickets Sold: %-18d │%n", totalTickets);
        System.out.printf("│ Available Seats:    %-18d │%n", availableSeats);
        System.out.printf("│ Maximum Capacity:   %-18d │%n", Constants.MAX_TICKETS);
        System.out.printf("│ Total Revenue:      £%-17.2f │%n", totalRevenue);
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│ Class Distribution:                    │");
        
        // Functional: using forEach with lambda
        classDistribution.forEach((ticketClass, count) -> 
            System.out.printf("│   %-15s: %3d tickets        │%n", ticketClass, count)
        );
        
        System.out.println("└────────────────────────────────────────┘");
    }
    
    /**
     * Displays the seating plan. param occupiedSeats Map of row to list of occupied seat numbers
     */
    public void displaySeatingPlan(Map<String, List<Integer>> occupiedSeats) {
        displayHeader("Seating Plan");
        System.out.println("O = Available, X = Occupied\n");
        
        displayRow("A", Constants.SEATS_ROW_A, occupiedSeats.getOrDefault("A", List.of()));
        displayRow("B", Constants.SEATS_ROW_B, occupiedSeats.getOrDefault("B", List.of()));
        System.out.println();  // Aisle
        displayRow("C", Constants.SEATS_ROW_C, occupiedSeats.getOrDefault("C", List.of()));
        displayRow("D", Constants.SEATS_ROW_D, occupiedSeats.getOrDefault("D", List.of()));
    }
    
    /**
     * Displays a single row of the seating plan.
    param row Row letter
    param totalSeats Total seats in the row
    param occupied List of occupied seat numbers
     */
    private void displayRow(String row, int totalSeats, List<Integer> occupied) {
        System.out.print(row + " ");
        for (int i = 1; i <= totalSeats; i++) {
            System.out.print(occupied.contains(i) ? "X" : "O");
        }
        System.out.println();
    }

    /**
     * Gets confirmation from user.
    param prompt Confirmation prompt
    return true if user confirms
     */
    public boolean getConfirmation(String prompt) {
        System.out.print(prompt + " (yes/no): ");
        String response = scanner.next().toLowerCase();
        scanner.nextLine();
        return response.equals("yes") || response.equals("y");
    }

    /**
     * Displays goodbye message.
     */
    public void displayGoodbye() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  Thank you for flying with us!         ║");
        System.out.println("║  Data has been saved to tickets.json   ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}
