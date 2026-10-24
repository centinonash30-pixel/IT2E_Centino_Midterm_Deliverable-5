package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();           // 1. Initialize database
        UserDao userDao = new UserDao();            // 2. Single shared DAO
        try (Scanner scanner = new Scanner(System.in)) {
            new Authentication(userDao, scanner).start();   // 3. Delegate
        }
    }
}