package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Authentication {

    // TODO: replace with the roles from Section 2 of your System Proposal
    private static final List<String> ROLES =
            List.of("ADMIN", "ASSET_MANAGER", "DISTRIBUTOR", "RECIPIENT");

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Exchange Asset & Distribution Management System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> login();
                case "2" -> register();
                case "0" -> { running = false; System.out.println("Goodbye!"); }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void register() {
        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        System.out.println("Select role:");
        for (int i = 0; i < ROLES.size(); i++) {
            System.out.println((i + 1) + ". " + ROLES.get(i));
        }
        System.out.print("Choose: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid role selection.");
            return;
        }
        if (choice < 1 || choice > ROLES.size()) {
            System.out.println("Invalid role selection.");
            return;
        }

        boolean ok = userDao.register(username, UserDao.hash(password),
                fullName, ROLES.get(choice - 1));
        System.out.println(ok
                ? "Registration successful! Your account is PENDING approval."
                : "Username already taken.");
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Optional<User> result = userDao.authenticate(username, UserDao.hash(password));
        if (result.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }
        User user = result.get();
        if (!"APPROVED".equalsIgnoreCase(user.status())) {
            System.out.println("Your account is " + user.status() + ". Please wait for approval.");
            return;
        }
        routeToMenu(user);
    }

    private void routeToMenu(User user) {
        switch (user.role()) {
            case "ADMIN"         -> new AdminMenu(userDao, scanner).show(user);
            case "ASSET_MANAGER" -> new RoleMenu("ASSET MANAGER", userDao, scanner).show(user);
            case "DISTRIBUTOR"   -> new RoleMenu("DISTRIBUTOR", userDao, scanner).show(user);
            case "RECIPIENT"     -> new RoleMenu("RECIPIENT", userDao, scanner).show(user);
            default -> System.out.println("Unknown role: " + user.role());
        }
    }
}