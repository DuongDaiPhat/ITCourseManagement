package utils;

@FunctionalInterface
public interface ControllerDataSetter<T> {
    void setData(Object controller, T data);
}