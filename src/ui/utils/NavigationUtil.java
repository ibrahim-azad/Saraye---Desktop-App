package ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * NavigationUtil - Utility class for handling screen navigation
 * GRASP Pattern: Low Coupling - Centralizes navigation logic
 */
public class NavigationUtil {

    private static Stage primaryStage;

    /**
     * Set the primary stage for the application
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Navigate to a new screen without passing data
     */
    public static void navigateTo(String fxmlFile) {
        navigateTo(fxmlFile, null);
    }

    /**
     * Navigate to a new screen and pass data to controller
     * 
     * @param fxmlFile - Name of FXML file (e.g., "login.fxml")
     * @param data     - Data to pass to the controller
     */
    public static void navigateTo(String fxmlFile, Object data) {
        try {
            // Load FXML file
            FXMLLoader loader = new FXMLLoader(
                    NavigationUtil.class.getResource("/ui/views/" + fxmlFile));
            Parent root = loader.load();

            // Pass data to controller if available
            if (data != null) {
                Object controller = loader.getController();

                // Try to call setData method if it exists
                try {
                    controller.getClass()
                            .getMethod("setData", Object.class)
                            .invoke(controller, data);
                } catch (NoSuchMethodException e) {
                    // Try setUser method
                    try {
                        controller.getClass()
                                .getMethod("setUser", Object.class)
                                .invoke(controller, data);
                    } catch (Exception ex) {
                        // Controller doesn't have data setter, ignore
                        System.out.println("Warning: Controller has no setData/setUser method");
                    }
                }
            }

            // Set scene and show
            Scene scene = new Scene(root);

            // Load CSS if available
            try {
                String css = NavigationUtil.class.getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.out.println("CSS file not found, using default styling");
            }

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error",
                    "Could not load screen: " + fxmlFile + "\nError: " + e.getMessage());
        }
    }

    /**
     * Navigate with multiple data objects
     */
    public static void navigateWithMultipleData(String fxmlFile, Object... dataObjects) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    NavigationUtil.class.getResource("/ui/views/" + fxmlFile));
            Parent root = loader.load();

            // Pass multiple data objects
            if (dataObjects != null && dataObjects.length > 0) {
                Object controller = loader.getController();

                // Handle first object (usually User)
                if (dataObjects.length >= 1 && dataObjects[0] != null) {
                    try {
                        controller.getClass()
                                .getMethod("setUser", Object.class)
                                .invoke(controller, dataObjects[0]);
                    } catch (NoSuchMethodException e) {
                        System.out.println("Warning: Controller doesn't have setUser method");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Handle second object (usually Property or other data)
                if (dataObjects.length >= 2 && dataObjects[1] != null) {
                    try {
                        controller.getClass()
                                .getMethod("setData", Object.class)
                                .invoke(controller, dataObjects[1]);
                    } catch (NoSuchMethodException e) {
                        System.out.println("Warning: Controller doesn't have setData method");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Scene scene = new Scene(root);

            // Load CSS
            try {
                String css = NavigationUtil.class.getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                // CSS not found
            }

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error", "Could not load screen: " + fxmlFile);
        }
    }
}
