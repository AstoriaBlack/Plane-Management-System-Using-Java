package config;

/**
 * Application constants to eliminate magic numbers and strings.
 * Demonstrates clean code practices by centralizing configuration values.
 */
public final class Constants {
    
    // Prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
    
    // Capacity limits
    public static final int MAX_TICKETS = 52;  // 14 + 12 + 12 + 14 seats across 4 rows
    public static final int SEATS_ROW_A = 14;
    public static final int SEATS_ROW_B = 12;
    public static final int SEATS_ROW_C = 12;
    public static final int SEATS_ROW_D = 14;
    
    // Ticket pricing
    public static final double FIRST_CLASS_PRICE = 200.0;
    public static final double BUSINESS_CLASS_PRICE = 150.0;
    public static final double ECONOMY_CLASS_PRICE = 180.0;
    
    // Ticket class labels
    public static final String CLASS_FIRST = "First Class";
    public static final String CLASS_BUSINESS = "Business";
    public static final String CLASS_ECONOMY = "Economy";
    
    // Row labels
    public static final String ROW_A = "A";
    public static final String ROW_B = "B";
    public static final String ROW_C = "C";
    public static final String ROW_D = "D";
    
    // File paths
    public static final String DATA_FILE = "tickets.json";
    
    // Menu options
    public static final int MENU_BUY_TICKET = 1;
    public static final int MENU_VIEW_ALL = 2;
    public static final int MENU_CANCEL_TICKET = 3;
    public static final int MENU_FIND_AVAILABLE = 4;
    public static final int MENU_VIEW_DETAILS = 5;
    public static final int MENU_VIEW_STATISTICS = 6;
    public static final int MENU_EXIT = 7;
}

