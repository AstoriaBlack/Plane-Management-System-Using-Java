package util;

import annotation.PatternInfo;
import config.Constants;
import pricing.PricingStrategy;
import pricing.PricingStrategyFactory;
import model.Passenger;
import model.Ticket;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton data manager for ticket persistence.
 * Handles reading and writing ticket data to/from JSON file.
 * 
 * Design Pattern: Singleton
 * - Ensures only one instance manages file access
 * - Provides global access point
 * - Thread-safe lazy initialization
 */
@PatternInfo(
    patternName = "Singleton",
    role = "Singleton",
    description = "Ensures single instance for file I/O operations to prevent conflicts"
)
public class TicketDataManager {

    private static volatile TicketDataManager instance;
    private final String dataFile;
    private final PricingStrategy pricingStrategy;

    /**
     * Private constructor for Singleton pattern.
     */
    private TicketDataManager() {
        this.dataFile = Constants.DATA_FILE;
        this.pricingStrategy = PricingStrategyFactory.createDefaultStrategy();
    }
    
    /**
     * Constructor with custom file path (for testing).
     */
    private TicketDataManager(String customDataFile) {
        this.dataFile = customDataFile;
        this.pricingStrategy = PricingStrategyFactory.createDefaultStrategy();
    }

    /**
     * Gets the singleton instance (thread-safe double-checked locking).
     * 
     * @return The singleton TicketDataManager instance
     */
    public static TicketDataManager getInstance() {
        if (instance == null) {
            synchronized (TicketDataManager.class) {
                if (instance == null) {
                    instance = new TicketDataManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Creates an instance with custom file path (for testing).
     * 
     * @param customDataFile Path to custom data file
     * @return A new TicketDataManager instance
     */
    public static TicketDataManager createWithFile(String customDataFile) {
        return new TicketDataManager(customDataFile);
    }

    /**
     * Saves tickets to JSON file.
     * Uses functional programming for JSON generation.
     * 
     * @param tickets List of tickets to save
     */
    public void saveTickets(List<Ticket> tickets) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile))) {
            String json = toJson(tickets);
            writer.print(json);
        } catch (IOException e) {
            System.err.println("Error saving tickets: " + e.getMessage());
        }
    }
    
    /**
     * Converts ticket list to JSON string using functional approach.
     * 
     * @param tickets List of tickets
     * @return JSON string representation
     */
    private String toJson(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return "[]";
        }
        
        String ticketEntries = tickets.stream()
            .map(this::ticketToJson)
            .collect(Collectors.joining(",\n  ", "[\n  ", "\n]"));
        
        return ticketEntries;
    }
    
    /**
     * Converts a single ticket to JSON.
     * 
     * @param ticket The ticket to convert
     * @return JSON object string
     */
    private String ticketToJson(Ticket ticket) {
        Passenger passenger = ticket.getPassenger();
        
        return String.format(
            "{\n" +
            "    \"ticketNumber\": \"%s\",\n" +
            "    \"row\": \"%s\",\n" +
            "    \"seat\": %d,\n" +
            "    \"passengerName\": \"%s\",\n" +
            "    \"passengerSurname\": \"%s\",\n" +
            "    \"passengerEmail\": \"%s\",\n" +
            "    \"ticketClass\": \"%s\",\n" +
            "    \"price\": %.2f\n" +
            "  }",
            escapeJson(ticket.getTicketNumber()),
            ticket.getRow(),
            ticket.getSeat(),
            escapeJson(passenger.getName()),
            escapeJson(passenger.getSurname()),
            escapeJson(passenger.getEmail()),
            passenger.getTicketClass(),
            passenger.getFinalPrice()
        );
    }

    /**
     * Loads tickets from JSON file.
     * 
     * @return List of tickets, empty if file doesn't exist
     */
    public List<Ticket> loadTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(dataFile))) {
                return tickets;
            }
            
            String jsonStr = Files.readString(Paths.get(dataFile)).trim();
            
            if (jsonStr.isEmpty() || jsonStr.equals("[]")) {
                return tickets;
            }
            
            // Simple JSON parsing (without external library)
            tickets = parseTicketsJson(jsonStr);
            
        } catch (IOException e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }
        
        return tickets;
    }
    
    /**
     * Parses JSON string to list of tickets.
     * Basic parser without external dependencies.
     * 
     * @param jsonStr JSON string
     * @return List of tickets
     */
    private List<Ticket> parseTicketsJson(String jsonStr) {
        List<Ticket> tickets = new ArrayList<>();
        
        // Remove leading/trailing brackets and whitespace
        jsonStr = jsonStr.trim();
        if (jsonStr.startsWith("[")) jsonStr = jsonStr.substring(1);
        if (jsonStr.endsWith("]")) jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        
        // Split by ticket objects
        String[] ticketObjects = jsonStr.split("\\},\\s*\\{");
        
        for (String ticketObj : ticketObjects) {
            try {
                // Clean up braces
                ticketObj = ticketObj.replaceAll("^\\{\\s*|\\s*\\}$", "").trim();
                
                String ticketNumber = extractValue(ticketObj, "ticketNumber");
                String row = extractValue(ticketObj, "row");
                int seat = Integer.parseInt(extractValue(ticketObj, "seat"));
                String passengerName = extractValue(ticketObj, "passengerName");
                String passengerSurname = extractValue(ticketObj, "passengerSurname");
                String passengerEmail = extractValue(ticketObj, "passengerEmail");
                
                Passenger passenger = new Passenger(passengerName, passengerSurname, passengerEmail, seat, pricingStrategy);
                Ticket ticket = new Ticket(ticketNumber, row, seat, passenger);
                
                tickets.add(ticket);
            } catch (Exception e) {
                System.err.println("Error parsing ticket: " + e.getMessage());
            }
        }
        
        return tickets;
    }
    
    /**
     * Extracts value for a given key from JSON object string.
     * 
     * @param json JSON object string
     * @param key Key to extract
     * @return Extracted value
     */
    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"?([^,\"\\}]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "";
    }
    
    /**
     * Escapes special characters for JSON.
     * 
     * @param str String to escape
     * @return Escaped string
     */
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Resets the singleton instance (for testing).
     */
    public static void resetInstance() {
        instance = null;
    }
}
