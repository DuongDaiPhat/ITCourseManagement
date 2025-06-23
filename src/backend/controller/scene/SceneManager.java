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
	private static Object sharedData;
	private static boolean isMaximized = false;

	public static void setPrimaryStage(Stage stage) {
		primaryStage = stage;
		primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
			isMaximized = newValue;
		});
	}

	public static void switchScene(String sceneName, String fxmlPath) {
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
			}

			primaryStage.setTitle(sceneName);
			primaryStage.setScene(scene);
			primaryStage.show();

			if (isMaximized) {
				primaryStage.setMaximized(true);
			}

			preserveWindowState();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> void switchScene(String sceneName, String fxmlPath, T data) {
		sharedData = data;
		switchScene(sceneName, fxmlPath);
	}

	public static Object getData() {
		return sharedData;
	}

	public static <T> void switchSceneReloadWithData(String sceneName, String fxmlPath, ControllerDataSetter<T> setter,
			T data) {
		try {
			Scene currentScene = primaryStage.getScene();
			sceneCache.remove(sceneName);
			if (currentScene != null) {
				sceneStack.push(currentScene);
			}

			FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
			Parent root = loader.load();

			Object controller = loader.getController();
			if (setter != null && data != null) {
				setter.setData(controller, data);
			}

			Scene scene = new Scene(root);
			primaryStage.setTitle(sceneName);
			primaryStage.setScene(scene);
			primaryStage.show();

			if (isMaximized) {
				primaryStage.setMaximized(true);
			}

			preserveWindowState();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void goBack() {
		if (!sceneStack.isEmpty()) {
			Scene previousScene = sceneStack.pop();
			primaryStage.setScene(previousScene);
			primaryStage.show();

			if (isMaximized) {
				primaryStage.setMaximized(true);
			}

			preserveWindowState();
		} else {
			System.out.println("Không có scene trước để quay lại.");
		}
	}

	public static void clearSceneCache() {
		sceneCache.clear();
	}

	public static <T> void switchSceneWithData(String sceneName, String fxmlPath, ControllerDataSetter<T> setter,
			T data) {
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

			if (isMaximized) {
				primaryStage.setMaximized(true);
			}

			preserveWindowState();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void preserveWindowState() {
		if (isMaximized) {
			Platform.runLater(() -> {
				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
				primaryStage.setMaximized(true);

				Platform.runLater(() -> {
					primaryStage.setX(screenBounds.getMinX());
					primaryStage.setY(screenBounds.getMinY());
					primaryStage.setWidth(screenBounds.getWidth());
					primaryStage.setHeight(screenBounds.getHeight());
					primaryStage.setMaximized(true);

					new Thread(() -> {
						try {
							Thread.sleep(100);
							Platform.runLater(() -> {
								if (!primaryStage.isMaximized()
										|| Math.abs(primaryStage.getWidth() - screenBounds.getWidth()) > 5
										|| Math.abs(primaryStage.getHeight() - screenBounds.getHeight()) > 5) {

									primaryStage.setX(screenBounds.getMinX());
									primaryStage.setY(screenBounds.getMinY());
									primaryStage.setWidth(screenBounds.getWidth());
									primaryStage.setHeight(screenBounds.getHeight());
									primaryStage.setMaximized(true);
								}
							});
						} catch (InterruptedException e) {
						}
					}).start();
				});
			});
		} else {
			Platform.runLater(() -> {
				Rectangle2D rec = Screen.getPrimary().getVisualBounds();
				primaryStage.setX((rec.getWidth() - primaryStage.getWidth()) / 2);
				primaryStage.setY((rec.getHeight() - primaryStage.getHeight()) / 2);
			});
		}
	}

	public static void switchSceneWithRefresh(String sceneName, String fxmlPath) {
		try {
			Scene currentScene = primaryStage.getScene();
			if (currentScene != null) {
				sceneStack.push(currentScene);
			}

			sceneCache.remove(sceneName);

			FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
			Parent root = loader.load();
			Object controller = loader.getController();

			if (controller != null) {
				try {
					controller.getClass().getMethod("refreshPageData").invoke(controller);
					System.out.println("Called refreshPageData on " + controller.getClass().getSimpleName());
				} catch (Exception e) {
					System.out.println("No refreshPageData method found on " + controller.getClass().getSimpleName());
				}
			}

			Scene scene = new Scene(root);
			sceneCache.put(sceneName, scene);

			primaryStage.setTitle(sceneName);
			primaryStage.setScene(scene);
			primaryStage.show();

			if (isMaximized) {
				primaryStage.setMaximized(true);
			}

			preserveWindowState();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}