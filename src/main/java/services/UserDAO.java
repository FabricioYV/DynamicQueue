package services;

import MatchMaking.Ranks;
import entities.Administrator;
import entities.BaseUser;
import entities.RegularUser;
import utils.ConfigManager;

import java.sql.*;

public class UserDAO {
    private DatabaseManager dbManager;

    public UserDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        createTableIfNotExists();

    }
    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                discord_id VARCHAR(20) PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                user_type ENUM('REGULAR', 'ADMIN', 'OWNER') DEFAULT 'REGULAR',
                rank_id INT DEFAULT 0,
                wins INT DEFAULT 0,
                losses INT DEFAULT 0,
                in_queue BOOLEAN DEFAULT FALSE,
                is_bot_owner BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            ConfigManager.logger("✅ Tabla 'users' verificada/creada");
        } catch (SQLException e) {
            ConfigManager.logger("❌ Error creando tabla users: " + e.getMessage());
        }
    }
    public boolean registerUser(BaseUser user) {
        String sql = "INSERT INTO users (discord_id, username, user_type, rank_id, wins, losses, in_queue, is_bot_owner) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getDiscordID());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getUserType());
            stmt.setInt(4, user.getRank().getRankId());
            stmt.setInt(5, user.getWins());
            stmt.setInt(6, user.getLosses());
            stmt.setBoolean(7, user.isInQueue());
            stmt.setBoolean(8, user instanceof Administrator && ((Administrator) user).isBotOwner());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConfigManager.logger("❌ Error registrando usuario: " + e.getMessage());
            return false;
        }
    }
    public BaseUser getUser(String discordId) {
        String sql = "SELECT * FROM users WHERE discord_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, discordId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("user_type");
                String username = rs.getString("username");
                boolean isBotOwner = rs.getBoolean("is_bot_owner");

                BaseUser user;
                if ("ADMIN".equals(userType) || "OWNER".equals(userType)) {
                    user = new Administrator(discordId, username, isBotOwner);
                } else {
                    user = new RegularUser(discordId, username);
                }

                user.setRank(Ranks.values()[rs.getInt("rank_id")]);
                user.setWins(rs.getInt("wins"));
                user.setLosses(rs.getInt("losses"));
                user.setInQueue(rs.getBoolean("in_queue"));

                return user;
            }
        } catch (SQLException e) {
            ConfigManager.logger("❌ Error obteniendo usuario: " + e.getMessage());
        }
        return null;

    }
    public boolean userExists(String discordId) {
        String sql = "SELECT COUNT(*) FROM users WHERE discord_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, discordId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            ConfigManager.logger("❌ Error verificando existencia de usuario: " + e.getMessage());
            return false;
        }
        return false;
    }
}
