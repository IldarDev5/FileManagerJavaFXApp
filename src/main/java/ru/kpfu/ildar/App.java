package ru.kpfu.ildar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        FileTable.stage = stage;
        stage.setTitle("Ildar's File Manager");

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainWindow.fxml"));
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
    }
}
