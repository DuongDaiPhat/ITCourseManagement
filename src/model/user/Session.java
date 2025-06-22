package model.user;

import model.course.Courses;

public class Session {
	private static Users currentUser;
    private static Courses currentCourse;
    private static boolean isPurchasedCourse = false;

    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    public static Users getCurrentUser() {
        return currentUser;
    }
    
    public static void setCurrentCourse(Courses course, boolean isPurchased) {
        currentCourse = course;
        isPurchasedCourse = isPurchased;
    }
    
    public static Courses getCurrentCourse() {
        return currentCourse;
    }
    
    public static boolean isCurrentCoursePurchased() {
        return isPurchasedCourse;
    }
    
    public static void clearCurrentCourse() {
        currentCourse = null;
        isPurchasedCourse = false;
    }
    
    public static void clearCurrentUser() {
        currentUser = null;
        clearCurrentCourse();
    }
}
