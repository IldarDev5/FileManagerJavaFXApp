package ru.kpfu.ildar.dialogs;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import ru.kpfu.ildar.filessource.FileSystemSource;

import java.io.File;

public class CreateNewElementDialog extends Dialog
{
    private Action submitAction;
    private boolean openWhenCreated;
    private boolean isFile = true;
    private String name;

    private FileSystemSource source;
    private String currPath;

    public Action getSubmitAction() { return submitAction; }

    public boolean toOpenWhenCreated() { return openWhenCreated; }
    public boolean isFile() { return isFile; }
    public String getName() { return name; }

    public CreateNewElementDialog(Object owner, String title, String currPath, FileSystemSource source)
    {
        super(owner, title);

        this.source = source;
        this.currPath = currPath;
    }

    public Action showDialog()
    {
        GridPane root = new GridPane();
        root.setHgap(10); root.setVgap(10);

        ToggleGroup group = new ToggleGroup();
        RadioButton createFile = new RadioButton("Create file");
        createFile.setId("file");
        RadioButton createFolder = new RadioButton("Create folder");
        createFolder.setId("folder");
        createFile.setToggleGroup(group);
        createFolder.setToggleGroup(group);
        createFile.setSelected(true);

        Label enterLabel = new Label("Enter name of the file to create:");
        TextField nameField = new TextField();

        Label errLabel = new Label();

        CheckBox openBox = new CheckBox("Open file when created");

        HBox box = new HBox(createFile, createFolder);
        box.setSpacing(10);
        root.add(box, 0, 0, 2, 1);
        root.add(enterLabel, 0, 1);
        root.add(nameField, 1, 1);
        root.add(errLabel, 0, 2);
        root.add(openBox, 0, 3);

        root.getColumnConstraints().add(new ColumnConstraints(200));

        openBox.selectedProperty().addListener((obs, oldVal, newVal) -> openWhenCreated = newVal);

        group.selectedToggleProperty().addListener((obs, oldVal, newVal) ->
        {
            String id = ((RadioButton)newVal).getId();
            if(id.equals("file"))
            {
                enterLabel.setText("Enter name of the file to create:");
                openBox.setText("Open file when created");
                isFile = true;
            }
            else if(id.equals("folder"))
            {
                enterLabel.setText("Enter name of the folder to create:");
                openBox.setText("Open folder when created");
                isFile = false;
            }
        });

        submitAction = new AbstractAction("Create")
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                name = nameField.getText();
                Dialog d = (Dialog)actionEvent.getSource();
                d.hide();
            }
        };
        submitAction.disabledProperty().set(true);

        nameField.textProperty().addListener((obs, oldVal, newVal) ->
        {
            if(newVal.equals(""))
            {
                errLabel.setText("Name mustn't be empty");
                submitAction.disabledProperty().set(true);
            }
            else
            {
                if(source.checkExistence(currPath + File.separator + newVal))
                {
                    errLabel.setText("Such " + (createFile.isSelected() ? "file" : "folder")
                            + " already exists.");
                    submitAction.disabledProperty().set(true);
                }
                else
                {
                    errLabel.setText("");
                    submitAction.disabledProperty().set(false);
                }
            }
        });

        ButtonBar.setType(submitAction, ButtonBar.ButtonType.OK_DONE);

        Platform.runLater(() -> nameField.requestFocus());

        this.setContent(root);
        this.setResizable(false);
        this.getActions().addAll(Dialog.Actions.CANCEL, submitAction);
        this.setGraphic(new ImageView(getClass().getClassLoader()
                .getResource("images/create_big.png").toString()));
        return this.show();
    }
}
