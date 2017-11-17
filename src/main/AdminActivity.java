package main;

import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.user.Admin;
import main.user.Student;
import main.user.User;
import main.utilities.Room;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static main.Main.getUpdate;
import static main.Main.setUpdate;

/*Admin Controller on Admin Login*/
public class AdminActivity extends Application implements Initializable
{
    private static Admin admin;
    private static String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

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
    private JFXTextField cap;
    @FXML
    private JFXRadioButton requestsBtn, roomsDetail, roomsBooked;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("adminactivity.fxml"));
        primaryStage.setTitle("My Classroom- Admin");
        Scene scene = new Scene(root, 660, 450);
        scene.getStylesheets().add(getClass().getResource("../resources/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //call start and declare current user
    public void go(Stage stage, Admin admin) throws Exception
    {
        this.admin = admin;
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tname.setText("Welcome, " + admin.getName());
        roomsDetail.setSelected(true);
        roomsBooked.setSelected(false);
        requestsBtn.setSelected(false);

        try
        {
            refresh();
        } catch (Exception e)
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

        room.getItems().add("nil");
        for (Room r : Main.allRooms)
            room.getItems().add(r.getName().toUpperCase());
    }

    //View All Rooms detail.
    public void viewAllRooms() throws ParseException, IOException
    {
        // update structure(database)
        admin = (Admin) getUpdate(admin);
        roomList.getItems().clear();
        SimpleDateFormat format = new SimpleDateFormat("H:mm");
        // Traversing allRooms
        for (Room i : Main.allRooms)
        {
            Label la1 = new Label(i.getName().toUpperCase() + "   Capacity: " + i.getCapacity());
            la1.setStyle("-fx-font-size: 16; -fx-text-fill: white;");

            roomList.getItems().add(la1);
            ArrayList<ArrayList<Date[]>> status = i.getTimeIntevals();

            // Add Day of week details
            for (int d = 0; d < 7; d++)
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

                //check timetable rooms
                for (int a = 0; a < ti.size(); a++)
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

                // check rooms booked by users
                for (Date[] dd : i.getBooked())
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

    // Book a Room
    public void bookRoom() throws IOException, ParseException
    {
        // if room is not selected by user. Provide empty room automatically else provide room given by user.
        if ((!room.getSelectionModel().isEmpty()) && (!room.getSelectionModel().getSelectedItem().toString().equals("nil")))
        {
            int t = bookRoom2(room.getSelectionModel().getSelectedItem().toString().toLowerCase());
            if (t == 1)
            {
                Main.callPop("Room Booked Successfully.");
                return;
            }
            else if (t == -1)
            {
                return;
            }
            else if (t == 5)
            {
                Main.callPop("Selected Room does not have required capacity");
                return;
            }
        }
        else
        {
            int f = 0;
            for (Room r : Main.allRooms)
            {
                int t = bookRoom2(r.getName());
                f++;
                if (t == 1)
                {
                    Main.callPop("Room Booked Successfully.");
                    return;
                }
                else if (t == -1)
                {
                    return;
                }
            }
            if (f == Main.allRooms.size())
            {
                Main.callPop("No room is available with required capacity.");
                return;
            }
        }
        Main.callPop("Room not available.");
    }

    //func called by bookRoom()
    public int bookRoom2(String autoroom) throws ParseException, IOException
    {
        //get update from database
        admin = (Admin) getUpdate(admin);

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
            String roomname = autoroom;
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
            if (startTime.compareTo(endTime) >= 0) // wrong time interval
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
            try
            {
                int tcap = Integer.parseInt(cap.getText().toString().trim());
                if (tcap > r.getCapacity()) return 5;
            } catch (Exception e1)
            {
                Main.callPop("Enter a positive integer in capacity field.");
                return -1;
            }

            System.out.println(r + " " + roomname + " " + Day + " " + format.format(startTime) + " " + format.format(endTime));
            boolean avail = true;
            for (int i = 0; i < status.get(dayIndex).size(); i++) // timetable intervals checking
            {
                if (!(startTime.compareTo(status.get(dayIndex).get(i)[1]) >= 0 || endTime.compareTo(status.get(dayIndex).get(i)[0]) <= 0))
                {
                    avail = false;
                    System.out.println("Room Not Available");
                    Main.callPop("Room Not Available");
                    return 0;
                }
            }

            format = new SimpleDateFormat("dd/MM/yyyy H:mm");
            startTime = format.parse(Day + " " + s);
            endTime = format.parse(Day + " " + e);
            for (int i = 0; i < r.getBooked().size(); i++) // room booked by user checking
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
                    Main.callPop("Room Not Available");
                    return 0;
                }
            }
            if (avail) // if room is available
            {
                System.out.println("Room Available");
                r.getBooked().add(new Date[]{format.parse(Day + " " + s), format.parse(Day + " " + e)});

                HashMap<String, ArrayList<ArrayList<Object>>> br = admin.getBookedRoom(); // update booking
                if (br.containsKey(r.getName()))
                {
                    ArrayList<Object> ar = new ArrayList<Object>();
                    ar.add(format.parse(Day + " " + s));
                    ar.add(format.parse(Day + " " + e));
                    ar.add(reason.getText().toString().trim());
                    br.get(r.getName()).add(ar);
                    System.out.println(br.toString());
                }
                else
                {
                    ArrayList<Object> ar = new ArrayList<Object>();
                    ar.add(format.parse(Day + " " + s));
                    ar.add(format.parse(Day + " " + e));
                    ar.add(reason.getText().toString().trim());
                    ArrayList<ArrayList<Object>> arr = new ArrayList<>();
                    arr.add(ar);
                    br.put(r.getName(), arr);
                    System.out.println(br.toString());
                }
                // Room Booked and serialize room and admin object
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

    // room booked by current user
    public void bookedRoom() throws IOException
    {
        //get update from database
        admin = (Admin) getUpdate(admin);

        roomList.getItems().clear();
        if (admin.getBookedRoom() == null) // no room booked
        {
            roomList.getItems().add("No Room is Booked.");
            Main.callPop("No Room is Booked.");
        }
        else
        {
            for (String k : admin.getBookedRoom().keySet()) // get booked room
            {
                for (ArrayList<Object> i : admin.getBookedRoom().get(k))
                {
                    SimpleDateFormat format = new SimpleDateFormat("H:mm");
                    Label la1 = new Label(k.toUpperCase());
                    la1.setStyle("-fx-text-fill: white; -fx-font-size: 16");

                    roomList.getItems().add(la1);
                    roomList.getItems().add(new SimpleDateFormat("dd/M/yyyy").format(i.get(0)) + ": " + format.format(i.get(0)) + " - " + format.format(i.get(1)));
                    roomList.getItems().add("Reason: " + i.get(2));
                    Button cancel = new Button("Cancel Booking");
                    cancel.setOnAction(new EventHandler<ActionEvent>() // cancel booking
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
                                // serialize room and admin
                                admin.getBookedRoom().get(k).remove(i);
                                try
                                {
                                    setUpdate();
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
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

    // view room requests by students.
    public void viewRequests() throws IOException
    {
        admin = (Admin) getUpdate(admin);

        roomList.getItems().clear();
        for (String k : Admin.requests.keySet())
        {
            for (ArrayList<Object> i : Admin.requests.get(k))
            {
                SimpleDateFormat format = new SimpleDateFormat("H:mm");
                Label la1 = new Label(k.toUpperCase());
                la1.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
//                i.set(0, (Student) getUpdate(((User) i.get(0))));

                roomList.getItems().add(la1);
                roomList.getItems().add(new SimpleDateFormat("dd/M/yyyy").format(i.get(1)) + ": " + format.format(i.get(1)) + " - " + format.format(i.get(2)));
                roomList.getItems().add("Reason: " + i.get(3));

                Student student = null;
                for (User u : Main.allUsers)
                {
                    if (u.getEmail().equals(((String) i.get(0))))
                    {
                        student = (Student) u;
                        break;
                    }
                }
                roomList.getItems().add("Requested by:- " + student.getName() + " " + student.getEmail());

                HBox hbox = new HBox();
                Button accept = new Button("Accept");
                Button reject = new Button("Reject");
                Label sp = new Label("   ");
                Label acclabel = new Label("Accepted by Admin.");
                Student finalStudent = student;
                accept.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        System.out.println("accept clicked");
                        HashMap<String, ArrayList<ArrayList<Object>>> br = finalStudent.getBookedRoom();

                        for (ArrayList<Object> ar : br.get(k))
                        {
                            if (((Date) ar.get(0)).compareTo((Date) i.get(1)) == 0 && ((Date) ar.get(1)).compareTo((Date) i.get(2)) == 0)
                            {
                                ar.set(3, "1");
                                System.out.println("Request Accepted");
                                try
                                {
                                    sendMail(finalStudent.getEmail(), "Your request for booking " + k + " room on " + new SimpleDateFormat("dd/M/yyyy").format(i.get(1)) + " from " + format.format(i.get(1)) + " to " + format.format(i.get(2)) + " is accepted.");
                                } catch (Exception wer)
                                {
                                    System.out.println("email not sent");
                                }
                                break;
                            }
                        }
//                        Admin.requests.get(k).remove(i);
                        // serialize room, admin, admin
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
                reject.setOnAction(new EventHandler<ActionEvent>()
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
                        HashMap<String, ArrayList<ArrayList<Object>>> br = finalStudent.getBookedRoom();
                        for (ArrayList<Object> ar : br.get(k))
                        {
                            System.out.println(ar);
                            if (((Date) ar.get(0)).compareTo((Date) i.get(1)) == 0 && ((Date) ar.get(1)).compareTo((Date) i.get(2)) == 0)
                            {
                                System.out.println("Removed");
                                try
                                {
                                    sendMail(finalStudent.getEmail(), "Your request for booking " + k + " room on " + new SimpleDateFormat("dd/M/yyyy").format(i.get(1)) + " from " + format.format(i.get(1)) + " to " + format.format(i.get(2)) + " is rejected.");
                                } catch (Exception ew)
                                {
                                    System.out.println("email not sent");
                                }
                                ar.set(3, "2");
                                break;
                            }
                        }
                        // serialize room, admin, admin
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

                HashMap<String, ArrayList<ArrayList<Object>>> br = finalStudent.getBookedRoom();

                int flag = 0;
                if (br.containsKey(k))
                {
                    for (ArrayList<Object> ar : br.get(k))
                    {
                        if (((Date) ar.get(0)).compareTo((Date) i.get(1)) == 0 && ((Date) ar.get(1)).compareTo((Date) i.get(2)) == 0)
                        {
                            if (((String) ar.get(3)).equals("1"))
                            {
                                roomList.getItems().add("Requested Accepted By Admin.");
                                hbox.getChildren().addAll(reject);
                                roomList.getItems().add(hbox);
                                flag = 1;
                            }
                            break;
                        }
                    }
                }
                if (flag == 0)
                {
                    hbox.getChildren().addAll(accept, sp, reject);
                    roomList.getItems().add(hbox);
                }
                roomList.getItems().add("");
            }
        }
    }

    // refresh application
    public void refresh() throws ParseException, IOException
    {
        admin = (Admin) getUpdate(admin);

        if (requestsBtn.isSelected()) viewRequests();
        else if (roomsBooked.isSelected()) bookedRoom();
        else if (roomsDetail.isSelected()) viewAllRooms();
    }

    private static void sendMail(String mail, String sub)
    {
        final String user = "classroom.booking.system@gmail.com";//change accordingly
        final String password = "ankur+anvit";//change accordingly

        String to = mail;//change accordingly

        //Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(user, password);
            }
        });

        //Compose the message
        try
        {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Request Status: Classroom Booking System");
            String messageBody = sub;
            message.setText(messageBody);

            //send the message
            Transport.send(message);

            System.out.println("message sent successfully...");
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Try again after sometime.");
        }
    }
}
