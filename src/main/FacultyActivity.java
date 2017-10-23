package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.user.Faculty;

public class FacultyActivity extends Application
{
    private Faculty faculty;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("facultyactivity.fxml"));
        primaryStage.setTitle("My Classroom- Faculty");
        primaryStage.setScene(new Scene(root, 400, 280));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void go(Stage stage, Faculty faculty) throws Exception
    {
        this.faculty=faculty;
        start(stage);
    }

    public void gotoHome(ActionEvent event)
    {
        try
        {
            ((Node) (event.getSource())).getScene().getWindow().hide();
            new Main().start(new Stage());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
