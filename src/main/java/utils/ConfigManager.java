package utils;

public class ConfigManager {
    // Replace with database connection details or other configurations as needed
    private static String token = "YOUR_TOKEN_HERE";
    private static String prefix = "!";
    private static String messageActivity = "DynamicQueue is online!";
    public static String getToken() {
        return token;
    }
    private static final String QueueID = "YOUR_QUEUE";
    public static final String DATABASE_URL = "url_to_your_database";
    public static final String DATABASE_USERNAME = "username_to_your_database";
    public static final String DATABASE_PASSWORD = "password_to_your_database";
    private static final String BOT_OWNER_ID = "YOUR_DISCORD_ID_HERE";





    public static String getPrefix() {
        return prefix;
    }
    public static String getMessageActivity() {
        return messageActivity;
    }
    public static void logger(String message) {
        // Simple logger to print messages to the console
        //Future enhancements could include writing to a file or external logging service
        System.out.println("[DynamicQueue] " + message);
    }
    public static String getQueueID() {
        return QueueID;
    }
    public static String getBotOwnerId() {
        return BOT_OWNER_ID;
    }


}
// Fabricio YV 2025 | Project DynamicQueue for subject "ORIENTED TO OBJECT PROGRAMMING" at Universidad Nacional de Ingeneria | Computer Science
