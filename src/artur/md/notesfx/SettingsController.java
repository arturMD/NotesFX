package artur.md.notesfx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable{
    @FXML private ColorPicker colorPicker;
    @FXML private ColorPicker bgColorPicker;

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        colorPicker.setValue(Color.web(Controller.settings.getFontColor()));
        bgColorPicker.setValue(Color.web(Controller.settings.getBgColor()));
        colorPicker.setOnAction( event -> {
            Controller.settings.setFontColor("#" +  colorPicker.getValue().toString().substring(2));
        });
        bgColorPicker.setOnAction( event -> {
            Controller.settings.setBgColor("#" +  colorPicker.getValue().toString().substring(2));
        });

    }

}

