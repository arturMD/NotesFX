package artur.md.notesfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Optional;

public class Controller implements Initializable{
    private static final String dirName = "notes";
    private static final String settingsFile = "settings.xml";
    private static final List<File> notes = new ArrayList<>();  //delete it maybe needed only for loading
    private static final List<String> noteNames = new ArrayList<>(); //move as local to loadNotes
    private static final ObservableList<String> listItems = FXCollections.observableArrayList();
    private boolean noteSaved = true;
    private String currentNote;

    static Settings settings;

    @FXML private ListView<String> noteList;
    @FXML TextArea noteContent;


    @FXML
    protected void addNote() {
        TextInputDialog addNoteDialog = new TextInputDialog("New note");
        addNoteDialog.setTitle("New note");
        addNoteDialog.setHeaderText(null);
        addNoteDialog.setContentText("New note name: ");
        Optional<String> result = addNoteDialog.showAndWait();
        result.ifPresent(s -> {
            if (s.matches(".*[\\*\\\\\\?/\"\'].*")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Name Error");
                alert.setHeaderText(null);
                alert.setContentText("Names cannot have any of * \\ ? / \" '");
                alert.showAndWait();
            } else {
                File file = new File(dirName + File.separator + s);
                try {
                    file.createNewFile();
                    notes.add(file);
                    listItems.add(file.getName());
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText(null);
                    alert.setContentText("Unable to create a file" + s + ".");
                }
            }
        });
    }

    @FXML
    private void delNote() {
        int selectedItem = noteList.getSelectionModel().getSelectedIndex();
        String name = listItems.get(selectedItem);
        File file = new File(dirName + File.separator + name);
        if(file.exists()) {
            file.delete();
        }

        listItems.remove(selectedItem);

    }

    @FXML
    private void showInfo() {
        Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
        infoDialog.setTitle("Info");
        infoDialog.setHeaderText(null);
        infoDialog.setContentText("Icons by Plainicon downloaded from www.flaticon.com under CC 3.O BY licence.");
        infoDialog.showAndWait();
    }

    @FXML
    private void exportNote() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(currentNote);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Export to file");
        File file = fileChooser.showSaveDialog(null);
        saveNote(file.getAbsolutePath());

    }

    @FXML
    private void importNote() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Import from file");
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            String name = file.getName();
            int i =1;
            while (noteList.getItems().contains(name) ) {
                name = file.getName() + " (" + i + ")";
                i++;
            }

            try {
                BufferedWriter target = new BufferedWriter(new FileWriter(dirName + File.separator + name));
                BufferedReader source = new BufferedReader(new FileReader(file));
                String str;
                while((str = source.readLine()) != null) {
                    target.write(str + "\n");
                }
                target.close();
                source.close();
                noteList.getItems().add(name);

            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error!");
                alert.setContentText("Problem with file.\n" + ex.getCause());
            }
        }
    }

    @FXML
    private void showSettings() {
        Parent root;
        Stage settingsStage = new Stage();
        try {
            root = FXMLLoader.load(getClass().getResource("Settings.fxml"));
            Scene settingsScene = new Scene(root);
            settingsStage.setTitle("Settings");
            settingsStage.setScene(settingsScene);
            settingsStage.showAndWait();
            noteContent.setStyle("-fx-text-fill: " + settings.getFontColor() + ";" +
                    " -fx-control-inner-background: " + settings.getBgColor() + ";");
            settings.save();
        } catch(IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Input/Output error");
            alert.showAndWait();
        }
    }

    @FXML
    private void saveNote() {
        saveNote(dirName + File.separator + currentNote);
    }

    private void saveNote(String targetName) {
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter(targetName));
            file.write(noteContent.getText());
            file.close();
            noteSaved = true;
        } catch (IOException ex) {
            Alert alert =new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Can't save note.");
            alert.showAndWait();
        }
    }


    private void loadNotes() {
        File dir = new File(dirName);
        if (notes != null) {
            notes.clear();
        }
        if (noteNames != null) {
            noteNames.clear();
        }
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            notes.addAll(Arrays.asList(dir.listFiles()));
            for (File file : notes) {
                noteNames.add(file.getName());
            }
            listItems.setAll(noteNames);

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadNotes();
        noteList.setItems(listItems);

        noteList.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
                if(!noteSaved) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Save changes?");
                    alert.setContentText("Do you want to save changes?");
                    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> result = alert.showAndWait();
                    if((result.isPresent()) && (result.get() == ButtonType.YES)) {
                        saveNote();
                    }
                }
                currentNote = newVal;
                loadNoteFromFile();
            }
        );
        settings = new Settings(settingsFile);
        settings.load();
        noteContent.setStyle("-fx-text-fill: " + settings.getFontColor() +
                "; -fx-control-inner-background: " + settings.getBgColor() + ";");
        noteContent.textProperty().addListener((observable,oldVal,newVal) -> {
            noteSaved = false;
        });
        noteList.getSelectionModel().select(0);
        settings = new Settings("settings.xml");
    }


    /*
    Loads a file contents with name stored in currentNote
     */
    @FXML
    private void loadNoteFromFile() {
        try {
            noteContent.setText("");
            BufferedReader in = new BufferedReader(new FileReader(dirName + File.separator + currentNote));
            String str;
            while((str = in.readLine()) != null) {
                noteContent.appendText(str + "\n");
            }
            noteSaved = true;
        } catch (FileNotFoundException ex) {
            Alert alert =new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("File not found.");
            alert.showAndWait();
        } catch (IOException ex) {
            Alert alert =new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Input/Output error.");
            alert.showAndWait();
        }
    }

}
