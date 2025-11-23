import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import ui.utils.NavigationUtil;

/**
 * Main Application Entry Point
 * Saraye - Property Rental Desktop Application
 *
 * Team:
 * - Abdul Raffay (23I-0587) - UI Layer
 * - Ibrahim Azad (23I-3049) - Business Logic Layer
 * - M. Omer Khan (23I-0650) - Database Layer
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Set application title
            primaryStage.setTitle("Saraye - Property Rental Platform");

            // Set application icon
            try {
                Image icon = new Image(getClass().getResourceAsStream("/resources/images/icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Could not load application icon: " + e.getMessage());
            }

            // Set primary stage for navigation
            NavigationUtil.setPrimaryStage(primaryStage);

            // Set fixed window size to prevent resizing between screens
            primaryStage.setWidth(1200);
            primaryStage.setHeight(750);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // Navigate to login screen
            NavigationUtil.navigateTo("login.fxml");

            // Show the stage
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}