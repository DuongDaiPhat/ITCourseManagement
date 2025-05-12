package backend.controller.scene;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import utils.ControllerDataSetter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

public class SceneManager {

    private static final HashMap<String, Scene> sceneCache = new HashMap<>();
    private static final Stack<Scene> sceneStack = new Stack<>();
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String sceneName, String fxmlPath) {
        try {
            Scene currentScene = primaryStage.getScene();
            if (currentScene != null) {
                sceneStack.push(currentScene); // Lưu lại scene hiện tại trước khi chuyển
            }

            Scene scene;
            if (sceneCache.containsKey(sceneName)) {
                scene = sceneCache.get(sceneName);
            } else {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                Parent root = loader.load();
                scene = new Scene(root);
                sceneCache.put(sceneName, scene);
            }

            primaryStage.setTitle(sceneName);
            primaryStage.setScene(scene);
            primaryStage.show();
            Platform.runLater(() -> {
                Rectangle2D rec = Screen.getPrimary().getVisualBounds();
                primaryStage.setX((rec.getWidth() - primaryStage.getWidth()) / 2);
                primaryStage.setY((rec.getHeight() - primaryStage.getHeight()) / 2);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static <T> void switchSceneReloadWithData(String sceneName, String fxmlPath, ControllerDataSetter<T> setter, T data) {
        try {
            Scene currentScene = primaryStage.getScene();

            // Xóa cache để luôn load mới
            sceneCache.remove(sceneName);
            if (currentScene != null) {
            	sceneStack.push(currentScene);
            }

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Lấy controller và truyền dữ liệu nếu có
            Object controller = loader.getController();
            if (setter != null && data != null) {
                setter.setData(controller, data);
            }

            Scene scene = new Scene(root);
            // Có thể bỏ nếu không muốn cache lại

            primaryStage.setTitle(sceneName);
            primaryStage.setScene(scene);
            primaryStage.show();

            Platform.runLater(() -> {
                Rectangle2D rec = Screen.getPrimary().getVisualBounds();
                primaryStage.setX((rec.getWidth() - primaryStage.getWidth()) / 2);
                primaryStage.setY((rec.getHeight() - primaryStage.getHeight()) / 2);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void goBack() {
        if (!sceneStack.isEmpty()) {
            Scene previousScene = sceneStack.pop();
            primaryStage.setScene(previousScene);
            primaryStage.show();
            Platform.runLater(() -> {
                Rectangle2D rec = Screen.getPrimary().getVisualBounds();
                primaryStage.setX((rec.getWidth() - primaryStage.getWidth()) / 2);
                primaryStage.setY((rec.getHeight() - primaryStage.getHeight()) / 2);
            });
        } else {
            System.out.println("Không có scene trước để quay lại.");
        }
    }

    public static void clearSceneCache() {
        sceneCache.clear();
    }

    public static <T> void switchSceneWithData(String sceneName, String fxmlPath, ControllerDataSetter<T> setter, T data) {
        try {
            Scene currentScene = primaryStage.getScene();
            if (currentScene != null) {
                sceneStack.push(currentScene);
            }

            Scene scene;
            if (sceneCache.containsKey(sceneName)) {
                scene = sceneCache.get(sceneName);
            } else {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                Parent root = loader.load();
                scene = new Scene(root);
                sceneCache.put(sceneName, scene);

                Object controller = loader.getController();
                if (setter != null) {
                    setter.setData(controller, data);
                }
            }

            primaryStage.setTitle(sceneName);
            primaryStage.setScene(scene);
            primaryStage.show();
            Platform.runLater(() -> {
                Rectangle2D rec = Screen.getPrimary().getVisualBounds();
                primaryStage.setX((rec.getWidth() - primaryStage.getWidth()) / 2);
                primaryStage.setY((rec.getHeight() - primaryStage.getHeight()) / 2);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
