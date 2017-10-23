package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.user.Student;
import main.utilities.Course;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

public class StudentActivity extends Application implements Initializable
{
    private static Student student;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("studentactivity.fxml"));

    @FXML
    private RadioButton myCourses;
    @FXML
    private RadioButton allCourses;
    @FXML
    private ListView coursesList;
    @FXML
    private TextField search;
    @FXML
    private Label tname;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("studentactivity.fxml"));
        primaryStage.setTitle("My Classroom- Student");
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setResizable(false);
        primaryStage.show();

        myCoursesListener();
    }

    public void go(Stage stage, Student student) throws Exception
    {
        this.student = student;
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

    public void myCoursesListener()
    {
        System.out.println(student.getName());
        coursesList.getItems().clear();
        try
        {
            if (coursesList.getItems().size() != 0)
                coursesList.getItems().clear();
            if (student.getCourses().isEmpty()) return;

        } catch (NullPointerException e)
        {
        }
        try
        {
            ArrayList<Course> currMyCourses = student.getCourses();
            for (Course i : currMyCourses)
            {
                coursesList.getItems().add(i.getName());
            }
        } catch (Exception e)
        {

        }
    }

    public void setAllCoursesListener(ActionEvent event)
    {
        coursesList.getItems().clear();
        for (Course i : Main.allCourses)
        {
            coursesList.getItems().add(i.getName());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
//        System.out.println("asa");
        tname.setText("Welcome, "+student.getName());
    }
}
