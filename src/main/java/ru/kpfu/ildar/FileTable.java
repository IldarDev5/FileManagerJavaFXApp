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

/** Represents a controller for FileTable.fxml; FileTable is a control with a files/folders table and
 * some buttons to operate with files and folders. */
public class FileTable extends VBox
{
    /** Stage of the main window */
    public static Stage stage;

    /** Table where files are placed */
    @FXML
    private TableView<TableViewFile> filesView;
    /** Path will be specified here */
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

    /** Current elements are stored in this list */
    private ObservableList<TableViewFile> filesList = FXCollections.observableArrayList();
    /** Current opened path */
    private String currPath;

    //These three fields are for copy/cut functions
    /** If true, that a copy button was clicked, otherwise a cut button. */
    private boolean copyOrCut;
    /** Path to the file that was copied/cut */
    private String fileCopyCutPath;
    /** File that was copied/cut represented in the table */
    private TableViewFile fileCopyCut;

    /** Controller of the main window; instance is used to communicate with it */
    private Controller controller;
    private FileSystemSource filesSource;

    /** Stack where previous links are stored */
    private Stack<String> prevStack = new Stack<>();
    /** Stack where next accessed links are stored */
    private Stack<String> nextStack = new Stack<>();

    public FileTable()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
                .getResource("customwindows/FileTable.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try { loader.load(); }
        catch (IOException exc) { throw new RuntimeException(exc); }

        setCellValueFactory();  //Link columns to the TableView instance
        setCellFactoryForSize(); //Cell factory for correct formatting in the 'size' column
        setCellFactoryForName(); //Cell factory for correct formatting in the 'name' column

        //Disable some buttons that are used only on selected table items
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

    /** 'Create new element' button clicked */
    @FXML
    private void createNewBtnClicked(ActionEvent evt)
    {
        CreateNewElementDialog dialog = new CreateNewElementDialog
                (stage, "Create new element", currPath, filesSource);
        Action result = dialog.showDialog();
        if(result == dialog.getSubmitAction())  //If user submitted file creation
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

            //Open this element if user has chosen so
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

    /** If some key was pressed on the table */
    @FXML
    private void filesViewKeyPressed(KeyEvent evt)
    {
        //If enter, than open the item
        if(evt.getCode() == KeyCode.ENTER || evt.getCode() == KeyCode.SPACE)
        {
            openSelectedItem();
        }
        ///If backspace then go to the previous link
        else if(evt.getCode() == KeyCode.BACK_SPACE)
        {
            if(!prevBtn.isDisable())
                prevBtnClicked(null);
        }
        //If delete button then delete the element
        else if(evt.getCode() == KeyCode.DELETE)
        {
            if(!deleteBtn.isDisable())
                deleteClicked(null);
        }
    }

    /** Open the element that was selected in the table */
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

            //currPath will be null when the last items access was search of items
            if(prevPath != null)
            {
                prevStack.push(prevPath);
                nextStack.clear();
            }

            prevBtn.setDisable(false);
            nextBtn.setDisable(true);
        }
    }

    /** Open the selected element */
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

    /** Mouse click was made on the table */
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

    /** Set cell factory for 'name' column */
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

                                //'Name' columns cells will have an icon and a name of the file
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

    /** Set cell factory for 'size' column */
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

                        //Convert size in kilobytes
                        double kbs = floor((double) bytes / 1024);
                        if(kbs / 1024 > 1.0)
                        {
                            //If size is more than one megabyte, convert it to MBs
                            double mbs = kbs / 1024;
                            if(mbs / 1024 > 1.0)
                                //If size is more than one gigabyte, convert it to GBs
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

    /** Map properties of the TableViewFile class to the respective columns */
    private void setCellValueFactory()
    {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        changedCol.setCellValueFactory(new PropertyValueFactory<>("changedDate"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    /** Set new current folder and items */
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

    /** 'Copy element' button clicked */
    @FXML
    private void copyClicked(ActionEvent evt)
    {
        copyOrCut = true;
        TableViewFile file = filesView.getSelectionModel().getSelectedItem();

        controller.copiedOrCut(copyOrCut, file, currPath, this);
    }

    private String cutFromPath;

    /** 'Cut element' button clicked */
    @FXML
    private void cutClicked(ActionEvent evt)
    {
        copyOrCut = false;
        TableViewFile file = filesView.getSelectionModel().getSelectedItem();

        cutFromPath = currPath;
        controller.copiedOrCut(copyOrCut, file, currPath, this);
    }

    /** 'Paste an element' button clicked */
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
            if(copyOrCut == false) //If it was a cut, then delete a file from its first path
                filesSource.deleteElement(fileCopyCutPath);

            pasteBtn.setDisable(true);
            filesList.add(fileCopyCut);
            controller.pasted();
        }
        catch(Exception exc) { exc.printStackTrace(); }
    }

    public void disablePasteBtn() { pasteBtn.setDisable(true); }

    /** 'Delete an element' button clicked */
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

    /** 'Open the specified path' button clicked */
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

        //If the same path as current path was entered, do not put in into previous paths stack
        if(!currPath.equals(enteredText))
        {
            prevStack.push(currPath);
            nextStack.clear();

            prevBtn.setDisable(false);
            nextBtn.setDisable(true);
        }
    }

    /** 'Previous path' button clicked */
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

    /** 'Next path' button clicked */
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

    /** 'Search of files/folders' button clicked. Search will be made in the current folder and
     * in the subfolders of it */
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

    /** Search recursively in subfolders */
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

    /** Set the current folder and load its elements */
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

    /** There was a copy/cut made in another table */
    public void copiedOrCut(boolean copied, TableViewFile file, String filePath)
    {
        this.copyOrCut = copied;
        fileCopyCutPath = filePath + File.separator + file.getFileName();
        fileCopyCut = file;

        pasteBtn.setDisable(false);
    }

    /** There was a paste made in another table */
    public void pasted()
    {
        if(currPath.equals(cutFromPath))
            filesList.remove(fileCopyCut);
    }
}
