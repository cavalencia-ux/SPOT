import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SPOTDashboardProApp - Professional SPOT Application Launcher
 * 
 * SPOT: Smart Parking Optimization for Tenants
 * 
 * Features:
 * - Professional dark theme inspired by GitHub, Figma, VS Code
 * - HashMap for O(1) parking slot lookups
 * - Queue for FIFO waiting list management
 * - ArrayList for dynamic slot storage
 * - Clean MVC architecture with FXML and CSS
 * 
 * Design Philosophy:
 * - Minimal, clean interface
 * - Strong visual hierarchy
 * - Professional typography and spacing
 * - Smooth interactions and feedback
 */
public class SPOTDashboardProApp extends Application {
    
    private static final String APP_TITLE = "SPOT • Smart Parking System";
    private static final String FXML_FILE = "SPOTDashboardPro.fxml";
    private static final String CSS_FILE = "SPOTDashboardPro.css";
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 900;
    private static final int MIN_WIDTH = 1200;
    private static final int MIN_HEIGHT = 700;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE));
        Parent root = loader.load();
        
        // Create scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Apply CSS stylesheet
        String css = getClass().getResource(CSS_FILE).toExternalForm();
        scene.getStylesheets().add(css);
        
        // Configure window
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setResizable(true);
        
        // Display
        primaryStage.show();
        
        // Log startup
        printStartupBanner();
    }
    
    /**
     * Print startup banner to console
     */
    private void printStartupBanner() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                                                        ║");
        System.out.println("║         SPOT • Smart Parking System v1.0               ║");
        System.out.println("║                                                        ║");
        System.out.println("║  Professional JavaFX Dashboard Application            ║");
        System.out.println("║  Data Structures: HashMap, Queue, ArrayList           ║");
        System.out.println("║  Technology: JavaFX + FXML + CSS                      ║");
        System.out.println("║                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
