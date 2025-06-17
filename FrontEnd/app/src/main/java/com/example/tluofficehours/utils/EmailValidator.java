package com.example.tluofficehours.utils;

public class EmailValidator {
    public static boolean isValidTLUEmail(String email) {
        return email != null && email.endsWith("@e.tlu.edu.vn");
    }
}