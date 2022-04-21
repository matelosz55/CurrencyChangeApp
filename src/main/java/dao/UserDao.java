package dao;
import entity.User;
import org.mindrot.jbcrypt.BCrypt;
import util.DbUtil;

import java.sql.*;
import java.util.Arrays;

public class UserDao {


    // ADD MONEY TO ACCOUNT - QUERY
    private static final String ADD_MONEY_TO_ACCOUNT = "UPDATE users SET balanceEURO = balanceEURO + ?,balanceUSD = balanceUSD + ?, balancePLN = balancePLN + ? WHERE id = ?";
    //SUBSTRACT MONEY FROM ACCOUNT - QUERY
    private static final String SUBSTRACT_MONEY_FROM_ACCOUNT = "UPDATE users SET balanceEURO = balanceEURO - ?,balanceUSD = balanceUSD - ?, balancePLN = balancePLN - ? WHERE id = ?";
    //USERS CRUD - QUERIES
    private static final String CREATE_USER_QUERY = "INSERT INTO users(username, email, accountNumber, password, balanceEURO, balanceUSD, balancePLN) VALUES (?, ?, ?, ?, 0, 0, 0)";
    private static final String SELECT_USER_QUERY = "SELECT username, email, accountNumber, balanceEURO, balanceUSD, balancePLN FROM users WHERE id = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    //UPDATE TRANSFER HISTORY - QUERY
    private static final String OPERATION =
            "INSERT INTO transfersHistory(from_user_id, to_user_id ,commission,amount_from,amount_to,operation_type, currency_from, currency_to) VALUES (?,?,?,?,?,?,?,?)";
    //UPDATE BANK ACCOUNT - QUERIES
    private static final String UPDATE_BANK_ACCOUNT = "INSERT INTO bank(EURO,USD,PLN) VALUES(?,?,?)";
    private static final String SHOW_ALL_MONEY = "select SUM(EURO),SUM(USD),SUM(PLN) from bank";
    //TRANSFER HISTORY FOR ACCOUNT - QUERY
    private static final String TRANSACTION_HISTORY = " SELECT * FROM transfersHistory WHERE from_user_id = ? OR to_user_id = ?";

    //      CREATE NEW USER
    public User create(User user) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, user.getUserName());
                statement.setString(2, user.getEmail());
                statement.setLong(3, user.getAccountNumber());
                statement.setString(4, user.getHashedPassword());
                statement.executeUpdate();
                System.out.println("User added.");

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
    public void read(long userId) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_QUERY)) {
                statement.setLong(1, userId);
                displayMethod(statement);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    //      DISPLAY HISTORY FOR SPECIFIC USER
    public void readHistory(long userId) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(TRANSACTION_HISTORY)) {
                statement.setLong(1, userId);
                statement.setLong(2, userId);
                displayMethod(statement);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }


    //      UPDATE USER DATA
    public void update(long id, String username, String email, String password) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_QUERY)) {
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, hashPassword(password));
                statement.setLong(4, id);
                statement.executeUpdate();
                System.out.println("User data updated.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //      HASH PASSWORD
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    //      REMOVE USER FROM DATABASE
    public void delete(long userId) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_USER_QUERY)) {
                statement.setLong(1, userId);
                statement.executeUpdate();
                System.out.println("User " + userId + " removed.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    //      LIST CURRENT VALUE OF BANK ACCOUNT
    public void showAllMoney() {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SHOW_ALL_MONEY)) {
                displayMethod(statement);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displayMethod(PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rs.getString(i);
                System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
            }
            System.out.println("");
        }
    }

    //      ADD MONEY TO ACCOUNT
    public void addMoney(long id, String currency, double amount, double commission) {
        cashOperations(ADD_MONEY_TO_ACCOUNT, id, currency, (amount - (amount * commission)));
        saveInHistory(id, id, (amount * commission), amount, (amount - (amount * commission)), "External transfer", currency, currency);
        addCommission(currency,amount*commission);
    }

    //      SUBTRACT MONEY FROM ACCOUNT
    public void withDrawMoney(long id, String currency, double amount, double commission) {
        cashOperations(SUBSTRACT_MONEY_FROM_ACCOUNT, id, currency, (amount - (amount * commission)));
        saveInHistory(id, id, (amount * commission), -amount, (amount - (amount * commission)), "Withdrawal", currency, currency);
        addCommission(currency,amount*commission);
    }

    //      EXCHANGE MONEY
    public void exchange(long id, String currencyFrom, String currencyTo, double amount, double commission, double exchangeRate) {
        cashOperations(SUBSTRACT_MONEY_FROM_ACCOUNT, id, currencyFrom, amount);
        cashOperations(ADD_MONEY_TO_ACCOUNT, id, currencyTo, exchangeRate * (amount - (amount * commission)));
        saveInHistory(id,id, (amount * commission), amount, exchangeRate * (amount - (amount * commission)), "Exchange", currencyFrom, currencyTo);
        addCommission(currencyFrom,amount*commission);
    }

    //      UPDATE MONEY METHOD - JUST TO IMPROVE VISIBILITY AND AVOID CODE DUPLICATION
    private void cashOperations(String operationQuery, long userId, String currency, double amount) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(operationQuery)) {
                insertValues(currency, amount, statement);

                statement.setLong(4, userId);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //      OPERATION HISTORY METHOD - JUST TO IMPROVE VISIBILITY AND AVOID CODE DUPLICATION
    private void saveInHistory(long idFrom, long idTo, double commission, double amountFrom, double amountTo, String operationType, String currencyFrom, String currencyTo) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(OPERATION)) {
                statement.setLong(1, idFrom);
                statement.setLong(2, idTo);
                statement.setDouble(3, commission);
                statement.setDouble(4, amountFrom);
                statement.setDouble(5, amountTo);
                statement.setString(6, operationType);
                statement.setString(7, currencyFrom);
                statement.setString(8, currencyTo);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //      ADD COMMISSION TO THE BANK TABLE
    private void addCommission(String currency, double amount) {
        try (Connection connection = DbUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_BANK_ACCOUNT)) {
                insertValues(currency, amount, statement);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //      INSERT MONEY METHOD - JUST TO IMPROVE VISIBILITY AND AVOID CODE DUPLICATION

    private void insertValues(String currency, double amount, PreparedStatement statement) throws SQLException {
        switch (currency) {
            case "PLN":
                statement.setDouble(1, 0);
                statement.setDouble(2, 0);
                statement.setDouble(3, amount);
                break;
            case "EURO":
                statement.setDouble(1, amount);
                statement.setDouble(2, 0);
                statement.setDouble(3, 0);
                break;
            case "USD":
                statement.setDouble(1, 0);
                statement.setDouble(2, amount);
                statement.setDouble(3, 0);
                break;
        }
    }


    //      TAKE ALL CREDENTIALS METHOD - JUST TO IMPROVE VISIBILITY AND AVOID CODE DUPLICATION
    private void takeAllCredentials(ResultSet rs, long id) throws SQLException {
        rs.getString("username");
        rs.getString("email");
        rs.getLong("accountNumber");
    }

    //      TRANSFER MONEY
    public void transferMoney(long idFrom, long idTo, String currency, double amount, double commission) {
        cashOperations(SUBSTRACT_MONEY_FROM_ACCOUNT,idFrom,currency,amount);
        cashOperations(ADD_MONEY_TO_ACCOUNT,idTo,currency,(amount - (amount * commission)));
        saveInHistory(idFrom,idTo,(amount * commission),-amount,(amount - (amount * commission)),"Cash transfer",currency,currency);
        addCommission(currency,amount*commission);
    }

    private User[] addToArray(User user, User[] users) {
        User[] copy = Arrays.copyOf(users, users.length + 1);
        copy[users.length] = user;
        return copy;
    }
}
