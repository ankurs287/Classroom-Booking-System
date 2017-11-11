package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.user.Faculty;
import main.user.User;
import main.utilities.Course;
import main.utilities.Room;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class FacultyActivity extends Application implements Initializable
{
    private static Faculty faculty;
    private static String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @FXML
    private Label tname;
    @FXML
    private ListView coursesList, roomList;
    @FXML
    private ComboBox sTimeInterval, eTimeInterval, day, room;
    @FXML
    private TextArea reason;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("facultyactivity.fxml"));
        primaryStage.setTitle("My Classroom- Faculty");
        Scene scene = new Scene(root, 660, 450);
        scene.getStylesheets().add(getClass().getResource("../resources/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void go(Stage stage, Faculty faculty) throws Exception
    {
        this.faculty = faculty;
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tname.setText("Welcome, " + faculty.getName());
        try
        {
            viewAllRooms();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
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

    public void bookRoom(ActionEvent event) throws ParseException, IOException
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
                r.getBooked().add(new Date[]{format.parse(Day.split(" ")[0] + " " + s), format.parse(Day.split(" ")[0] + " " + e)});

                HashMap<String, ArrayList<ArrayList<Object>>> br = faculty.getBookedRoom();
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
                Main.serialize(faculty, "users/" + faculty.getEmail());
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

    public void bookedRoom()
    {
        roomList.getItems().clear();
        if (faculty.getBookedRoom() == null)
        {
            roomList.getItems().add("No Room is Booked.");
            Main.callPop("No Room is Booked.");
        }
        else
        {
            for (String k : faculty.getBookedRoom().keySet())
            {
                for (ArrayList<Object> i : faculty.getBookedRoom().get(k))
                {
                    SimpleDateFormat format = new SimpleDateFormat("H:mm");
                    Label la1 = new Label(k.toUpperCase());
                    la1.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

                    roomList.getItems().add(la1);
                    roomList.getItems().add(new SimpleDateFormat("dd/M/yyyy").format(i.get(0)) + ": " + format.format(i.get(0)) + " - " + format.format(i.get(1)));
                    roomList.getItems().add("Reason: " + i.get(2));
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
                                faculty.getBookedRoom().get(k).remove(i);
                                // serialize room and admin
                                try
                                {
                                    Main.serialize(faculty, "users/" + faculty.getEmail());
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

    private void myCourses()
    {
        for (Course c: Main.allCourses)
        {
            if(c.getInstructor().toLowerCase().equals(faculty.getName()))
            {
                coursesList.getItems().add(c.getName());
            }
        }
    }

    public void refresh(MouseEvent mouseEvent) throws ParseException
    {
//        if()
    }
}
