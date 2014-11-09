package ru.kpfu.ildar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ru.kpfu.ildar.filessource.FileSystemSource;
import ru.kpfu.ildar.filessource.FilesSource;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    private FileTable table1;
    @FXML
    private FileTable table2;

    private FilesSource filesSource = new FileSystemSource();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    @FXML
    private void exitClicked(ActionEvent actionEvent) { }

    @FXML
    public void freqVisFoldersClicked(ActionEvent actionEvent)
    {

    }
}
