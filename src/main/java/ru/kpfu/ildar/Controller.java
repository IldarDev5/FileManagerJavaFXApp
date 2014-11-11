package ru.kpfu.ildar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.controlsfx.dialog.Dialog;
import ru.kpfu.ildar.filessource.FileSystemSource;
import ru.kpfu.ildar.pojos.TableViewFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/** Represents a controller for MainWindow.fxml */
public class Controller implements Initializable
{
    /** First files table */
    @FXML
    private FileTable table1;
    /** Second files table */
    @FXML
    private FileTable table2;

    /** Source of files/folders information */
    private FileSystemSource filesSource = new FileSystemSource();

    private List<FileTable> tables;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        table1.setFilesSource(filesSource);
        table2.setFilesSource(filesSource);

        table1.setController(this);
        table2.setController(this);
        tables = new ArrayList<>(Arrays.asList(table1, table2));

        table1.init("C:\\");
        table2.init("C:\\");

        Dialog.Actions.CANCEL.textProperty().set("Cancel");
    }

    /** Files table where an element cut was made */
    private FileTable cutFileTable;

    /** A table indicates that there was a copy or cut made in its files; notify other tables */
    public void copiedOrCut(boolean copied, TableViewFile file, String filePath, FileTable caller)
    {
        for(FileTable table : tables)
            table.copiedOrCut(copied, file, filePath);

        if(copied == false)
            cutFileTable = caller;
    }

    /** A table indicates that there was a paste made of the previously cut file; notify other tables */
    public void pasted()
    {
        if(cutFileTable != null)
        {
            cutFileTable.pasted();
            cutFileTable = null;
        }
    }

    @FXML
    private void exitClicked(ActionEvent actionEvent) { onExit(actionEvent); }

    private<T> void onExit(T evt)
    {
        System.exit(0);
    }
}
