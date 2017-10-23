package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Signup extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
        primaryStage.setTitle("Sign up");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setResizable(false);
        primaryStage.show();

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
