import dao.UserDao;
import entity.User;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
      /*  System.out.println("Hello in Crust bank!");
        System.out.println("What do you want to do?");
        System.out.println("0 - create");
        String operation = "";
        User user = new User();
        Scanner scanner = new Scanner(System.in);

       */

        User user = new User();
        user.setUserName("anna4");
        user.setEmail("jan.anowak1@onet.pl");
        user.setAccountNumber(20700001);
        user.setPassword("asdsd");

        UserDao userDao = new UserDao();
        //userDao.create(user);

        //System.out.println(userDao.read(1));
        //userDao.update(1,"testCipy11","testCipy1@gmail.com","pussytest");
       //userDao.delete(3);
        userDao.addMoney(5,"PLN",100,0.05);
        userDao.addMoney(5,"USD",100,0.05);
        userDao.addMoney(5,"EURO",100,0.05);




    }
}
