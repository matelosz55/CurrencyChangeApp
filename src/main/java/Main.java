import dao.UserDao;
import entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static void hello() throws SQLException, IOException {
        System.out.println("Hello in Crust bank!");
        System.out.println("Do you want to use regular rates and commission ?   (y/n)");
        Scanner menu = new Scanner(System.in);
        String str = menu.nextLine();
        switch (str) {
            case "y":
                fileWriterDefault();
                menu();
                break;
            case "n":
                fileWriter();
                menu();
                break;
        }
    }

    private static void fileWriter() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("rates.txt");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type commission rate (in %):");
        double commissionRate = Double.parseDouble(scanner.nextLine()) / 100;
        writer.println(commissionRate);
        System.out.println("Type EURO to PLN rate:");
        writer.println(scanner.nextLine());
        System.out.println("Type PLN to EURO rate:");
        writer.println(scanner.nextLine());
        System.out.println("Type PLN to USD rate:");
        writer.println(scanner.nextLine());
        System.out.println("Type USD to USD rate:");
        writer.println(scanner.nextLine());
        System.out.println("Type USD to EURO rate:");
        writer.println(scanner.nextLine());
        System.out.println("Type EURO to USD rate:");
        writer.println(scanner.nextLine());
        writer.close();
    }

    private static void fileWriterDefault() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("rates.txt");
        writer.println(0.05);
        writer.println(4.64);
        writer.println(0.22);
        writer.println(0.23);
        writer.println(4.28);
        writer.println(0.92);
        writer.println(1.08);
        writer.close();
    }

    private static Double[] fileReader() throws IOException {
        Double[] ratesTab = new Double[7];
        int i = 0;
        for (String line : Files.readAllLines(Paths.get("rates.txt"))) {
            ratesTab[i] = Double.parseDouble(line);
            i++;
        }
       return ratesTab;
    }


    private static void menu() throws SQLException, IOException {
        System.out.println("What do you want to do?");
        System.out.println("0 - create a new account                1 - check user balance");
        System.out.println("2 - add money to account                3 - withdraw money");
        System.out.println("4 - transfer to another user            5 - exchange currency");
        System.out.println("6 - check transactions history          7 - check bank balance");
        System.out.println("8 - update user data                    E - EXIT");
        Scanner menu = new Scanner(System.in);
        String str = menu.nextLine();
        switch (str) {
            case "0":
                createUser();
                break;
            case "1":
                checkBalance();
                break;
            case "2":
                addMoney();
                break;
            case "3":
                withDrawal();
                break;
            case "4":
                transfer();
            case "5":
                exchangeCurrency(fileReader()[0],fileReader()[1],fileReader()[2],fileReader()[3],fileReader()[4],fileReader()[5],fileReader()[6]);
                break;
            case "6":
                checkTransactionHistory();
                break;
            case "7":
                bankBalance();
                break;
            case "8":
                updateUserData();
                break;
            case "E":
                break;
        }
    }

    private static void updateUserData() throws SQLException, IOException {
        UserDao userDao = new UserDao();
        Scanner scan = new Scanner(System.in);
        System.out.println("Type id of a user:");
        long id = Long.parseLong(scan.nextLine());
        System.out.println("Type new username:");
        String username = scan.nextLine();
        System.out.println("Type new email:");
        String email = scan.nextLine();
        System.out.println("Type new password:");
        String pass = scan.nextLine();
        userDao.update(id, username, email, pass);
        anyOtherAction();
    }

    private static void bankBalance() throws SQLException, IOException {
        UserDao userDao = new UserDao();
        userDao.showAllMoney();
        anyOtherAction();
    }

    private static void checkTransactionHistory() throws SQLException, IOException {
        UserDao userDao = new UserDao();
        System.out.println("Type user ID:");
        Scanner scan = new Scanner(System.in);
        userDao.readHistory(scan.nextInt());
        anyOtherAction();
    }

    private static void exchangeCurrency(double commissionRate, double euroToPln, double plnToEuro, double usdToPln, double plnToUsd, double usdToEuro, double euroToUsd) throws SQLException, IOException {
        UserDao userDao = new UserDao();
        Scanner scan = new Scanner(System.in);
        System.out.println("Type id of a user");
        long id = Integer.parseInt(scan.nextLine());
        System.out.println("Which currency do you want to exchange? (PLN/EURO/USD");
        String currencyFrom = scan.nextLine();
        System.out.println("Choose new currency: (PLN/EURO/USD");
        String currencyTo = scan.nextLine();
        System.out.println("Type amount of money:");
        double amount = Double.parseDouble(scan.nextLine());
        double rate = 0.00;
        switch (currencyFrom) {
            case "PLN":
                switch (currencyTo) {
                    case "EURO" -> rate = plnToEuro;
                    case "USD" -> rate = plnToUsd;
                }
                break;
            case "EURO":
                rate = switch (currencyTo) {
                    case "PLN" -> euroToPln;
                    case "USD" -> euroToUsd;
                    default -> rate;
                };

            case "USD":
                rate = switch (currencyTo) {
                    case "EURO" -> usdToEuro;
                    case "PLN" -> usdToPln;
                    default -> rate;
                };
                userDao.exchange(id, currencyFrom, currencyTo, amount, commissionRate, rate);
                anyOtherAction();
        }
    }

    private static void transfer() throws SQLException, IOException {
        UserDao userdao = new UserDao();
        Scanner scan = new Scanner(System.in);
        System.out.println("Type id of a user");
        long id = Integer.parseInt(scan.nextLine());
        System.out.println("Type id of a second user");
        long id2 = Integer.parseInt(scan.nextLine());
        System.out.println("Type currency to withdraw: (PLN/USD/EURO)");
        String currency = scan.nextLine();
        System.out.println("Type amount:");
        double amount = Double.parseDouble(scan.nextLine());
        while (amount < 0) {
            System.out.println("Type positive value!");
            amount = Double.parseDouble(scan.nextLine());
        }
        userdao.transferMoney(id, id2, currency, amount, fileReader()[0]);
        anyOtherAction();
    }

    private static void withDrawal() throws SQLException, IOException {
        UserDao userdao = new UserDao();
        Scanner scan = new Scanner(System.in);
        System.out.println("Type id of a user");
        long id = Integer.parseInt(scan.nextLine());
        System.out.println("Type currency to withdraw: (PLN/USD/EURO)");
        String currency = scan.nextLine();
        System.out.println("Type amount:");
        double amount = Double.parseDouble(scan.nextLine());
        while (amount < 0) {
            System.out.println("Type positive value!");
            amount = Double.parseDouble(scan.nextLine());
        }
        userdao.withDrawMoney(id, currency, amount, fileReader()[0]);
        anyOtherAction();
    }

    private static void addMoney() throws SQLException, IOException {
        UserDao userdao = new UserDao();
        Scanner scan = new Scanner(System.in);
        System.out.println("Type id of a user");
        long id = Integer.parseInt(scan.nextLine());
        System.out.println("Type currency to withdraw: (PLN/USD/EURO)");
        String currency = scan.nextLine();
        System.out.println("Type amount:");
        double amount = Double.parseDouble(scan.nextLine());
        while (amount < 0) {
            System.out.println("Type positive value!");
            amount = Double.parseDouble(scan.nextLine());
        }
        userdao.addMoney(id, currency, amount, fileReader()[0]);
        anyOtherAction();
    }

    private static void checkBalance() throws SQLException, IOException {
        UserDao userdao = new UserDao();
        Scanner scan = new Scanner(System.in);
        System.out.println("Type id of a user");
        long id = Integer.parseInt(scan.nextLine());
        userdao.read(id);
        anyOtherAction();
    }

    private static void createUser() throws SQLException, IOException {
        User user = new User();
        System.out.println("Type new username:");
        Scanner scan = new Scanner(System.in);
        user.setUserName(scan.nextLine());
        System.out.println("Type new email:");
        user.setEmail(scan.nextLine());
        user.setAccountNumber(getRandomNumber(100000000, 999999999));
        System.out.println("Type new password:");
        user.setPassword(scan.nextLine());
        UserDao userDao = new UserDao();
        userDao.create(user);
        anyOtherAction();
    }

    private static long getRandomNumber(long min, long max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private static void anyOtherAction() throws SQLException, IOException {
        System.out.println("\nWould you like to perform any other action? (press y/n)");
        Scanner menu = new Scanner(System.in);
        String str = menu.nextLine();
        switch (str) {
            case "y":
                menu();
                break;
            case "n":
                break;
        }
    }

    public static void main(String[] args) throws SQLException, IOException {

        hello();



    }

}
