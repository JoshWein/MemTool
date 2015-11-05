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
import javafx.stage.Stage;

/**
 *
 * @author josh
 */
public class Gui extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = fxmlLoader.load();
        FXMLDocumentController controller = fxmlLoader.<FXMLDocumentController>getController();
        controller.setStage(stage);
        Scene scene = new Scene(root);
        scene.widthProperty().addListener(new ChangeListener(){
        @Override public void changed(ObservableValue o,Object oldVal, 
                 Object newVal){
             controller.refresh();
        }
      });
        scene.heightProperty().addListener(new ChangeListener(){
        @Override public void changed(ObservableValue o,Object oldVal, 
                 Object newVal){
             controller.refresh();
        }
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
