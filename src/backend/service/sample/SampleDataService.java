package backend.service.sample;

import backend.service.lecture.LectureService;
import model.lecture.Lecture;

import java.util.ArrayList;
import java.util.List;

public class SampleDataService {
    
    private LectureService lectureService;
    
    public SampleDataService() {
        this.lectureService = new LectureService();
    }
    
    // Create sample lectures for courses
    public void createSampleLectures() {
        List<SampleLectureData> sampleLectures = getSampleLectureData();
        
        for (SampleLectureData lectureData : sampleLectures) {
            Lecture lecture = new Lecture();
            lecture.setLectureName(lectureData.lectureName);
            lecture.setCourseID(lectureData.courseID);
            lecture.setVideoURL(lectureData.videoURL);
            lecture.setDuration(lectureData.duration);
            lecture.setLectureDescription(lectureData.description);
            
            boolean success = lectureService.addLecture(lecture);
            if (success) {
                System.out.println("Sample lecture created: " + lectureData.lectureName);
            } else {
                System.err.println("Failed to create sample lecture: " + lectureData.lectureName);
            }
        }
    }
    
    // Check if we need to create sample lectures (if no lectures exist for sample courses)
    public boolean needsampleLectures() {
        // Check if any lectures exist for course IDs 1-12 (our sample courses)
        for (int courseId = 1; courseId <= 12; courseId++) {
            ArrayList<Lecture> lectures = lectureService.getLecturesByCourseID(courseId);
            if (!lectures.isEmpty()) {
                return false; // Found lectures, no need to create samples
            }
        }
        return true; // No lectures found, create samples
    }
    
    private List<SampleLectureData> getSampleLectureData() {
        List<SampleLectureData> lectures = new ArrayList<>();
          // Course 1: Introduction to Machine Learning
        lectures.add(new SampleLectureData(1, "What is Machine Learning?", 
            "Introduction to the fundamentals of machine learning and its applications", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4", (short) 15));
        lectures.add(new SampleLectureData(1, "Types of Machine Learning", 
            "Supervised, Unsupervised, and Reinforcement Learning explained", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4", (short) 20));
        lectures.add(new SampleLectureData(1, "Linear Regression", 
            "Understanding and implementing linear regression algorithms", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4", (short) 25));
          // Course 2: Web Development Bootcamp
        lectures.add(new SampleLectureData(2, "HTML Fundamentals", 
            "Introduction to HTML structure and basic tags", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4", (short) 18));
        lectures.add(new SampleLectureData(2, "CSS Styling", 
            "Styling web pages with CSS properties and selectors", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4", (short) 22));
        lectures.add(new SampleLectureData(2, "JavaScript Basics", 
            "Introduction to JavaScript programming for web development", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4", (short) 30));
        lectures.add(new SampleLectureData(2, "Responsive Design", 
            "Creating responsive layouts with CSS Grid and Flexbox", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4", (short) 28));
          // Course 3: Python for Beginners
        lectures.add(new SampleLectureData(3, "Python Installation and Setup", 
            "Setting up Python development environment", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4", (short) 12));
        lectures.add(new SampleLectureData(3, "Variables and Data Types", 
            "Understanding Python variables, strings, numbers, and booleans", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4", (short) 16));
        lectures.add(new SampleLectureData(3, "Control Structures", 
            "If statements, loops, and conditional logic in Python", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4", (short) 24));
        lectures.add(new SampleLectureData(3, "Functions and Modules", 
            "Creating reusable code with functions and importing modules", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4", (short) 20));
          // Course 4: Advanced Java Programming
        lectures.add(new SampleLectureData(4, "Object-Oriented Programming", 
            "Deep dive into OOP concepts: inheritance, polymorphism, encapsulation", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4", (short) 35));
        lectures.add(new SampleLectureData(4, "Collections Framework", 
            "Working with ArrayList, HashMap, and other Java collections", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4", (short) 28));
        lectures.add(new SampleLectureData(4, "Exception Handling", 
            "Understanding try-catch blocks and custom exceptions", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4", (short) 22));
        lectures.add(new SampleLectureData(4, "Multithreading", 
            "Concurrent programming and thread management in Java", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4", (short) 32));
          // Course 5: Data Science Fundamentals
        lectures.add(new SampleLectureData(5, "Introduction to Data Science", 
            "Overview of data science workflow and methodologies", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4", (short) 18));        lectures.add(new SampleLectureData(5, "Data Cleaning and Preprocessing", 
            "Techniques for preparing raw data for analysis", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4", (short) 26));
        lectures.add(new SampleLectureData(5, "Statistical Analysis", 
            "Introduction to descriptive and inferential statistics", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4", (short) 30));
        
        // Course 6: Mobile App Development
        lectures.add(new SampleLectureData(6, "Introduction to Android Development", 
            "Setting up Android Studio and creating your first app", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4", (short) 20));
        lectures.add(new SampleLectureData(6, "UI Design with XML", 
            "Creating user interfaces using Android XML layouts", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4", (short) 25));
        lectures.add(new SampleLectureData(6, "Activities and Intents", 
            "Managing app screens and navigation between activities", 
            "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4", (short) 22));
        
        return lectures;
    }
    
    // Helper class to hold sample lecture data
    private static class SampleLectureData {
        int courseID;
        String lectureName;
        String description;
        String videoURL;
        short duration;
        
        SampleLectureData(int courseID, String lectureName, String description, String videoURL, short duration) {
            this.courseID = courseID;
            this.lectureName = lectureName;
            this.description = description;
            this.videoURL = videoURL;
            this.duration = duration;
        }
    }
}
