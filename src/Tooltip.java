
//import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Josh
 */
public class Tooltip extends Stage{
    String tip;
    Tooltip (String tip) {
        this.tip = tip;
        this.setWidth(50);
        this.setHeight(30);
        Label label = new Label(tip);
        //this.a
    }
}
