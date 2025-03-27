package model.lecture;

public enum LectureStatus {
    in_progress("in-progress"), 
    done("done"), 
    unfinished("unfinished");

    private final String value;

    LectureStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LectureStatus fromString(String value) {
        for (LectureStatus status : LectureStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.value;
    }
}