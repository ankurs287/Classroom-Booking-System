package main;

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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.user.Admin;
import main.user.Student;
import main.user.User;
import main.utilities.Course;
import main.utilities.Room;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudentActivity extends Application implements Initializable
{
    private static Student student;
    private static String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @FXML
    private TextField search;
    @FXML
    private Label tname;
    @FXML
    private ListView roomList;
    @FXML
    private ComboBox sTimeInterval, eTimeInterval, day, room;
    @FXML
    private TextArea reason;
    @FXML
    private RadioButton myCourses, allCourses, roomsDetail, roomsBooked;
    @FXML
    private TableView<ArrayList<String>> tableView;
    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, String> name, code, credits, instructor;

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
//        item1.setDisable(true);
//        item2.setDisable(false);
        coursesTable.setItems(null);
        if (student.getCourses().isEmpty()) return;

        coursesTable.refresh();
        ArrayList<Course> currMyCourses = student.getCourses();
        ObservableList<Course> items = FXCollections.observableArrayList();
        for (Course i : currMyCourses)
            items.add(i);

        coursesTable.setItems(items);

    }

    public void setAllCoursesListener()
    {
//        item2.setDisable(true);
//        item1.setDisable(false);

        coursesTable.setItems(null);
//        for (int i = 0; i < coursesTable.getItems().size(); i++)
//            coursesTable.getItems().clear();
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
                        String selectedCourse = coursesTable.getSelectionModel().getSelectedItem().getCode().toString().trim();
                        try
                        {
                            for (Course i : student.getCourses())
                            {
                                if (i.getCode().trim().toString().equals(selectedCourse))
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
                            if (i.getCode().trim().toString().equals(selectedCourse))
                            {
                                student.addCourse(i);
                                System.out.println("Course Added.");
                                try
                                {
                                    Main.serialize(student, "users/" + student.getName());
                                } catch (IOException e1)
                                {
                                    e1.printStackTrace();
                                }
                                break;
                            }
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
                        String selectedCourse = coursesTable.getSelectionModel().getSelectedItem().getCode().toString().trim();
                        try
                        {
                            for (Course i : student.getCourses())
                            {
                                if (i.getCode().trim().toString().equals(selectedCourse))
                                {
                                    student.getCourses().remove(i);
                                    Main.serialize(student, "users/" + student.getName());
                                    if (myCourses.isSelected()) myCoursesListener();
                                    System.out.println("Course Successfully Removed");
                                    return;
                                }
                            }
                        } catch (Exception e1)
                        {

                        }
                        System.out.println("It's not your Course.");
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
                        text.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14;");
                        text.setWrapText(true);
                        this.setGraphic(text);
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
                        this.setGraphic(text);
                    }
                }
            };
            return cell;
        });

        for (int i = 0; i < 7; i++)
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.now().plusDays(i);
            day.getItems().add(dtf.format(localDate) + " " + localDate.getDayOfWeek());
        }

        for (Room r : Main.allRooms)
            room.getItems().add(r.getName().toUpperCase());


        for (int a = 0; a < 24; a++)
        {
            try
            {
                sTimeInterval.getItems().add(timeInterval(a));
                eTimeInterval.getItems().add(timeInterval(a));
                a += 0;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void viewAllRooms() throws ParseException
    {
        roomList.getItems().clear();
        SimpleDateFormat format = new SimpleDateFormat("H:mm");
        for (Room i : Main.allRooms)
        {
            Label la1 = new Label(i.getName().toUpperCase());
            la1.setStyle("-fx-font-weight: bold; -fx-text-fill: white");

            roomList.getItems().add(la1);
            ArrayList<ArrayList<Date[]>> status = i.getTimeIntevals();
            for (int d = 0; d < 7; d++)
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate localDate = LocalDate.now().plusDays(d);
                String temp = dtf.format(localDate) + " " + localDate.getDayOfWeek() + ": ";

                ArrayList<Date[]> ti = new ArrayList<>();
                for (int j = 0; j < 6; j++)
                {
                    if (days[j].toUpperCase().equals(localDate.getDayOfWeek().toString().toUpperCase()))
                    {
                        ti = status.get(j);
                    }
                }
                for (int a = 0; a < ti.size(); a++)
                {

                    String sTime = format.format(ti.get(a)[0]);
                    String eTime = format.format(ti.get(a)[1]);

                    temp += sTime + "-" + eTime + ", ";
                }

                for (Date[] dd : i.getBooked())
                {
                    if (new SimpleDateFormat("dd/MM/yyyy").format(dd[0]).equals(dtf.format(localDate)))
                    {
                        temp += new SimpleDateFormat("H:mm").format(dd[0]) + "-" + new SimpleDateFormat("H:mm").format(dd[1]) + ", ";
                    }
                }
                roomList.getItems().add(temp);
            }
            roomList.getItems().add("");
        }
    }

    private static String timeInterval(int initial) throws ParseException
    {
        String time1 = "08:00:00";
        String time2 = "08:00:00";

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(time1);
        Date date2 = format.parse(time2);

        long curTimeInMs = date1.getTime();
        date2 = new Date(curTimeInMs + ((initial) * 60000 * 30));
        return new SimpleDateFormat("H:mm").format(date2);
    }

    public void searchCourse(ActionEvent event)
    {
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

    public void bookRoom() throws ParseException, IOException
    {
        SimpleDateFormat format = new SimpleDateFormat("H:mm");
        if (room.getSelectionModel().isEmpty())
        {
            System.out.println("Select Room.");
            Main.callPop("Select Room");
        }
        else if (day.getSelectionModel().isEmpty())
        {
            System.out.println("Select Date");
            Main.callPop("Select Date");
        }
        else if (sTimeInterval.getSelectionModel().isEmpty())
        {
            System.out.println("Select Start Time");
            Main.callPop("Select Start Time");
        }
        else if (eTimeInterval.getSelectionModel().isEmpty())
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
            String roomname = room.getSelectionModel().getSelectedItem().toString().toLowerCase();
            String Day = day.getSelectionModel().getSelectedItem().toString();
            String s = sTimeInterval.getSelectionModel().getSelectedItem().toString();
            String e = eTimeInterval.getSelectionModel().getSelectedItem().toString();
            Date startTime = format.parse(s);
            Date endTime = format.parse(e);
            if (startTime.compareTo(endTime) >= 0)
            {
                Main.callPop("Select a valid interval.");
                return;
            }

            int dayIndex = -1;
            for (int i = 0; i < 7; i++)
            {
                if (Day.split(" ")[1].equals(days[i].toUpperCase()))
                {
                    dayIndex = i;
                    break;
                }
            }

            Room r = null;
            ArrayList<ArrayList<Date[]>> status = new ArrayList<>();
            for (Room room : Main.allRooms)
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
            for (int i = 0; i < status.get(dayIndex).size(); i++)
            {
                if (!(startTime.compareTo(status.get(dayIndex).get(i)[1]) >= 0 || endTime.compareTo(status.get(dayIndex).get(i)[0]) <= 0))
                {
                    avail = false;
                    System.out.println("Room Not Available");
                    Main.callPop("Room Not Available");
                    return;
                }
            }

            format = new SimpleDateFormat("dd/MM/yyyy H:mm");
            startTime = format.parse(Day.split(" ")[0] + " " + s);
            endTime = format.parse(Day.split(" ")[0] + " " + e);
            for (int i = 0; i < r.getBooked().size(); i++)
            {
                Date[] whole = r.getBooked().get(i);
                SimpleDateFormat onlyDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                if (!(onlyDateFormat.format(startTime).equals(onlyDateFormat.format(whole[0]))))
                {
                    continue;
                }

                if (!(startTime.compareTo(whole[1]) >= 0 || endTime.compareTo(whole[0]) <= 0))
                {
                    avail = false;
                    System.out.println("Room Not Available");
                    Main.callPop("Room Not Available");
                    return;
                }
            }
            if (avail)
            {
                System.out.println("Room Available");
                sendRequest(r, Day, s, e);
                r.getBooked().add(new Date[]{format.parse(Day.split(" ")[0] + " " + s), format.parse(Day.split(" ")[0] + " " + e)});
//                if(1==1) return;

                HashMap<String, ArrayList<ArrayList<Object>>> br = student.getBookedRoom();
                if (br.containsKey(r.getName()))
                {
                    ArrayList<Object> ar = new ArrayList<Object>();
                    ar.add(format.parse(Day.split(" ")[0] + " " + s));
                    ar.add(format.parse(Day.split(" ")[0] + " " + e));
                    ar.add(reason.getText().toString().trim());
                    ar.add((boolean) false);
                    br.get(r.getName()).add(ar);
                    System.out.println(br.toString());
                }
                else
                {
                    ArrayList<Object> ar = new ArrayList<Object>();
                    ar.add(format.parse(Day.split(" ")[0] + " " + s));
                    ar.add(format.parse(Day.split(" ")[0] + " " + e));
                    ar.add(reason.getText().toString().trim());
                    ar.add((boolean) false);
                    ArrayList<ArrayList<Object>> arr = new ArrayList<>();
                    arr.add(ar);
                    br.put(r.getName(), arr);
                    System.out.println(br.toString());
                }
                // Room Booked and serialize room and admin object
                Main.serialize(r, "rooms/" + r.getName());
                Main.serialize(student, "users/" + student.getEmail());
                viewAllRooms();
                // refresh function call
            }
            else
            {
                System.out.println("Room Not Available");
                Main.callPop("Room Not Available");
            }
        }
    }

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
            ar.add(student);
            ar.add(format.parse(Day.split(" ")[0] + " " + s));
            ar.add(format.parse(Day.split(" ")[0] + " " + e));
            ar.add(reason.getText().toString().trim());
            br.get(r.getName()).add(ar);
            System.out.println(br.toString());
        }
        else
        {
            ArrayList<Object> ar = new ArrayList<Object>();
            ar.add(student);
            ar.add(format.parse(Day.split(" ")[0] + " " + s));
            ar.add(format.parse(Day.split(" ")[0] + " " + e));
            ar.add(reason.getText().toString().trim());
            ArrayList<ArrayList<Object>> arr = new ArrayList<>();
            arr.add(ar);
            br.put(r.getName(), arr);
            System.out.println(br.toString());
        }
        Main.serialize(Admin.requests, "requests/request");
        System.out.println("thissssssss" + Admin.requests);
        Main.callPop("Request sent to Admin.");
    }

    public void bookedRoom()
    {
        roomList.getItems().clear();
        if (student.getBookedRoom() == null)
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
                    la1.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

                    roomList.getItems().add(la1);
                    roomList.getItems().add(new SimpleDateFormat("dd/M/yyyy").format(i.get(0)) + ": " + format.format(i.get(0)) + " - " + format.format(i.get(1)));
                    roomList.getItems().add("Reason: " + i.get(2));
                    if ((boolean) i.get(3))
                    {
                        roomList.getItems().add("Request Status: Accepted");
                    }
                    else
                    {
                        roomList.getItems().add("Request Status: Pending");
                    }
                    Button cancel = new Button("Cancel Booking");
                    cancel.setOnAction(new EventHandler<ActionEvent>()
                    {
                        @Override
                        public void handle(ActionEvent event)
                        {
                            for (Room r : Main.allRooms)
                            {
                                if (r.getName().equals(k))
                                {
                                    ArrayList<Date[]> da = r.getBooked();
                                    for (Date[] dd : da)
                                    {
                                        if ((dd[0].compareTo((Date) i.get(0)) == 0) && dd[1].compareTo((Date) i.get(1)) == 0)
                                        {
                                            try
                                            {
                                                da.remove(dd);
                                                Main.serialize(r, "rooms/" + r.getName());
                                            } catch (Exception e)
                                            {
                                            }
                                        }
                                        break;
                                    }
                                }
                                student.getBookedRoom().get(k).remove(i);
                                // serialize room and admin
                                try
                                {
                                    Main.serialize(student, "users/" + student.getEmail());
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }

                                roomList.getItems().clear();
                                bookedRoom();
                                try
                                {
                                    viewAllRooms();
                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    roomList.getItems().add(cancel);
                }
            }
        }
    }

    public void searchCoursekey(KeyEvent keyEvent)
    {
//        coursesList.getItems().clear();
        searchCourse(new ActionEvent());
    }

    public void refresh(MouseEvent mouseEvent) throws ParseException
    {
        if (roomsDetail.isSelected()) viewAllRooms();
        else if (roomsBooked.isSelected()) bookedRoom();
        if (myCourses.isSelected()) myCoursesListener();
        else if (allCourses.isSelected()) setAllCoursesListener();
        viewTimeTable();
    }

    public void viewTimeTable() throws ParseException
    {
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
}