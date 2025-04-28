package model.course;

import model.user.Users;

public class CourseSession {
	private static Courses currentCourse;

    public static void setCurrentCourse(Courses course) {
    	currentCourse = course;
    }

    public static Courses getCurrentCourse() {
        return currentCourse;
    }
}
