import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * SPOTDashboardApp - Main JavaFX Application Entry Point
 * 
 * SPOT: Smart Parking Optimization for Tenants
 * A modern parking management system demonstrating:
 * - HashMap for O(1) slot lookup
 * - Queue for FIFO waiting list
 * - ArrayList for dynamic storage
 * - JavaFX with FXML and CSS styling
 * - Clean MVC architecture
 */
public class SPOTDashboardApp extends Application {
    
    private static final String APP_TITLE = "SPOT - Smart Parking System";
    private static final String FXML_FILE = "SPOTDashboard.fxml";
    private static final String CSS_FILE = "SPOTDashboard.css";
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 800;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE));
        Parent root = loader.load();
        
        // Create scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Apply CSS stylesheet
        String css = getClass().getResource(CSS_FILE).toExternalForm();
        scene.getStylesheets().add(css);
        
        // Configure primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        
        // Apply window style
        primaryStage.initStyle(StageStyle.DECORATED);
        
        // Show window
        primaryStage.show();
        
        // Log startup
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║        SPOT - Smart Parking System v1.0        ║");
        System.out.println("║     Data Structures: HashMap, Queue, ArrayList ║");
        System.out.println("║         Technology: JavaFX with FXML & CSS     ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
