import javafx.scene.control.Label;
import javafx.stage.Stage;


/**
 *
 * @author Josh Wein
 */
public class Tooltip extends Stage{
    String tip;
    Tooltip (String tip) {
        this.tip = tip;
        this.setWidth(50);
        this.setHeight(30);
        Label label = new Label(tip);
    }
}
