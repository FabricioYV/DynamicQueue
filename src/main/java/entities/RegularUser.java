package entities;

public class RegularUser extends BaseUser{
    public RegularUser(String discordID, String username) {
        // Call the constructor of the parent class BaseUser
        super(discordID, username);

    }
    @Override
    public boolean canExecuteCommand(String command) {
        // Comandos permitidos para usuarios regulares
        return switch (command.toLowerCase()) {
            case "register", "registro", "profile", "perfil",
                 "queue", "cola", "leave", "salir","matches","partidas", "stats", "estadisticas" -> true;
            default -> false;
        };
    }
    @Override
    public String getUserType() {
        return "Usuario";
    }

    @Override
    public String getDisplayName() {
        return username + " (" + rank.getRankName() + ")";
    }
}

