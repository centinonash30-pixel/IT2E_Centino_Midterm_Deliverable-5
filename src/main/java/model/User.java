package com.scc.model;

public record User(int id, String username, String passwordHash,
                   String fullName, String role, String status) {}