package main;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.List;

public class TableViewByColumns extends Application
{
    @Override
    public void start(Stage primaryStage) {
        // Create the data structure
        String[][] data = new String[5][2];
        data[0] = new String[]{"Jon Skeet","876k"};
        data[1] = new String[]{"Darin Dimitrov","670k"};
        data[2] = new String[]{"BalusC","660k"};
        data[3] = new String[]{"Hans Passant","635k"};
        data[4] = new String[]{"Marc Gravell",null};

        // Create the table and columns
        TableView<String[]> tv = new TableView();

        TableColumn<String[],String> nameColumn = new TableColumn();
        nameColumn.setText("Name Column");

        TableColumn<String[],String> valueColumn = new TableColumn();
        valueColumn.setText("Value Column");
        tv.getColumns().addAll(nameColumn,valueColumn);

        // Add cell value factories
        nameColumn.setCellValueFactory((p)->{
                String[] x = p.getValue();
                return new SimpleStringProperty(x != null && x.length>0 ? x[0] : "<no name>");
        });

        valueColumn.setCellValueFactory((p)->{
                String[] x = p.getValue();
                return new SimpleStringProperty(x != null && x.length>1 ? x[1] : "<no value>");
        });


        // Add Data
        tv.getItems().addAll(Arrays.asList(data));

        // Finish setting the stage
        StackPane root = new StackPane();
        root.getChildren().add(tv);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Cell Value Factory Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
