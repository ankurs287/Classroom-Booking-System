package main;

import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import main.user.Admin;
import main.user.Student;
import main.user.User;
import main.utilities.Course;
import main.utilities.Room;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static main.Main.getUpdate;
import static main.Main.setUpdate;
/*Student Controller on Student Login*/
public class StudentActivity extends Application implements Initializable
{
    private static Student student;
    private static String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @FXML
    private JFXTextField search;
    @FXML
    private Label tname;
    @FXML
    private JFXListView roomList;
    @FXML
    private JFXTimePicker sTimeInterval, eTimeInterval;
    @FXML
    private JFXDatePicker day;
    @FXML
    private JFXComboBox room;
    @FXML
    private JFXTextArea reason;
    @FXML
    private JFXRadioButton myCourses, allCourses, roomsDetail, roomsBooked;
    @FXML
    private TableView<ArrayList<String>> tableView;
    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, String> name, code, credits, instructor;
    @FXML
    private ImageView anim;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("studentactivity.fxml"));
        primaryStage.setTitle("My Classroom- Student");
        Scene scene = new Scene(root, 660, 450);
        scene.getStylesheets().add(getClass().getResource("../resources/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //call start and declare current user
    public void go(Stage stage, Student student) throws Exception
    {
        this.student = student;
        start(stage);
    }

    // go to login/signup page (log out)
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

    // display courses taught by faculty
    public void myCoursesListener() throws IOException, ParseException
    {
        // get update from database
        student = (Student) getUpdate(student);

        coursesTable.setItems(null);
        if (student.getCourses().isEmpty()) return;

        coursesTable.refresh();
        ArrayList<Course> currMyCourses = student.getCourses();
        ObservableList<Course> items = FXCollections.observableArrayList();
        for (Course i : currMyCourses)
            items.add(i);

        coursesTable.setItems(items);
    }

    // display all courses in institute
    public void setAllCoursesListener() throws IOException, ParseException
    {
        // get update from database
        student = (Student) getUpdate(student);

        coursesTable.setItems(null);
        coursesTable.refresh();
        ObservableList<Course> items = FXCollections.observableArrayList();
        for (Course i : Main.allCourses)
            items.add(i);

        coursesTable.setItems(items);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tname.setText("Welcome, " + student.getName());

        myCourses.setSelected(true);
        allCourses.setSelected(false);
        roomsDetail.setSelected(true);
        roomsBooked.setSelected(false);
        sTimeInterval.setEditable(false);
        eTimeInterval.setEditable(false);

        try
        {
            refresh();
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        day.setConverter(new StringConverter<LocalDate>()
        {
            String pattern = "dd/MM/yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date)
            {
                if (date != null)
                {
                    return dateFormatter.format(date);
                }
                else
                {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string)
            {
                if (string != null && !string.isEmpty())
                {
                    return LocalDate.parse(string, dateFormatter);
                }
                else
                {
                    return null;
                }
            }
        });
        day.setPromptText("Date");

        coursesTable.setRowFactory(new Callback<TableView<Course>, TableRow<Course>>()
        {
            @Override
            public TableRow<Course> call(TableView<Course> tableView)
            {
                final TableRow<Course> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();

                final MenuItem joinMenuItem = new MenuItem("Join Course");
                joinMenuItem.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        try
                        {
                            student = (Student) getUpdate(student);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        String selectedCourse = coursesTable.getSelectionModel().getSelectedItem().getName().toString().trim();
                        try
                        {
                            for (Course i : student.getCourses())
                            {
                                if (i.getName().trim().toString().equals(selectedCourse))
                                {
                                    System.out.println("You've already joined the course.");
                                    return;
                                }
                            }
                        } catch (Exception e1)
                        {

                        }

                        for (Course i : Main.allCourses)
                        {
                            if (i.getName().trim().toString().equals(selectedCourse))
                            {
                                student.addCourse(i);
                                System.out.println("Course Added.");
                                try
                                {
                                    setUpdate();
                                } catch (IOException e1)
                                {
                                    e1.printStackTrace();
                                }
                                break;
                            }
                        }
                        try
                        {
                            setUpdate();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            refresh();
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                joinMenuItem.setStyle("-fx-font-size: 13;");

                final MenuItem removeMenuItem = new MenuItem("Leave Course");
                removeMenuItem.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        try
                        {
                            student = (Student) getUpdate(student);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        String selectedCourse = coursesTable.getSelectionModel().getSelectedItem().getName().toString().trim();
                        try
                        {
                            for (Course i : student.getCourses())
                            {
                                if (i.getName().trim().toString().equals(selectedCourse))
                                {
                                    student.getCourses().remove(i);
                                    setUpdate();
                                    if (myCourses.isSelected()) myCoursesListener();
                                    break;
                                }
                            }
                        } catch (Exception e1)
                        {

                        }
                        System.out.println("It's not your Course.");
                        try
                        {
                            setUpdate();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            refresh();
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                removeMenuItem.setStyle("-fx-font-size: 13;");


//                contextMenu.getItems().addAll(joinMenuItem, removeMenuItem);

                if (allCourses.isSelected())
                {
                    contextMenu.getItems().addAll(joinMenuItem);
//                    removeMenuItem.setDisable(true);
//                    joinMenuItem.setDisable(false);
                }
                else if (myCourses.isSelected())
                {
//                    joinMenuItem.setDisable(true);
//                    removeMenuItem.setDisable(false);
                    contextMenu.getItems().addAll(removeMenuItem);
                }
                else
                {
                    contextMenu.getItems().addAll(joinMenuItem, removeMenuItem);
                }
                // Set context menu on row, but use a binding to make it only show for non-empty rows:
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );
                return row;
            }
        });

        name.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        code.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));
        credits.setCellValueFactory(new PropertyValueFactory<Course, String>("credits"));
        instructor.setCellValueFactory(new PropertyValueFactory<Course, String>("instructor"));

        name.setCellFactory(col ->
        {
            TableCell<Course, String> cell = new TableCell<Course, String>()
            {
                @Override
                public void updateItem(String item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (item != null)
                    {
                        Label text = new Label(item);
                        text.setStyle("-fx-text-fill: white; -fx-font-size: 14");
                        text.setWrapText(true);
                        text.setAlignment(Pos.TOP_CENTER);
                        text.setAlignment(Pos.TOP_CENTER);
                        this.setGraphic(text);
                        this.setAlignment(Pos.TOP_CENTER);
                    }
                }
            };
            return cell;
        });
        instructor.setCellFactory(col ->
        {
            TableCell<Course, String> cell = new TableCell<Course, String>()
            {
                @Override
                public void updateItem(String item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (item != null)
                    {
                        Label text = new Label(item);
                        text.setStyle("-fx-text-fill: white;");
                        text.setWrapText(true);
                        text.setAlignment(Pos.TOP_CENTER);
                        text.setAlignment(Pos.TOP_CENTER);
                        this.setGraphic(text);
                        this.setAlignment(Pos.TOP_CENTER);
                    }
                }
            };
            return cell;
        });

        room.getItems().add("nil");
        for (Room r : Main.allRooms)
            room.getItems().add(r.getName().toUpperCase());
    }

    //View All Rooms detail.
    public void viewAllRooms() throws ParseException, IOException
    {
        // update structure(database)
        roomList.getItems().clear();
        student = (Student) getUpdate(student);
        SimpleDateFormat format = new SimpleDateFormat("H:mm");
        for (Room i : Main.allRooms) // Traversing allRooms
        {
            Label la1 = new Label(i.getName().toUpperCase());
            la1.setStyle("-fx-font-size: 16; -fx-text-fill: white;");

            roomList.getItems().add(la1);
            ArrayList<ArrayList<Date[]>> status = i.getTimeIntevals();

            for (int d = 0; d < 7; d++) // Add Day of week details
            {
                HBox hb = new HBox();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String temp = days[d] + ": ";
                Label ll = new Label(temp.toUpperCase());
                ll.setStyle("-fx-text-fill: white");
                ll.setMinWidth(70);
                hb.getChildren().add(ll);

                ArrayList<Date[]> ti = new ArrayList<>();
                ti = status.get(d);

                if (ti.size() == 0)
                {
                    VBox vb = new VBox();
                    vb.getChildren().addAll(new Label(), new Label());
                    hb.getChildren().add(vb);
                }

                for (int a = 0; a < ti.size(); a++)  //check timetable rooms
                {
                    VBox vb = new VBox();

                    String sTime = format.format(ti.get(a)[0]);
                    String eTime = format.format(ti.get(a)[1]);

                    vb.setAlignment(Pos.TOP_CENTER);
                    Label l1 = new Label("Always");
                    l1.setAlignment(Pos.TOP_CENTER);
                    l1.setTextAlignment(TextAlignment.CENTER);
                    l1.setContentDisplay(ContentDisplay.TOP);
                    Label l2 = new Label(sTime + "-" + eTime);
                    l2.setAlignment(Pos.TOP_CENTER);
                    l2.setTextAlignment(TextAlignment.CENTER);
                    l1.setStyle("-fx-text-fill: white");
                    l2.setStyle("-fx-text-fill: white");

                    vb.getChildren().addAll(l1, l2);
                    hb.getChildren().addAll(vb, new Label("    "));
                }
                for (Date[] dd : i.getBooked()) // check rooms booked by users
                {
//                    System.out.println(dd[0].toString());
                    if (dd[0].toString().split(" ")[0].equals(days[d].substring(0, 3)))
                    {
                        temp = new SimpleDateFormat("H:mm").format(dd[0]) + "-" + new SimpleDateFormat("H:mm").format(dd[1]) + ", ";
                        VBox vb = new VBox();
                        vb.setAlignment(Pos.TOP_CENTER);
                        Label l1 = new Label(dd[0].toString().substring(4, 10) + " " + dd[0].toString().split(" ")[5]);
                        l1.setAlignment(Pos.TOP_CENTER);
                        l1.setTextAlignment(TextAlignment.CENTER);
                        l1.setContentDisplay(ContentDisplay.TOP);
                        Label l2 = new Label(dd[0].toString().split(" ")[3].substring(0, 5) + "-" + dd[1].toString().split(" ")[3].substring(0, 5));
                        l2.setAlignment(Pos.TOP_CENTER);
                        l2.setTextAlignment(TextAlignment.CENTER);
                        l1.setStyle("-fx-text-fill: white");
                        l2.setStyle("-fx-text-fill: white");

                        vb.getChildren().addAll(l1, l2);
                        hb.getChildren().addAll(vb, new Label("   "));

                    }
                }
                roomList.getItems().add(hb);
            }
            roomList.getItems().add("");
        }
    }

    // search courses
    public void searchCourse(ActionEvent event) throws IOException
    {
        student = (Student) getUpdate(student);

        myCourses.setSelected(false);
        allCourses.setSelected(false);
        coursesTable.setItems(null);
        coursesTable.refresh();
        ObservableList<Course> items = FXCollections.observableArrayList();

        String[] tbs = search.getText().trim().toString().toLowerCase().split(" ");
        if (search.getText().trim().toString().isEmpty()) return;
        for (Course i : Main.allCourses)
        {
            String j = i.getPostConditions().toString();
            for (String tbss : tbs)
            {
                if (j.toLowerCase().indexOf(tbss) != -1 || i.getName().toLowerCase().indexOf(tbss) != -1)
                {
                    items.add(i);
                    break;
                }
            }
        }
        coursesTable.setItems(items);
    }

    // Book a Room
    public void bookRoom() throws IOException, ParseException
    {
        // if room is not selected by user. Provide empty room automatically else provide room given by user.
        if ((!room.getSelectionModel().isEmpty()) && (!room.getSelectionModel().getSelectedItem().toString().equals("nil")))
        {
            int t = bookRoom2(room.getSelectionModel().getSelectedItem().toString().toLowerCase());
            if (t == 1)
            {
                Main.callPop("Request Sent to Admin.");
                return;
            }
            else if (t == -1)
            {
                return;
            }
        }
        else
        {
            for (Room r : Main.allRooms)
            {
                int t = bookRoom2(r.getName());
                if (t == 1)
                {
                    Main.callPop("Request Sent to Admin.");
                    return;
                }
                else if (t == -1)
                {
                    return;
                }
            }
        }
        Main.callPop("Room not available.");
    }

    //func called by bookRoom()
    public int bookRoom2(String autoRoom) throws ParseException, IOException
    {
        //get update from database
        student = (Student) getUpdate(student);

        SimpleDateFormat format = new SimpleDateFormat("H:mm");
        if (day.getValue() == null)
        {
            System.out.println("Select Date");
            Main.callPop("Select Date");
        }
        else if (sTimeInterval.getValue() == null)
        {
            System.out.println("Select Start Time");
            Main.callPop("Select Start Time");
        }
        else if (eTimeInterval.getValue() == null)
        {
            System.out.println("Select End Time");
            Main.callPop("Select End Time");
        }
        else if (reason.getText().toString().trim().isEmpty())
        {
            Main.callPop("Please enter the reason.");
        }
        else
        {
            String roomname = autoRoom;
            String Day = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(day.getValue().toString()));
            System.out.println(Day);
            String s = (sTimeInterval.getValue().toString());
            String e = (eTimeInterval.getValue().toString());
            Date startTime = format.parse(s);
            Date endTime = format.parse(e);
            System.out.println(s + " " + e);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm");
            String strDate = sdf.format(cal.getTime());
            if (sdf.parse(Day + " " + s).compareTo(sdf.parse(strDate)) < 0) //if past date is selected
            {
                System.out.println("Select valid date.");
                Main.callPop("Select valid Date");
                return -1;
            }
            if (startTime.compareTo(endTime) >= 0) // wronf time interval
            {
                Main.callPop("Select a valid interval.");
                return -1;
            }

            int dayIndex = -1;
            SimpleDateFormat onlyDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (int i = 0; i < 7; i++) // get day of week
            {
                if (day.getValue().getDayOfWeek().toString().toUpperCase().equals(days[i].toUpperCase()))
                {
                    dayIndex = i;
                    break;
                }
            }

            Room r = null;
            ArrayList<ArrayList<Date[]>> status = new ArrayList<>();
            for (Room room : Main.allRooms) // get room
            {
                if (room.getName().equals(roomname))
                {
                    status = room.getTimeIntevals();
                    r = room;
                    break;
                }
            }

            System.out.println(r + " " + roomname + " " + Day + " " + format.format(startTime) + " " + format.format(endTime));
            boolean avail = true;
            for (int i = 0; i < status.get(dayIndex).size(); i++) // timetable intervals checking
            {
                if (!(startTime.compareTo(status.get(dayIndex).get(i)[1]) >= 0 || endTime.compareTo(status.get(dayIndex).get(i)[0]) <= 0))
                {
                    avail = false;
                    System.out.println("Room Not Available");
//                    Main.callPop("Room Not Available");
                    return 0;
                }
            }

            format = new SimpleDateFormat("dd/MM/yyyy H:mm");
            startTime = format.parse(Day + " " + s);
            endTime = format.parse(Day + " " + e);
            for (int i = 0; i < r.getBooked().size(); i++)  // room booked by user checking
            {
                Date[] whole = r.getBooked().get(i);
                if (!(onlyDateFormat.format(startTime).equals(onlyDateFormat.format(whole[0]))))
                {
                    continue;
                }

                if (!(startTime.compareTo(whole[1]) >= 0 || endTime.compareTo(whole[0]) <= 0))
                {
                    avail = false;
                    System.out.println("Room Not Available");
//                    Main.callPop("Room Not Available");
                    return 0;
                }
            }
            if (avail)  // if room is available
            {
                System.out.println("Room Available");
                // send request to admin
                sendRequest(r, Day, s, e);
                r.getBooked().add(new Date[]{format.parse(Day + " " + s), format.parse(Day + " " + e)});

                HashMap<String, ArrayList<ArrayList<Object>>> br = student.getBookedRoom();
                if (br.containsKey(r.getName()))
                {
                    ArrayList<Object> ar = new ArrayList<Object>();
                    ar.add(format.parse(Day + " " + s));
                    ar.add(format.parse(Day + " " + e));
                    ar.add(reason.getText().toString().trim());
                    ar.add("0");
                    br.get(r.getName()).add(ar);
                    System.out.println(br.toString());
                }
                else
                {
                    ArrayList<Object> ar = new ArrayList<Object>();
                    ar.add(format.parse(Day + " " + s));
                    ar.add(format.parse(Day + " " + e));
                    ar.add(reason.getText().toString().trim());
                    ar.add("0");
                    ArrayList<ArrayList<Object>> arr = new ArrayList<>();
                    arr.add(ar);
                    br.put(r.getName(), arr);
                    System.out.println(br.toString());
                }
                // Room Booked and serialize room and faculty object
                setUpdate(); //udate database
                refresh(); // refresh application
                return 1;
                // refresh function call
            }
            else
            {
                System.out.println("Room Not Available");
//                Main.callPop("Room Not Available");
                return 0;
            }
        }
        return -1;
    }

    // send request to admin
    private void sendRequest(Room r, String Day, String s, String e) throws ParseException, IOException
    {
        Admin ad = null;
        for (User u : Main.allUsers)
        {
            if (u.getType().equals("Admin"))
            {
                ad = (Admin) u;
            }
        }

        HashMap<String, ArrayList<ArrayList<Object>>> br = Admin.requests;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy H:mm");
        if (br.containsKey(r.getName()))
        {
            ArrayList<Object> ar = new ArrayList<Object>();
            ar.add(student.getEmail());
            ar.add(format.parse(Day + " " + s));
            ar.add(format.parse(Day + " " + e));
            ar.add(reason.getText().toString().trim());
            ar.add(LocalDate.now());
            br.get(r.getName()).add(ar);
            System.out.println(br.toString());
        }
        else
        {
            ArrayList<Object> ar = new ArrayList<Object>();
            ar.add(student.getEmail());
            ar.add(format.parse(Day + " " + s));
            ar.add(format.parse(Day + " " + e));
            ar.add(reason.getText().toString().trim());
            ar.add(LocalDate.now());
            ArrayList<ArrayList<Object>> arr = new ArrayList<>();
            arr.add(ar);
            br.put(r.getName(), arr);
            System.out.println(br.toString());
        }
//        Main.callPop("Request sent to Admin.");
    }

    // room booked by current user
    public void bookedRoom() throws IOException, ParseException
    {
        //get update from database
        student = (Student) getUpdate(student);

        roomList.getItems().clear();
        if (student.getBookedRoom().isEmpty())
        {
            roomList.getItems().add("No Room is Booked.");
            Main.callPop("No Room is Booked.");
        }
        else
        {
            for (String k : student.getBookedRoom().keySet())
            {
                for (ArrayList<Object> i : student.getBookedRoom().get(k))
                {
                    SimpleDateFormat format = new SimpleDateFormat("H:mm");
                    Label la1 = new Label(k.toUpperCase());
                    la1.setStyle("-fx-text-fill: white; -fx-font-size: 16");

                    roomList.getItems().add(la1);
                    roomList.getItems().add(new SimpleDateFormat("dd/M/yyyy").format(i.get(0)) + ": " + format.format(i.get(0)) + " - " + format.format(i.get(1)));
                    roomList.getItems().add("Reason: " + i.get(2));
                    System.out.println((String) i.get(3));
                    if (((String) i.get(3)).equals("1"))
                    {
                        roomList.getItems().add("Request Status: Accepted");
                    }
                    else if (((String) i.get(3)).equals("0"))
                    {
                        roomList.getItems().add("Request Status: Pending");
                    }
                    else roomList.getItems().add("Request Status: Rejected");

                    Button cancel = new Button("Cancel Booking");
                    cancel.setOnAction(new EventHandler<ActionEvent>() // cancel rom booking
                    {
                        @Override
                        public void handle(ActionEvent event)
                        {
                            try
                            {
                                refresh();
                            } catch (ParseException e)
                            {
                                e.printStackTrace();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            for (Room r : Main.allRooms)
                            {
                                if (r.getName().equals(k))
                                {
                                    ArrayList<Date[]> da = r.getBooked();
                                    for (Date[] dd : da)
                                    {
                                        if ((dd[0].compareTo((Date) i.get(0)) == 0) && dd[1].compareTo((Date) i.get(1)) == 0)
                                        {
                                            da.remove(dd);
                                        }
                                        break;
                                    }
                                }
                                student.getBookedRoom().get(k).remove(i);
                                // serialize room and admin
                            }

                            for (ArrayList<Object> ar : Admin.requests.get(k))
                            {
                                if (((String) ar.get(0)).equals(student.getEmail()) && ((Date) ar.get(1)).compareTo((Date) i.get(0)) == 0 && ((Date) ar.get(2)).compareTo((Date) i.get(1)) == 0)
                                {
                                    Admin.requests.get(k).remove(ar);
                                    break;
                                }
                            }
                            try
                            {
                                setUpdate();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            roomList.getItems().clear();
                            try
                            {
                                refresh();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                    roomList.getItems().add(cancel);

                }
            }
        }
    }

    // key listener on search textfield
    public void searchCoursekey(KeyEvent keyEvent) throws IOException, ParseException
    {
//        coursesList.getItems().clear();
        searchCourse(new ActionEvent());
    }

    // refresh application
    public void refresh() throws ParseException, IOException
    {
        student = (Student) getUpdate(student);

        if (roomsDetail.isSelected()) viewAllRooms();
        else if (roomsBooked.isSelected()) bookedRoom();
        if (myCourses.isSelected()) myCoursesListener();
        else if (allCourses.isSelected()) setAllCoursesListener();
        viewTimeTable();

        // cancel request if more than 5 days.
        updateAdminRequests();
    }

    // display student timetable
    public void viewTimeTable() throws ParseException, IOException
    {
        student = (Student) getUpdate(student);
        tableView.getColumns().clear();
        for (int i = 0; i < tableView.getItems().size(); i++)
        {
            tableView.getItems().clear();
        }

        // Create the data structure
        ArrayList<ArrayList<String>> data = new ArrayList<>(); //rows, columns
        ArrayList<Course> courses = student.getCourses();
        int maxCols = 0;
        for (int d = 0; d < 5; d++)
        {
            ArrayList<String> t = new ArrayList<>();
            t.add(days[d]);
            Object[][] t2 = new Object[100][2];
            int i = 0;
            for (Course c : courses)
            {
                if (!c.getTimeslot()[d].equals("-"))
                {
                    int x = Integer.parseInt(c.getTimeslot()[d].split("-")[0].split(":")[0]);
                    x = (x < 8) ? x + 12 : x;
                    String tempDate = x + ":" + c.getTimeslot()[d].split("-")[0].split(":")[1];
                    t2[i][0] = (Date) (new SimpleDateFormat("H:mm").parse(tempDate));
                    t2[i][1] = (String) (c.getName() + "\n" + c.getTimeslot()[d] + "\n" + c.getRoom()[d].toUpperCase());
                    i++;
                }
            }
            Comparator<Object[]> cc = new Comparator<Object[]>()
            {
                @Override
                public int compare(Object[] o1, Object[] o2)
                {
                    return ((Date) (o1[0])).compareTo(((Date) o2[0]));
                }
            };

            Arrays.sort(t2, 0, i, cc);
            for (int j = 0; j < i; j++)
            {
                t.add(((String) t2[j][1]));
            }
            data.add(t);
            maxCols = (t.size() > maxCols) ? t.size() : maxCols;
        }
        for (int i = 0; i < maxCols; i++)
        {
            TableColumn<ArrayList<String>, String> nameColumn = new TableColumn();
            nameColumn.setText("");
//            nameColumn.setMaxWidth(116);

            tableView.getColumns().add(nameColumn);

            // Add cell value factories
            int finalI = i;
            nameColumn.setCellValueFactory((p) ->
            {
                ArrayList<String> x = p.getValue();
                return new SimpleStringProperty(x != null && x.size() > finalI ? x.get(finalI) : " \n \n \n");
            });
        }

        // Add Data
        tableView.getItems().addAll(data);
    }

    // cancel request if more than 5 days.
    public static void updateAdminRequests() throws IOException
    {
        HashMap<String, ArrayList<ArrayList<Object>>> adr = Admin.requests;
        for (String k : adr.keySet())
        {
            for (ArrayList<Object> i : adr.get(k))
            {
                if (LocalDate.now().compareTo(((LocalDate) i.get(4)).plusDays(5)) == 0)
                {
                    System.out.println("");

                    for (Room r : Main.allRooms)
                    {
                        if (r.getName().equals(k))
                        {
                            ArrayList<Date[]> da = r.getBooked();
                            for (Date[] dd : da)
                            {
                                if ((dd[0].compareTo((Date) i.get(1)) == 0) && dd[1].compareTo((Date) i.get(2)) == 0)
                                {
                                    da.remove(dd);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    Admin.requests.get(k).remove(i);
                    Student student = null;
                    for (User u : Main.allUsers)
                    {
                        if (u.getEmail().equals(((String) i.get(0))))
                        {
                            student = (Student) u;
                            break;
                        }
                    }
                    HashMap<String, ArrayList<ArrayList<Object>>> br = student.getBookedRoom();
                    for (ArrayList<Object> ar : br.get(k))
                    {
                        System.out.println(ar);
                        if (((Date) ar.get(0)).compareTo((Date) i.get(1)) == 0 && ((Date) ar.get(1)).compareTo((Date) i.get(2)) == 0)
                        {
                            System.out.println("Removed");
                            ar.set(3, "2");
                            break;
                        }
                    }
                }
            }
        }

        setUpdate();
    }
}