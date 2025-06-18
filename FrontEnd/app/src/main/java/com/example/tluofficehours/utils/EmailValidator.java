package com.example.tluofficehours.utils;

public class EmailValidator {
    public static boolean isValidTLUEmail(String email) {
        return email != null && (email.endsWith("@e.tlu.edu.vn") || email.endsWith("@tlu.edu.vn"));
    }
    
    public static String detectUserType(String email) {
        if (email == null) {
            return "unknown";
        }
        
        // Sinh viên: @e.tlu.edu.vn
        if (email.endsWith("@e.tlu.edu.vn")) {
            return "student";
        }
        // Giảng viên: @tlu.edu.vn
        else if (email.endsWith("@tlu.edu.vn")) {
            return "faculty";
        }
        else {
            return "unknown";
        }
    }
    
    public static boolean isStudentEmail(String email) {
        return "student".equals(detectUserType(email));
    }
    
    public static boolean isFacultyEmail(String email) {
        return "faculty".equals(detectUserType(email));
    }
}