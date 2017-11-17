package main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.stage.Stage.*;

public class ProgressSample extends Application
{

    final Float[] values = new Float[]{-1.0f};
    final Label[] labels = new Label[values.length];
    final ProgressBar[] pbs = new ProgressBar[values.length];
    final ProgressIndicator[] pins = new ProgressIndicator[values.length];
    final HBox hbs[] = new HBox[values.length];
    public static Stage stage=new Stage();

    @Override
    public void start(Stage stage1)
    {
        stage=stage1;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        Pane root = new Pane();
        root.setBackground(Background.EMPTY);
        Scene scene = new Scene(root, 300, 250);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.setTitle("Progress Controls");

        final ProgressIndicator pin = new ProgressIndicator();
        pin.setProgress(-1.0f);
        HBox hb = new HBox();
        hb.setBackground(Background.EMPTY);
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(pin);

        scene.setRoot(hb);
//        Stage.initStyle(StageStyle.UNDECORATED);

        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
//        Stage.initStyle(StageStyle.UNDECORATED);
    }
}