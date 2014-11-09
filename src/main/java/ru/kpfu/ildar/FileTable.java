package ru.kpfu.ildar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ru.kpfu.ildar.filessource.FilesSource;
import ru.kpfu.ildar.pojos.TableViewFile;

import java.io.IOException;

public class FileTable extends VBox
{
    @FXML
    private TableView filesView;
    @FXML
    private Text pathText;

    @FXML
    private TableColumn<TableViewFile, String> nameCol;
    @FXML
    private TableColumn<TableViewFile, Double> sizeCol;
    @FXML
    private TableColumn<TableViewFile, String> changedCol;
    @FXML
    private TableColumn<TableViewFile, String> typeCol;

    @FXML
    private Button copyBtn;
    @FXML
    private Button pasteBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button propsBtn;
    @FXML
    private Button createNewBtn;

    private ObservableList<TableViewFile> filesList = FXCollections.observableArrayList();

    private FilesSource filesSource;

    public FileTable()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
                .getResource("customwindows/FileTable.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try { loader.load(); }
        catch (IOException exc) { throw new RuntimeException(exc); }

        setCellFactory();
    }

    public FilesSource getFilesSource()
    {
        return filesSource;
    }
    public void setFilesSource(FilesSource filesSource)
    {
        this.filesSource = filesSource;
    }

    private void setCellFactory()
    {


        nameCol.setCellValueFactory(new PropertyValueFactory<TableViewFile, String>("fileName"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<TableViewFile, Double>("size"));
        changedCol.setCellValueFactory(new PropertyValueFactory<TableViewFile, String>("changedDate"));
        typeCol.setCellValueFactory(new PropertyValueFactory<TableViewFile, String>("type"));
    }

    @FXML
    private void prevBtnClicked(ActionEvent evt)
    {

    }

    @FXML
    private void nextBtnClicked(ActionEvent evt)
    {

    }

    @FXML
    private void searchClicked(ActionEvent evt)
    {

    }
}
