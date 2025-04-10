package com.aen.spaceship_fights.database;

import com.aen.spaceship_fights.utils.Utils;
import javafx.util.Pair;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Random;
import java.util.logging.Logger;

public class Db {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private Connection con;
    public Db(){
        con = ConnectDB.getDBConnection();
    }

    private String hashUserPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private String generateUsername(String fistName, String lastName) {
        String baseUsername = (fistName.trim() + "_" + lastName.trim()).toLowerCase();


        Random random = new Random();
        int randomInt = 1000 + random.nextInt(9000);

        return baseUsername + randomInt;
    }

    private boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    };


    public void loadUserTable() {
        String query = "CREATE TABLE IF NOT EXISTS user (";
        query += "id INT AUTO_INCREMENT PRIMARY KEY,";
        query += "email VARCHAR(255) NOT NULL UNIQUE,";
        query += "first_name VARCHAR(255) NOT NULL,";
        query += "last_name VARCHAR(255) NOT NULL,";
        query += "username VARCHAR(255) NOT NULL,";
        query += "password VARCHAR(255) NOT NULL,";
        query += "last_login DATETIME DEFAULT CURRENT_TIMESTAMP,";
        query += "is_verified TINYINT(1) DEFAULT 0,";
        query += "reset_password_token VARCHAR(255),";
        query += "reset_password_expiresAt DATETIME,";
        query += "verification_token VARCHAR(255),";
        query += "verification_token_expiresAt DATETIME,";
        query += "accountType ENUM('ADMIN', 'CLIENT') NOT NULL DEFAULT 'CLIENT',";
        query += "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,";
        query += "updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";
        query += ");";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.executeUpdate();
            logger.info("User Table Instantiated Successfully");
        } catch (SQLException e) {
            logger.info(e.toString());
        }
    }


    public boolean createNewUser(String firstName, String lastName, String email, String password)  {
        boolean success = true;
        String query = "INSERT INTO user (first_name, last_name, email, password, username, verification_token, verification_token_expiresAt) VALUES (?, ?, ?, ?, ?, ?, ?)";


        String hashedPassword = hashUserPassword(password);

        Utils utils = new Utils();
        String verificationToken = utils.generateVerificationToken();


        String username = generateUsername(firstName, lastName);


        java.sql.Timestamp verificationTokenExpiresAt = utils.getTimestamp(15);


        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, hashedPassword);
            statement.setString(5, username);
            statement.setString(6, verificationToken);
            statement.setTimestamp(7, verificationTokenExpiresAt);


            statement.executeUpdate();
            logger.info("User inserted successfully");


            String body = "Hello " + firstName + " " + lastName + ",\n\n" +
                    "Thank you for registering with our service. Please use the following verification code to activate your account:\n\n" +
                    "Verification Code: " + verificationToken + "\n\n" +
                    "The code is valid for 15 minutes. If you did not request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "The Team";


            utils.sendEmail(email, "Account Verification", body);




        } catch (SQLException e) {
            logger.severe("Error inserting user: " + e.toString());
            success = false;
        }

        return success;
    }

    public String getUsernameByEmail(String email) {
        String username = null;
        String query = "SELECT username FROM user WHERE email = ?";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    username = resultSet.getString("username");
                }
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving username: " + e.toString());
        }

        return username;
    }

    public boolean createHighScoresTable() {
        String createTableSQL = """
        CREATE TABLE IF NOT EXISTS high_scores (
            id INT AUTO_INCREMENT PRIMARY KEY,
            email VARCHAR(255) NOT NULL,
            score INT NOT NULL,
            date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (email) REFERENCES user(email)
        );
    """;

        boolean success = true;

        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(createTableSQL);
            logger.info("High scores table created successfully.");
        } catch (SQLException e) {
            logger.severe("Error creating high scores table: " + e.toString());
            success = false;
        }

        return success;
    }


    public Pair<String, Timestamp> getUserVerificationToken(String email) {

        String query = "SELECT verification_token, verification_token_expiresAt FROM user WHERE email = ?";
        Pair<String, Timestamp> result = null;

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, email);


            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String token = resultSet.getString("verification_token");
                    Timestamp expiresAt = resultSet.getTimestamp("verification_token_expiresAt");


                    result = new Pair<>(token, expiresAt);
                } else {
                    logger.severe("No user found with email: " + email);
                }
            }
        } catch (SQLException e) {
            logger.severe("SQL Exception: " + e.toString());
        }

        return result;
    }

    public void updateUser(String email, String firstName, String lastName, String password, String username) throws SQLException {

        StringBuilder query = new StringBuilder("UPDATE user SET ");
        boolean isFirstNameUpdated = false;
        boolean isLastNameUpdated = false;
        boolean isPasswordUpdated = false;
        boolean isUsernameUpdated = false;


        if (firstName != null && !firstName.trim().isEmpty()) {
            query.append("first_name = ?, ");
            isFirstNameUpdated = true;
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            query.append("last_name = ?, ");
            isLastNameUpdated = true;
        }
        if (password != null && !password.trim().isEmpty()) {
            query.append("password = ?, ");
            isPasswordUpdated = true;
        }
        if (username != null && !username.trim().isEmpty()) {
            query.append("username = ?, ");
            isUsernameUpdated = true;
        }


        if (!isFirstNameUpdated && !isLastNameUpdated && !isPasswordUpdated && !isUsernameUpdated) {
            logger.warning("No fields to update.");
            return;
        }


        query.setLength(query.length() - 2);


        query.append(" WHERE email = ?");

        try (PreparedStatement statement = con.prepareStatement(query.toString())) {
            int parameterIndex = 1;


            if (isFirstNameUpdated) {
                statement.setString(parameterIndex++, firstName);
            }
            if (isLastNameUpdated) {
                statement.setString(parameterIndex++, lastName);
            }
            if (isPasswordUpdated) {

                statement.setString(parameterIndex++, BCrypt.hashpw(password, BCrypt.gensalt()));
            }
            if (isUsernameUpdated) {
                statement.setString(parameterIndex++, username);
            }


            statement.setString(parameterIndex, email);


            int rowsUpdated = statement.executeUpdate();


            if (rowsUpdated > 0) {
                logger.info("User details updated successfully.");
            } else {
                logger.warning("No user found with the provided email.");
            }

        } catch (SQLException e) {
            logger.severe("Error updating user: " + e.toString());
        }
    }

    public void getUserName(){

    }
    public void verifyEmail(String email) {
        String query = "UPDATE user SET is_verified = true WHERE email = ?";

        try(PreparedStatement statement = con.prepareStatement(query)){
            statement.setString(1, email);
            statement.executeUpdate();
            logger.info("Your email "+email+" has been verified successfully.");
        }catch (SQLException e){
            logger.severe(e.toString());
        }
    }

    public boolean loginUser(String email, String password) {
        String query = "SELECT password FROM user WHERE email = ?";
        boolean isLoggedIn = false;


        try (PreparedStatement statement = con.prepareStatement(query)){
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPasswordHash = resultSet.getString("password");
                    if (BCrypt.checkpw(password, storedPasswordHash)) {
                        isLoggedIn = true;
                        logger.info("User logged in successfully: " + email);
                    }else{

                        logger.warning("Invalid password attempt for email: " + email);
                    }
                }else{

                    logger.warning("No user found with email: " + email);
                }
            }

        } catch (SQLException e) {
            logger.severe(e.toString());
        }
        return isLoggedIn;
    }

}


