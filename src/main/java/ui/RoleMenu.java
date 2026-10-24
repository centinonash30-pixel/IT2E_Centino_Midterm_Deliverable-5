package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class RoleMenu {
    private final String roleLabel;
    private final UserDao userDao;
    private final Scanner scanner;

    public RoleMenu(String roleLabel, UserDao userDao, Scanner scanner) {
        this.roleLabel = roleLabel;
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void show(User user) {
        System.out.println("\n=== " + roleLabel + " MENU ===");
        System.out.println("Welcome, " + user.fullName() + "!");
        System.out.println("(Menu options coming in a later deliverable.)");
        System.out.print("Press Enter to log out...");
        scanner.nextLine();
    }
}