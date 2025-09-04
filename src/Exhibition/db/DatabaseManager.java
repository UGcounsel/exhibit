package Exhibition.db;

import java.sql.*;
import java.io.*;
import java.util.Properties;
import Exhibition.model.Participant;

public class DatabaseManager {
    private Connection connection;
    private static final String CONFIG_FILE = "config.properties";
    private static final String DEFAULT_DB_PATH = "db/VUE_Exhibition.accdb";

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            Properties props = new Properties();
            File configFile = new File(CONFIG_FILE);

            if (!configFile.exists()) {
                // Create default config
                props.setProperty("db.path", DEFAULT_DB_PATH);
                try (OutputStream out = new FileOutputStream(configFile)) {
                    props.store(out, "Database configuration");
                    System.out.println("Created default config.properties");
                }
            } else {
                try (InputStream in = new FileInputStream(configFile)) {
                    props.load(in);
                }
            }

            String dbPath = props.getProperty("db.path");

            File dbFile = new File(dbPath);
            String absoluteDbPath = dbFile.getAbsolutePath();

            connection = DriverManager.getConnection("jdbc:ucanaccess://" + absoluteDbPath);
            System.out.println("Database connected at: " + absoluteDbPath);

        } catch (Exception ex) {
            System.out.println("Database connection failed:");
            ex.printStackTrace();
        }
    }

    // Add a participant
    public boolean addParticipant(Participant participant) {
        String sql = "INSERT INTO Participants (RegistrationID, StudentName, Faculty, ProjectTitle, ContactNumber, Email, ImagePath) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, participant.getRegistrationId());
            stmt.setString(2, participant.getName());
            stmt.setString(3, participant.getFaculty());
            stmt.setString(4, participant.getProjectTitle());
            stmt.setString(5, participant.getContact());
            stmt.setString(6, participant.getEmail());
            stmt.setString(7, participant.getImagePath());

            int rows = stmt.executeUpdate();
            System.out.println("Participant added: " + participant);
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding participant:");
            e.printStackTrace();
            return false;
        }
    }

    // Search a participant by Registration ID
    public Participant searchParticipant(String registrationId) {
        String sql = "SELECT * FROM Participants WHERE RegistrationID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, registrationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Participant(
                    rs.getString("RegistrationID"),
                    rs.getString("StudentName"),
                    rs.getString("Faculty"),
                    rs.getString("ProjectTitle"),
                    rs.getString("ContactNumber"),
                    rs.getString("Email"),
                    rs.getString("ImagePath")
                );
            }
        } catch (SQLException ex) {
            System.out.println("Error searching participant:");
            ex.printStackTrace();
        }
        return null;
    }

    // Update a participant
    public boolean updateParticipant(Participant participant) {
        String sql = "UPDATE Participants SET StudentName = ?, Faculty = ?, ProjectTitle = ?, ContactNumber = ?, Email = ?, ImagePath = ? WHERE RegistrationID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, participant.getName());
            stmt.setString(2, participant.getFaculty());
            stmt.setString(3, participant.getProjectTitle());
            stmt.setString(4, participant.getContact());
            stmt.setString(5, participant.getEmail());
            stmt.setString(6, participant.getImagePath());
            stmt.setString(7, participant.getRegistrationId());

            int rows = stmt.executeUpdate();
            System.out.println("Participant updated: " + participant);
            return rows > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating participant:");
            ex.printStackTrace();
            return false;
        }
    }

    // Delete a participant
    public boolean deleteParticipant(String registrationId) {
        String sql = "DELETE FROM Participants WHERE RegistrationID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, registrationId);

            int rows = stmt.executeUpdate();
            System.out.println("Participant deleted with Registration ID: " + registrationId);
            return rows > 0;
        } catch (SQLException ex) {
            System.out.println("Error deleting participant:");
            ex.printStackTrace();
            return false;
        }
    }

    // Close the connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
