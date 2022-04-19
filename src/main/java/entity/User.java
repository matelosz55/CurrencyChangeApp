package entity;

import org.mindrot.jbcrypt.BCrypt;

public class User {

    private long id;
    private String userName;
    private String email;
    private String hashedPassword;
    private double balancePLN;
    private double balanceUSD;
    private double balanceEURO;
    private long accountNumber;

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBalancePLN() {
        return balancePLN;
    }

    public void setBalancePLN(double balancePLN) {
        this.balancePLN = balancePLN;
    }

    public double getBalanceUSD() {
        return balanceUSD;
    }

    public void setBalanceUSD(double balanceUSD) {
        this.balanceUSD = balanceUSD;
    }

    public double getBalanceEURO() {
        return balanceEURO;
    }

    public void setBalanceEURO(double balanceEURO) {
        this.balanceEURO = balanceEURO;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setPassword(String password) {
        this.hashedPassword = hashPassword(password);
    }

    @Override
    public String toString() {
        return "entity.User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}