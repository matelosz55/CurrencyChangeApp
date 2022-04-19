package dao;
import entity.User;
import util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class UserDao {


    // ADD MONEY TO ACCOUNT - QUERIES
    private static final String ADD_EURO_TO_ACC = "UPDATE users SET balanceEURO = balanceEURO + ? WHERE id = ?";
    private static final String ADD_USD_TO_ACC = "UPDATE users SET balanceUSD = balanceUSD + ? WHERE id = ?";
    private static final String ADD_PLN_TO_ACC = "UPDATE users SET balancePLN = balancePLN + ? WHERE id = ?";
    //SUBSTRACT MONEY FROM ACCOUNT - QUERIES
    private static final String SUBSTRACT_EURO_FROM_ACC = "UPDATE users SET balanceEURO = balanceEURO - ? WHERE id = ?";
    private static final String SUBSTRACT_USD_FROM_ACC = "UPDATE users SET balanceUSD = balanceUSD - ? WHERE id = ?";
    private static final String SUBSTRACT_PLN_FROM_ACC = "UPDATE users SET balancePLN = balancePLN - ? WHERE id = ?";
    //EXCHANGE MONEY - QUERIES
    private static final String EXCHANGE_PLN_EURO = "UPDATE users SET balancePLN = balancePLN - ? , balanceEURO = balanceEURO + ? WHERE id = ?";
    private static final String EXCHANGE_EURO_PLN = "UPDATE users SET balanceEURO = balanceEURO - ? , balancePLN = balancePLN + ? WHERE id = ?";
    private static final String EXCHANGE_PLN_USD = "UPDATE users SET balancePLN = balancePLN - ? , balanceUSD = balanceUSD + ? WHERE id = ?";
    private static final String EXCHANGE_USD_PLN = "UPDATE users SET balanceUSD = balanceUSD - ? , balancePLN = balancePLN + ? WHERE id = ?";
    private static final String EXCHANGE_USD_EURO = "UPDATE users SET balanceUSD = balanceUSD - ? , balanceEURO = balanceEURO + ? WHERE id = ?";
    private static final String EXCHANGE_EURO_USD = "UPDATE users SET balanceEURO = balanceEURO - ? , balanceUSD = balanceUSD + ? WHERE id = ?";
    //USERS CRUD - QUERIES
    private static final String CREATE_USER_QUERY = "INSERT INTO users(username, email, accountNumber, password, balanceEURO, balanceUSD, balancePLN) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_QUERY = "SELECT username, email, accountNumber, balanceEURO, balanceUSD, balancePLN FROM users WHERE id = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String SELECT_USERS_QUERY = "SELECT id, username, password, email FROM users";
    //UPDATE TRANSFER HISTORY - QUERIES
    private static final String TRANSFER_EURO =
            "INSERT INTO transfersHistory(from_user_id, to_user_id ,commission, currency, amount_from,amount_to) VALUES (?,?,?,?,?,?)";
    private static final String TRANSFER_USD =
            "INSERT INTO transfersHistory(from_user_id, to_user_id ,commission, currency, amount_from,amount_to) VALUES (?,?,?,?,?,?)";
    private static final String TRANSFER_PLN =
            "INSERT INTO transfersHistory(from_user_id, to_user_id ,commission, currency, amount_from,amount_to) VALUES (?,?,?,?,?,?)";
    private static final String OPERATION_EXCHANGE = "INSERT INTO transfersHistory (from_user_id, to_user_id ,commission, currency, amount_from,amount_to) VALUES (?,?,?,?,?,?)";
    //UPDATE BANK ACCOUNT - QUERIES
    private static final String UPDATE_BANK_ACCOUNT = "INSERT INTO bank(EURO,USD,PLN) VALUES(?,?,?)";

//      CREATE NEW USER AND ADD MONEY TO THE POCKET
    public User create(User user) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, user.getUserName());
                statement.setString(2, user.getEmail());
                statement.setLong(3, user.getAccountNumber());
                statement.setString(4, user.getHashedPassword());
                statement.setDouble(5, user.getBalanceEURO());
                statement.setDouble(6, user.getBalanceUSD());
                statement.setDouble(7, user.getBalancePLN());
                statement.executeUpdate();

                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    long id = rs.getLong(1);
                    user.setId(id);
                }

                return user;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }


//      DISPLAY SPECIFIC USER
    public User read(long userId) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_QUERY)) {
                statement.setLong(1, userId);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    User user = new User();
                    user.setId(userId);
                    user.setUserName(rs.getString("username"));
                    user.setHashedPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void update(User user) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_QUERY)) {
                statement.setString(1, user.getUserName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getHashedPassword());
                statement.setLong(4, user.getId());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void delete(long userId) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_USER_QUERY)) {
                statement.setLong(1, userId);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public User[] findAll() {
        User[] users = {};
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_USERS_QUERY)) {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUserName(rs.getString("username"));
                    user.setHashedPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    users = addToArray(user, users);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return users;
    }

    private User[] addToArray(User user, User[] users) {
        User[] copy = Arrays.copyOf(users, users.length + 1);
        copy[users.length] = user;
        return copy;
    }
}
