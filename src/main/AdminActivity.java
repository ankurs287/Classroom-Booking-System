package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.user.Admin;

public class AdminActivity extends Application
{
    private Admin admin;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("adminactivity.fxml"));
        primaryStage.setTitle("My Classroom- Admin");
        primaryStage.setScene(new Scene(root, 400, 280));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void go(Stage stage, Admin admin) throws Exception
    {
        this.admin=admin;
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
