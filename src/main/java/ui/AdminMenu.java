package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void show(User admin) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== ADMIN MENU (" + admin.fullName() + ") ===");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("0. Logout");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.println("\n--- All System Users ---");
                    printUserTable(userDao.getAllUsers());
                }
                case "2" -> {
                    System.out.println("\n--- Pending Registrations ---");
                    printUserTable(userDao.getPendingUsers());
                }
                case "3" -> approveUser();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void approveUser() {
        List<User> pending = userDao.getPendingUsers();
        System.out.println("\n--- Approve User Account ---");
        printUserTable(pending);
        if (pending.isEmpty()) return;

        while (true) {
            System.out.print("Enter Target User ID to approve (0 to cancel): ");
            int id;
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            if (id == 0) {
                System.out.println("Approval cancelled.");
                return;
            }
            boolean inPending = pending.stream().anyMatch(u -> u.id() == id);
            if (!inPending) {
                System.out.println("ID " + id + " is not in the pending list. Try again.");
                continue;
            }
            if (userDao.approveUser(id)) {
                System.out.println("Success! User ID " + id + " has been APPROVED.");
            } else {
                System.out.println("Approval failed. Please try again.");
            }
            return;
        }
    }

    private void printUserTable(List<User> users) {
        String format = "| %-5s | %-15s | %-15s | %-10s |%n";
        String border = "+-------+-----------------+-----------------+------------+";
        System.out.println(border);
        System.out.printf(format, "ID", "Username", "Role", "Status");
        System.out.println(border);
        if (users.isEmpty()) {
            System.out.printf("| %-50s |%n", "No records found.");
        } else {
            for (User u : users) {
                System.out.printf(format, u.id(), u.username(), u.role(), u.status());
            }
        }
        System.out.println(border);
    }
}