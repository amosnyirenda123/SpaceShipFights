package com.aen.spaceship_fights.utils;

public class Selection {
    private static String planeName = "player";
    private static String username = "Computer";
    private static String notificationRecep = "";

    public static void setPlaneName(String planeName) {
        Selection.planeName = planeName;
    }

    public static String getPlaneName() {
        return planeName;
    }
    public static void setUsername(String username) {
        Selection.username = username;
    }
    public static String getUsername() {
        return username;
    }
    public static void setNotificationRecep(String notificationRecep) {
        Selection.notificationRecep = notificationRecep;
    }
    public static String getNotificationRecep() {
        return notificationRecep;
    }
}
