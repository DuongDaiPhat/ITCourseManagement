package entity;

public enum CourseStatus {
    IN_PROGRESS("in-progress"),
    FINISHED("finished");

    private final String value;

    CourseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CourseStatus fromString(String value) {
        for (CourseStatus status : CourseStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}