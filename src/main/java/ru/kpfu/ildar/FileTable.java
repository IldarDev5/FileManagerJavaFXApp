package ru.kpfu.ildar;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;
import ru.kpfu.ildar.dialogs.CreateNewElementDialog;
import ru.kpfu.ildar.filessource.FileSystemSource;
import ru.kpfu.ildar.pojos.FileBean;
import ru.kpfu.ildar.pojos.TableViewFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FileTable extends VBox
{
    public static Stage stage;

    @FXML
    private TableView<TableViewFile> filesView;
    @FXML
    private TextField pathField;

    @FXML
    private TableColumn<TableViewFile, String> nameCol;
    @FXML
    private TableColumn<TableViewFile, Long> sizeCol;
    @FXML
    private TableColumn<TableViewFile, String> changedCol;
    @FXML
    private TableColumn<TableViewFile, String> typeCol;

    @FXML
    private Button copyBtn;
    @FXML
    private Button cutBtn;
    @FXML
    private Button pasteBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button propsBtn;
    @FXML
    private Button createNewBtn;
    @FXML
    private Button prevBtn;
    @FXML
    private Button nextBtn;
    @FXML
    private Button searchBtn;
    @FXML
    public Button openElemBtn;


    private ObservableList<TableViewFile> filesList = FXCollections.observableArrayList();
    private String currPath;

    private boolean copyOrCut;
    private String fileCopyCutPath;
    private TableViewFile fileCopyCut;

    private Controller controller;
    private FileSystemSource filesSource;

    private Stack<String> prevStack = new Stack<>();
    private Stack<String> nextStack = new Stack<>();

    public FileTable()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
                .getResource("customwindows/FileTable.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try { loader.load(); }
        catch (IOException exc) { throw new RuntimeException(exc); }

        setCellValueFactory();
        setCellFactoryForSize();
        setCellFactoryForName();

        filesView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        {
            boolean isNull = newVal == null;
            copyBtn.setDisable(isNull);
            cutBtn.setDisable(isNull);
            deleteBtn.setDisable(isNull);
            propsBtn.setDisable(isNull);
            openElemBtn.setDisable(isNull);
        });

        openElemBtn.setDisable(true);

        VBox.setVgrow(filesView, Priority.ALWAYS);
    }

    @FXML
    private void createNewBtnClicked(ActionEvent evt)
    {
        CreateNewElementDialog dialog = new CreateNewElementDialog
                (stage, "Create new element", currPath, filesSource);
        Action result = dialog.showDialog();
        if(result == dialog.getSubmitAction())
        {
            String path = currPath + File.separator + dialog.getName();
            try
            {
                filesSource.createNewElement(path, dialog.isFile());
            }
            catch(IOException exc)
            {
                exc.printStackTrace();
                Dialogs.create().title("Creation error")
                        .message("Error during file creation: " + exc.getMessage()).showError();
                return;
            }

            TableViewFile file = new TableViewFile(dialog.getName(), 0, new Date(),
                    dialog.isFile() ? "File" : "Folder", currPath);
            filesList.add(file);
            filesView.getSelectionModel().select(file);

            if(dialog.toOpenWhenCreated())
            {
                try { filesSource.openFile(dialog.getName(), currPath); }
                catch (IOException exc)
                {
                    exc.printStackTrace();
                    showCantOpenElement(exc);
                }
            }
        }
    }

    @FXML
    private void filesViewKeyPressed(KeyEvent evt)
    {
        if(evt.getCode() == KeyCode.ENTER || evt.getCode() == KeyCode.SPACE)
        {
            openSelectedItem();
        }
        else if(evt.getCode() == KeyCode.BACK_SPACE)
        {
            if(!prevBtn.isDisable())
                prevBtnClicked(null);
        }
        else if(evt.getCode() == KeyCode.DELETE)
        {
            if(!deleteBtn.isDisable())
                deleteClicked(null);
        }
    }

    private void openSelectedItem()
    {
        TableViewFile file = filesView.getSelectionModel().getSelectedItem();
        if(file == null)
            return;
        if(filesSource.isFile(file.getFileName(), file.getPath()))
        {
            try { filesSource.openFile(file.getFileName(), file.getPath()); }
            catch (IOException exc)
            {
                exc.printStackTrace();
                showCantOpenElement(exc);
            }
        }
        else
        {
            String newPath = file.getPath() + File.separator + file.getFileName();
            String prevPath = currPath;
            try { setCurrFolder(newPath); }
            catch(FileNotFoundException exc)
            {
                showElementDoesntExist();
                return;
            }
            catch(IllegalAccessException exc)
            {
                showCantEnterFolderErr();
                return;
            }

            if(prevPath != null)
            {
                prevStack.push(prevPath);
                nextStack.clear();
            }

            prevBtn.setDisable(false);
            nextBtn.setDisable(true);
        }
    }

    @FXML
    private void openElemBtnClicked(ActionEvent evt)
    {
        TableViewFile file = filesView.getSelectionModel().getSelectedItem();
        try
        {
            filesSource.openFile(file.getFileName(), currPath);
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
            showCantOpenElement(exc);
        }
    }

    @FXML
    private void filesViewMouseClicked(MouseEvent evt)
    {
        if(evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2)
        {
            openSelectedItem();
        }
    }

    public void setController(Controller controller) { this.controller = controller; }

    public FileSystemSource getFilesSource()
    {
        return filesSource;
    }
    public void setFilesSource(FileSystemSource filesSource)
    {
        this.filesSource = filesSource;
    }

    private void setCellFactoryForName()
    {
        Callback<TableColumn<TableViewFile, String>, TableCell<TableViewFile, String>> callback =
                new Callback<TableColumn<TableViewFile, String>, TableCell<TableViewFile, String>>()
                {
                    @Override
                    public TableCell call(TableColumn column)
                    {
                        return new TableCell<TableViewFile, String>()
                        {
                            @Override
                            protected void updateItem(String name, boolean empty)
                            {
                                super.updateItem(name, empty);
                                if(name == null || empty)
                                {
                                    setGraphic(null);
                                    return;
                                }

                                Label label = new Label(name);
                                TableViewFile file = filesList.stream().filter((f) -> f.getFileName()
                                        .equals(name)).findAny().get();
                                String path = file.getPath() + File.separator + file.getFileName();

                                String extension = filesSource.getExtension(name);
                                Image image = FXUtils.getFXImage(extension, path);
                                label.setGraphic(new ImageView(image));
                                this.setGraphic(label);
                            }
                        };
                    }
                };
        nameCol.setCellFactory(callback);
    }

    private void setCellFactoryForSize()
    {
        Callback<TableColumn<TableViewFile, Long>, TableCell<TableViewFile, Long>> callback =
                new Callback<TableColumn<TableViewFile, Long>, TableCell<TableViewFile, Long>>()
        {
            @Override
            public TableCell call(TableColumn tableColumn)
            {
                return new TableCell<TableViewFile, Long>()
                {
                    @Override
                    protected void updateItem(Long bytes, boolean empty)
                    {
                        super.updateItem(bytes, empty);
                        if(bytes == null || empty)
                        {
                            setGraphic(null);
                            return;
                        }

                        double kbs = floor((double) bytes / 1024);
                        if(kbs / 1024 > 1.0)
                        {
                            double mbs = kbs / 1024;
                            if(mbs / 1024 > 1.0)
                                setGraphic(new Label(floor(mbs / 1024) + " GBs"));
                            else
                                setGraphic(new Label(floor(mbs) + " MBs"));
                        }
                        else
                            setGraphic(new Label(kbs + " KBs"));
                    }

                    private double floor(double v)
                    {
                        return ((int)(v * 100)) / 100.0;
                    }
                };
            }
        };
        sizeCol.setCellFactory(callback);
    }
    
    private void setCellValueFactory()
    {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        changedCol.setCellValueFactory(new PropertyValueFactory<>("changedDate"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    private void setCurrFolder(String path) throws IllegalAccessException, FileNotFoundException
    {
        List<FileBean> files = filesSource.getElements(path);
        filesList = FXCollections.observableArrayList();
        files.stream().forEach((f) -> filesList.add(new TableViewFile(f.getName(),
                f.getSize(), f.getLastChangedDate(), f.getType().toString(), path)));
        filesView.setItems(filesList);

        if(filesList.size() != 0)
            filesView.getSelectionModel().select(0);

        currPath = path;
        pathField.setText(currPath);
    }

    @FXML
    private void copyClicked(ActionEvent evt)
    {
        copyOrCut = true;
        TableViewFile file = filesView.getSelectionModel().getSelectedItem();

        controller.copiedOrCut(copyOrCut, file, currPath, this);
    }

    private String cutFromPath;

    @FXML
    private void cutClicked(ActionEvent evt)
    {
        copyOrCut = false;
        TableViewFile file = filesView.getSelectionModel().getSelectedItem();

        cutFromPath = currPath;
        controller.copiedOrCut(copyOrCut, file, currPath, this);
    }

    @FXML
    private void pasteClicked(ActionEvent evt)
    {
        File fl1 = new File(fileCopyCutPath);
        String flPath2 = new File(currPath + File.separator + fl1.getName()).getAbsolutePath();
        if(fl1.getAbsolutePath().equals(flPath2))
            return;

        try
        {
            filesSource.copyElement(fileCopyCutPath, currPath);
            if(copyOrCut == false)
                filesSource.deleteElement(fileCopyCutPath);

            pasteBtn.setDisable(true);
            filesList.add(fileCopyCut);
            controller.pasted();
        }
        catch(Exception exc) { exc.printStackTrace(); }
    }

    public void disablePasteBtn() { pasteBtn.setDisable(true); }

    @FXML
    private void deleteClicked(ActionEvent evt)
    {
        TableViewFile file = filesView.getSelectionModel().getSelectedItem();
        try
        {
            if(filesSource.deleteElement(currPath + File.separator + file.getFileName()))
            {
                filesList.remove(file);
            }
            else
                Dialogs.create().title("Deletion error")
                        .message("Couldn't delete a file/folder.").showError();
        }
        catch(Exception exc) { exc.printStackTrace(); }
    }

    @FXML
    private void openPathClicked(ActionEvent evt)
    {
        String enteredText = pathField.getText();
        try { setCurrFolder(enteredText); }
        catch(FileNotFoundException exc)
        {
            showElementDoesntExist();
            return;
        }
        catch(IllegalAccessException exc)
        {
            showCantEnterFolderErr();
            return;
        }

        if(!currPath.equals(enteredText))
        {
            prevStack.push(currPath);
            nextStack.clear();

            prevBtn.setDisable(false);
            nextBtn.setDisable(true);
        }
    }

    @FXML
    private void prevBtnClicked(ActionEvent evt)
    {
        String path = prevStack.pop();
        String prevPath = currPath;
        try { setCurrFolder(path); }
        catch(FileNotFoundException exc)
        {
            showElementDoesntExist();
            return;
        }
        catch(IllegalAccessException exc)
        {
            showCantEnterFolderErr();
            return;
        }

        if(prevPath != null)
        {
            nextStack.push(prevPath);
            nextBtn.setDisable(false);
        }
        if(prevStack.size() == 0)
            prevBtn.setDisable(true);
    }

    @FXML
    private void nextBtnClicked(ActionEvent evt)
    {
        String path = nextStack.pop();
        String prevPath = currPath;
        try { setCurrFolder(path); }
        catch(FileNotFoundException exc)
        {
            showElementDoesntExist();
            return;
        }
        catch(IllegalAccessException exc)
        {
            showCantEnterFolderErr();
            return;
        }

        if(prevPath != null)
        {
            prevStack.push(prevPath);
            prevBtn.setDisable(false);
        }
        if(nextStack.size() == 0)
            nextBtn.setDisable(true);
    }

    @FXML
    private void searchClicked(ActionEvent evt)
    {
        Optional<String> op = Dialogs.create().title("Search").masthead("Search in the " +
            currPath + " folder").message("Enter name of the element you want to find:")
                .showTextInput();
        if(op.isPresent())
        {
            prevStack.push(currPath);
            prevBtn.setDisable(false);

            String name = op.get();
            try
            {
                List<TableViewFile> foundFiles = new ArrayList<>();
                search(name, currPath, foundFiles);
                filesList.clear();
                filesList.addAll(foundFiles);
                currPath = null;
                pathField.setText("");
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
            }
        }
    }

    private void search(String name, String path, List<TableViewFile> result) throws Exception
    {
        List<FileBean> files = filesSource.getElements(path);
        for(FileBean file : files)
        {
            if(file.getName().contains(name))
                result.add(new TableViewFile(file.getName(), file.getSize(), file.getLastChangedDate(), file.getType().toString(), path));

            if(!filesSource.isFile(file.getName(), path))
            {
                search(name, path + File.separator + file.getName(), result);
            }
        }
    }

    public void init(String currFolder)
    {
        try { setCurrFolder(currFolder); }
        catch(FileNotFoundException exc)
        {
            showElementDoesntExist();
            return;
        }
        catch(IllegalAccessException exc)
        {
            showCantEnterFolderErr();
        }
    }

    private void showCantEnterFolderErr()
    {
        Dialogs.create().title("Folder access error")
                .message("You can't enter this folder due to lack of access privileges.")
                .showError();
    }

    private void showCantOpenElement(Exception exc)
    {
        Dialogs.create().title("Opening error")
                .message("Error during file/folder opening: " +
                        exc.getMessage()).showError();
    }

    private void showElementDoesntExist()
    {
        Dialogs.create().title("Element not found")
                .message("The file/folder you're trying to open doesn't exist.")
                .showError();
    }

    public void copiedOrCut(boolean copied, TableViewFile file, String filePath)
    {
        this.copyOrCut = copied;
        fileCopyCutPath = filePath + File.separator + file.getFileName();
        fileCopyCut = file;

        pasteBtn.setDisable(false);
    }

    public void pasted()
    {
        if(currPath.equals(cutFromPath))
            filesList.remove(fileCopyCut);
    }
}
