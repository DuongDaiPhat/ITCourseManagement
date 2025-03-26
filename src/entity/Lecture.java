package entity;

import java.time.LocalDateTime;

public class Lecture {
    private int lectureID;
    private String lectureName;
    private int courseID;
    private String videoURL;
    private short duration;
    private String lectureDescription;
    
    public Lecture() {
        super();
    }

    public Lecture(int lectureID, String lectureName, int courseID, String videoURL, short duration,
            String lectureDescription) {
        super();
        this.lectureID = lectureID;
        this.lectureName = lectureName;
        this.courseID = courseID;
        this.videoURL = videoURL;
        this.duration = duration;
        this.lectureDescription = lectureDescription;
    }

    public int getLectureID() {
        return lectureID;
    }

    public void setLectureID(int lectureID) {
        this.lectureID = lectureID;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public String getLectureDescription() {
        return lectureDescription;
    }

    public void setLectureDescription(String lectureDescription) {
        this.lectureDescription = lectureDescription;
    }

    @Override
    public String toString() {
        return "Lecture [lectureID=" + lectureID + ", lectureName=" + lectureName + ", courseID=" + courseID
                + ", videoURL=" + videoURL + ", duration=" + duration + ", lectureDescription=" + lectureDescription
                + "]";
    }
    
    public void print() {
        System.out.println(this.toString());
    }
}