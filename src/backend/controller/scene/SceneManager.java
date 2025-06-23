package backend.controller.scene;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private static boolean isMaximized = false; // Track window maximized state

    // Simple category filter passing mechanism
    private static String pendingCategoryFilter = null;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        
        // Add listener to track when window is maximized/restored
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            isMaximized = newValue;
        });
    }    public static void switchScene(String sceneName, String fxmlPath) {
        // Check if this is a login page (register uses fullscreen)
        boolean isLoginPage = isLoginPage(sceneName, fxmlPath);
        
        if (isLoginPage) {
            // Use special handling for login pages
            switchToLoginScene(sceneName, fxmlPath);
            return;
        }
        
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
                scene = new Scene(root);                sceneCache.put(sceneName, scene);
            }

            primaryStage.setTitle(sceneName);
            primaryStage.setScene(scene);
            
            // Set to fullscreen for non-login pages
            setFullscreenMode();
            
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }    public static <T> void switchSceneReloadWithData(String sceneName, String fxmlPath, ControllerDataSetter<T> setter, T data) {
        // Check if this is a login page (register uses fullscreen)
        boolean isLoginPage = isLoginPage(sceneName, fxmlPath);
        
        if (isLoginPage) {
            // Use special handling for login pages
            switchToLoginScene(sceneName, fxmlPath);
            return;
        }
        
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
            }            Scene scene = new Scene(root);
            // Có thể bỏ nếu không muốn cache lại

            primaryStage.setTitle(sceneName);
            primaryStage.setScene(scene);
            
            // Set to fullscreen for non-login pages
            setFullscreenMode();
            
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }    public static void goBack() {
        if (!sceneStack.isEmpty()) {
            Scene previousScene = sceneStack.pop();
            primaryStage.setScene(previousScene);
            
            // Set to fullscreen for non-login pages (check current scene)
            setFullscreenMode();
            
            primaryStage.show();
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
            }            primaryStage.setTitle(sceneName);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Preserve maximized state immediately after setting scene
            if (isMaximized) {
                primaryStage.setMaximized(true);
            }
            
            preserveWindowState();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }    // Helper method to preserve window state after scene switch
    private static void preserveWindowState() {
        if (isMaximized) {
            // If window was maximized, use multiple attempts to ensure it stays maximized
            Platform.runLater(() -> {
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                
                // Attempt 1: Set maximized immediately
                primaryStage.setMaximized(true);
                
                // Attempt 2: Force exact screen dimensions
                Platform.runLater(() -> {
                    primaryStage.setX(screenBounds.getMinX());
                    primaryStage.setY(screenBounds.getMinY());
                    primaryStage.setWidth(screenBounds.getWidth());
                    primaryStage.setHeight(screenBounds.getHeight());
                    primaryStage.setMaximized(true);
                    
                    // Attempt 3: Final enforcement after a short delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(100); // Wait 100ms
                            Platform.runLater(() -> {
                                if (!primaryStage.isMaximized() || 
                                    Math.abs(primaryStage.getWidth() - screenBounds.getWidth()) > 5 ||
                                    Math.abs(primaryStage.getHeight() - screenBounds.getHeight()) > 5) {
                                    
                                    primaryStage.setX(screenBounds.getMinX());
                                    primaryStage.setY(screenBounds.getMinY());
                                    primaryStage.setWidth(screenBounds.getWidth());
                                    primaryStage.setHeight(screenBounds.getHeight());
                                    primaryStage.setMaximized(true);
                                }
                            });
                        } catch (InterruptedException e) {
                            // Handle interruption
                        }
                    }).start();
                });
            });
        } else {
            // Only center if window is not maximized
            Platform.runLater(() -> {
                Rectangle2D rec = Screen.getPrimary().getVisualBounds();
                primaryStage.setX((rec.getWidth() - primaryStage.getWidth()) / 2);
                primaryStage.setY((rec.getHeight() - primaryStage.getHeight()) / 2);
            });
        }
    }

    /**
     * Switch scene and call refresh method on the target controller
     */
    public static void switchSceneWithRefresh(String sceneName, String fxmlPath) {
        try {
            Scene currentScene = primaryStage.getScene();
            if (currentScene != null) {
                sceneStack.push(currentScene); // Save current scene before switching
            }

            // Always reload the scene to ensure fresh state
            sceneCache.remove(sceneName);
            
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            
            // Call refresh method if the controller has one
            if (controller != null) {
                try {
                    // Use reflection to call refreshPageData method if it exists
                    controller.getClass().getMethod("refreshPageData").invoke(controller);
                    System.out.println("Called refreshPageData on " + controller.getClass().getSimpleName());
                } catch (Exception e) {
                    // Method doesn't exist or failed to call - that's okay
                    System.out.println("No refreshPageData method found on " + controller.getClass().getSimpleName());
                }
            }
            
            Scene scene = new Scene(root);
            sceneCache.put(sceneName, scene);

            primaryStage.setTitle(sceneName);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Preserve maximized state immediately after setting scene
            if (isMaximized) {
                primaryStage.setMaximized(true);
            }
            
            preserveWindowState();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }    /**
     * Switch to login scene with specific window size and reset maximized state
     */
    public static void switchToLoginScene(String sceneName, String fxmlPath) {
        try {
            // Clear maximized state when going to login
            isMaximized = false;
            
            // First, reset the window state before loading new scene
            primaryStage.setMaximized(false);
            primaryStage.setResizable(true);
            
            // Clear any size constraints
            primaryStage.setMinWidth(0);
            primaryStage.setMinHeight(0);
            primaryStage.setMaxWidth(Double.MAX_VALUE);
            primaryStage.setMaxHeight(Double.MAX_VALUE);
            
            // Always reload login scene to ensure fresh state
            sceneCache.remove(sceneName);
            
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Create scene with explicit size
            Scene scene = new Scene(root, 800, 500);
            
            primaryStage.setTitle(sceneName);
            
            // Set window dimensions before setting scene
            primaryStage.setWidth(800);
            primaryStage.setHeight(540); // Add extra height to account for title bar and system decorations
            
            // Set the scene
            primaryStage.setScene(scene);
            
            // Use multiple passes to ensure proper sizing
            Platform.runLater(() -> {
                // First pass: Set basic dimensions
                primaryStage.setMaximized(false);
                primaryStage.setWidth(800);
                primaryStage.setHeight(540);
                
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                double centerX = (screenBounds.getWidth() - 800) / 2;
                double centerY = (screenBounds.getHeight() - 540) / 2;
                
                primaryStage.setX(centerX);
                primaryStage.setY(centerY);
                
                // Second pass: Verify and adjust positioning
                Platform.runLater(() -> {
                    // Ensure the window is not maximized and has correct size
                    primaryStage.setMaximized(false);
                    primaryStage.setWidth(800);
                    primaryStage.setHeight(540);
                    
                    // Ensure window is fully visible within screen bounds
                    double windowX = primaryStage.getX();
                    double windowY = primaryStage.getY();
                    double windowWidth = primaryStage.getWidth();
                    double windowHeight = primaryStage.getHeight();
                    
                    // Adjust X position if needed
                    if (windowX + windowWidth > screenBounds.getMaxX()) {
                        windowX = screenBounds.getMaxX() - windowWidth;
                    }
                    if (windowX < screenBounds.getMinX()) {
                        windowX = screenBounds.getMinX();
                    }
                    
                    // Adjust Y position if needed
                    if (windowY + windowHeight > screenBounds.getMaxY()) {
                        windowY = screenBounds.getMaxY() - windowHeight;
                    }
                    if (windowY < screenBounds.getMinY()) {
                        windowY = screenBounds.getMinY();
                    }
                    
                    primaryStage.setX(windowX);
                    primaryStage.setY(windowY);
                    
                    // Final verification after a short delay
                    Platform.runLater(() -> {
                        if (primaryStage.isMaximized() || 
                            Math.abs(primaryStage.getWidth() - 800) > 10 || 
                            Math.abs(primaryStage.getHeight() - 540) > 10) {
                            
                            primaryStage.setMaximized(false);
                            primaryStage.setWidth(800);
                            primaryStage.setHeight(540);
                            
                            // Re-center if size was adjusted
                            double finalCenterX = (screenBounds.getWidth() - 800) / 2;
                            double finalCenterY = (screenBounds.getHeight() - 540) / 2;
                            primaryStage.setX(finalCenterX);
                            primaryStage.setY(finalCenterY);
                        }
                    });
                });
            });
            
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    /**
     * Check if the scene is a login page (register page should be fullscreen)
     */
    private static boolean isLoginPage(String sceneName, String fxmlPath) {
        if (sceneName == null || fxmlPath == null) {
            return false;
        }
        
        String lowerSceneName = sceneName.toLowerCase();
        String lowerFxmlPath = fxmlPath.toLowerCase();
        
        // Only login pages should use fixed size, register should be fullscreen
        return lowerSceneName.contains("login") ||
               lowerFxmlPath.contains("login");
    }
    
    /**
     * Set window to fullscreen mode
     */
    private static void setFullscreenMode() {
        isMaximized = true;
        primaryStage.setMaximized(true);
        
        Platform.runLater(() -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
            primaryStage.setMaximized(true);
        });
    }

    public static void setPendingCategoryFilter(String category) {
        pendingCategoryFilter = category;
    }

    public static String getPendingCategoryFilter() {
        String filter = pendingCategoryFilter;
        pendingCategoryFilter = null; // Clear after retrieval
        return filter;
    }
}
