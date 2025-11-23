import javafx.application.Application;
import javafx.stage.Stage;
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

            // Set primary stage for navigation
            NavigationUtil.setPrimaryStage(primaryStage);

            // Set minimum window size
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