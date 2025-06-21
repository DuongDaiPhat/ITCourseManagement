package backend.service.lecture;

import backend.repository.lecture.LectureRepository;
import model.lecture.Lecture;

import java.sql.SQLException;
import java.util.ArrayList;

public class LectureService {
    
    private LectureRepository lectureRepository;
    
    public LectureService() {
        this.lectureRepository = LectureRepository.getInstance();
    }
    
    // Get all lectures for a specific course
    public ArrayList<Lecture> getLecturesByCourseID(int courseID) {
        try {
            String condition = "COURSEID = " + courseID;
            return lectureRepository.SelectByCondition(condition);
        } catch (SQLException e) {
            System.err.println("Error getting lectures for course " + courseID + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // Get a specific lecture by ID
    public Lecture getLectureByID(int lectureID) {
        try {
            return lectureRepository.SelectByID(lectureID);
        } catch (SQLException e) {
            System.err.println("Error getting lecture " + lectureID + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Get all lectures
    public ArrayList<Lecture> getAllLectures() {
        try {
            return lectureRepository.SelectAll();
        } catch (SQLException e) {
            System.err.println("Error getting all lectures: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // Add a new lecture
    public boolean addLecture(Lecture lecture) {
        try {
            int result = lectureRepository.Insert(lecture);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error adding lecture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Update a lecture
    public boolean updateLecture(Lecture lecture) {
        try {
            int result = lectureRepository.Update(lecture);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error updating lecture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
      // Delete a lecture
    public boolean deleteLecture(int lectureID) {
        try {
            // Create a lecture object with the ID to delete
            Lecture lectureToDelete = new Lecture();
            lectureToDelete.setLectureID(lectureID);
            int result = lectureRepository.Delete(lectureToDelete);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting lecture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
