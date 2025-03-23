package entity;

import java.util.Date;

public class MyLearning {
    private int userId;
    private int courseId;
    private CourseStatus courseStatus;
    private Date lastAccessedAt;

    public MyLearning(int userId, int courseId, CourseStatus courseStatus, Date lastAccessedAt) {
        this.userId = userId;
        this.courseId = courseId;
        this.courseStatus = courseStatus;
        this.lastAccessedAt = lastAccessedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public CourseStatus getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(CourseStatus courseStatus) {
        this.courseStatus = courseStatus;
    }

    public Date getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Date lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
}
