/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

/**
 *
 * @author 
 */
public class Gui extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = fxmlLoader.load();
        FXMLDocumentController controller = fxmlLoader.<FXMLDocumentController>getController();
        controller.setStage(stage);
        stage.setTitle("Memory Snapshot Visualizer Tool");
        stage.getIcons().add(new Image("icon.png"));
        Scene scene = new Scene(root);
        scene.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            controller.refresh();
        });
        scene.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            controller.refresh();
        });
        
        scene.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
         scene.setOnDragDropped((DragEvent event) -> {
             Dragboard db = event.getDragboard();
             boolean success = false;
             if (db.hasFiles()) {
                 success = true;
                 controller.load(db.getFiles().get(0));
             }
             event.setDropCompleted(success);
             event.consume();
        });
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
