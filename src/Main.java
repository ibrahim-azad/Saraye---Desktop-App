import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create UI elements
        Label titleLabel = new Label("🏠 Saraye - Property Rental System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2196F3;");
        
        Label statusLabel = new Label("✅ Setup Successful!");
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        statusLabel.setStyle("-fx-text-fill: #4CAF50;");
        
        Label teamLabel = new Label("Team: Ibrahim, Omer, Raffay");
        teamLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        teamLabel.setStyle("-fx-text-fill: #666;");
        
        // Layout
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, statusLabel, teamLabel);
        root.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 40;");
        
        // Scene and Stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Saraye - Setup Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}