package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import main.user.Admin;
import main.user.Faculty;
import main.user.Student;
import main.user.User;
import main.utilities.Course;
import main.utilities.Room;
import main.utilities.Timetable;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main extends Application implements Serializable
{
    private static final String FILENAME = (new File("").getAbsolutePath());
    public static ArrayList<Course> allCourses = new ArrayList<>();
    public static HashSet<Room> allRooms = new HashSet<Room>();
    public static ArrayList<User> allUsers = new ArrayList<>();
    private int otp = -1;
    public static Popup popup = new Popup();

    @FXML
    private TextField sName, sEmail, sOTP, lName;
    @FXML
    private PasswordField sPassword, lPassword;
    @FXML
    private ComboBox role;
    @FXML
    private Button getOTP, signup;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("My Classroom");
        Scene scene = new Scene(root, 500, 320);
        scene.getStylesheets().add(getClass().getResource("../resources/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException, ParseException
    {
        syncDB(); //sync Database (deserialize rooms courses, users)
//        syncCSV(); // serialize rooms and courses from csv
        launch(args);
    }

    private static void syncCSV() throws IOException, ParseException
    {
        // All Courses & All Rooms
        BufferedReader br = new BufferedReader(new FileReader(FILENAME + "/src/database/timetable/finalap.csv"));
        String line = null;
        br.readLine();
        while ((line = br.readLine()) != null) // one course per line
        {
            String[] data = line.split(",");

            String[] tempTimeslot = new String[5];
            String[] tempRooms = new String[5];

            for (int i = 6; i <= 10; i++)
            {
                if (data[i].indexOf("$") == -1)
                {
                    tempTimeslot[i - 6] = "-";
                    tempRooms[i - 6] = "-";
                }
                else
                {
                    data[i] = data[i].replace("$", " ");
                    tempTimeslot[i - 6] = data[i].split(" ")[0];
                    tempRooms[i - 6] = data[i].split(" ")[1];

                    allRooms.add(new Room(data[i].split(" ")[1], 100));

                    SimpleDateFormat format = new SimpleDateFormat("H:mm");
                    for (Room r : allRooms)
                    {
                        if (r.getName().equals(tempRooms[i - 6]))
                        {
                            String[] tis = tempTimeslot[i - 6].split("-");
                            if (Integer.parseInt(tis[0].split(":")[0]) < 8)
                            {
                                tis[0] = Integer.parseInt(tis[0].split(":")[0]) + 12 + ":" + tis[0].split(":")[1];
                            }
                            if (Integer.parseInt(tis[1].split(":")[0]) < 8)
                            {
                                tis[1] = Integer.parseInt(tis[1].split(":")[0]) + 12 + ":" + tis[1].split(":")[1];
                            }

                            ArrayList<ArrayList<Date[]>> temp = r.getTimeIntevals();
                            temp.get(i - 6).add(new Date[]{format.parse(tis[0]), format.parse(tis[1])});
                            break;
                        }
                    }
                }
            }
            Course temp = new Course(data[1], new ArrayList<Course>(), tempTimeslot, tempRooms, new ArrayList<String>(),
                    100, Integer.parseInt(data[4]), data[0], data[2], data[5], data[3]);

            allCourses.add(temp);
        }
        System.out.println(allRooms.toString());

//        All Courses Post Condition
        br = new BufferedReader(new FileReader(FILENAME + "/src/database/timetable/Post-Conditions_Monsoon-2016.csv"));
        line = null;
        br.readLine();
        while ((line = br.readLine()) != null) // one course per line
        {
            String[] data = line.split(",");
            for (Course i : allCourses)
            {
                if (i.getName().toLowerCase().equals(data[1].toLowerCase()))
                {
                    i.setPostConditions(new ArrayList<String>()
                    {{
                        add(data[3]);
                        add(data[4]);
                        add(data[5]);
                        add(data[6]);
                        add(data[7]);
                    }});
                    break;
                }
            }
        }
        System.out.println(allCourses.toString());
        for (Course c : allCourses)
        {
            serialize(c, "courses/" + c.getCode());
        }
        for (Room r : allRooms)
        {
            serialize(r, "rooms/" + r.getName());
        }

    }

    private static void syncDB() throws IOException, ParseException
    {
        // Users Record
        File directory = new File(FILENAME + "/src/database/users/");
        for (File file : directory.listFiles())
        {
            if (file.getName().endsWith(".a"))
            {
                User temp = (User) deserialize(file.getName(), "users");
                allUsers.add(temp);
            }
        }
        System.out.println(allUsers);

        new Admin();
        Admin.requests = (HashMap) deserialize("request.a", "requests");
        System.out.println(Admin.requests);

//        All Courses
        directory = new File(FILENAME + "/src/database/courses/");
        for (File file : directory.listFiles())
        {
            if (file.getName().endsWith(".a"))
            {
                Course temp = (Course) deserialize(file.getName(), "courses");
                allCourses.add(temp);
            }
        }
        System.out.println(allCourses);

//        All Rooms
        directory = new File(FILENAME + "/src/database/rooms/");
        for (File file : directory.listFiles())
        {
            if (file.getName().endsWith(".a"))
            {
                Room temp = (Room) deserialize(file.getName(), "rooms");
                allRooms.add(temp);
            }
        }
        System.out.println(allRooms);
    }

    public static void serialize(Object o, String path) throws IOException
    {
        ObjectOutputStream out = null;
        try
        {
            out = new ObjectOutputStream(new FileOutputStream(FILENAME + "/src/database/" + path + ".a"));
            out.writeObject(o);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        out.close();
    }

    public static Object deserialize(String name, String type) throws IOException
    {
        ObjectInputStream in = null;
        Object temp = null;
        try
        {
            in = new ObjectInputStream(new FileInputStream(FILENAME + "/src/database/" + type + "/" + name));
            temp = (Object) in.readObject();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (in != null) in.close();
        return temp;
    }

    public void sendOTP(ActionEvent event) throws Exception
    {
        goToSignup(new ActionEvent());
        if (1 == 1) return;
        if (sName.getText().trim().toString().isEmpty() || sEmail.getText().trim().toString().isEmpty() || sPassword.getText().toString().isEmpty())
        {
            System.out.println("All fields are mandatory.");
        }
        else if (!validity(sEmail.getText().trim().toString()))
        {
            System.out.println("Input a valid e-mail address.");
        }
        else if (role.getSelectionModel().isEmpty())
        {
            System.out.println("Choose your role.");
        }
        else if (searchUser(sEmail.getText().trim().toString()))
        {
            System.out.println("User with same Email address already exists.");
        }
        else
        {
            // send otp to email address.
            int randomPIN = (int) (Math.random() * 9000) + 1000;
            otp = randomPIN;
            final String user = "classroom.booking.system@gmail.com";//change accordingly
            final String password = "";//change accordingly

            String to = sEmail.getText().trim().toString();//change accordingly

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
                message.setSubject("Registration: Classroom Booking System");
                String messageBody = "Thank you for registering. You OTP is: ";
                messageBody = messageBody + randomPIN + ".";
                message.setText(messageBody);

                //send the message
                Transport.send(message);

                System.out.println("message sent successfully...");
                sName.setDisable(true);
                sEmail.setDisable(true);
                sPassword.setDisable(true);
                role.setDisable(true);
                getOTP.setDisable(true);
                signup.setDisable(false);
                sOTP.setDisable(false);

            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Try again after sometime.");
            }
        }
    }

    public void goToSignup(ActionEvent event) throws Exception
    {
        User user = null;
        String chk = role.getSelectionModel().getSelectedItem().toString();
        if (!(sOTP.getText().trim().toString().equals("" + otp)))
        {
            System.out.println("Enter correct OTP sent to your Email id.");
            sOTP.clear();
//            return;
        }
        if (chk.equals("Admin"))
        {
            user = new Admin(sName.getText().trim().toString(), sEmail.getText().trim().toString().toLowerCase(), "Admin", sPassword.getText().toString());
        }
        else if (chk.equals("Faculty"))
        {
            user = new Faculty(sName.getText().trim().toString(), sEmail.getText().trim().toString().toLowerCase(), "Faculty", sPassword.getText().toString(), new ArrayList<Course>());
        }
        else
        {
            user = new Student(sName.getText().trim().toString(), sEmail.getText().trim().toString().toLowerCase(), "Student", sPassword.getText().toString(), new ArrayList<Course>(), new Timetable());
        }
        serialize(user, "users/" + user.getEmail());
        allUsers.add(user);

        new Signup().start(new Stage());
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    public static boolean searchUser(String email)
    {
        for (User i : allUsers)
        {
            if (i.getEmail().equals(email))
                return true;
        }
        return false;
    }

    public static boolean validity(String trim)
    {
        if (trim.endsWith("@iiitd.ac.in"))
            return true;
        return true;
    }

    public void gotoUser(ActionEvent event) throws Exception
    {
        if (lName.getText().trim().isEmpty() || lPassword.getText().isEmpty())
        {
            System.out.println("All fields are mandatory.");
        }
        else if (!validity(sEmail.getText().trim().toString()))
        {
            System.out.println("Input a valid e-mail address.");
        }
        else
        {
            User user = searchUser(lName.getText().trim().toString().toLowerCase(), lPassword.getText().toString());
            if (user == null)
            {
                System.out.println("Username or Password incorrect.");
                return;
            }

            String chk = user.getType();
            if (chk.equals("Admin"))
            {
                ((Node) (event.getSource())).getScene().getWindow().hide();
                new AdminActivity().go(new Stage(), (Admin) user);
            }
            else if (chk.equals("Faculty"))
            {
                ((Node) (event.getSource())).getScene().getWindow().hide();
                new FacultyActivity().go(new Stage(), (Faculty) user);
            }
            else
            {
                ((Node) (event.getSource())).getScene().getWindow().hide();
                System.out.println(user.getName());
                new StudentActivity().go(new Stage(), (Student) user);
            }
        }
    }

    private User searchUser(String name, String pass)
    {
        for (User user : allUsers)
            if (user.getEmail().equals(name) && user.getPassword().equals(pass))
                return user;
        return null;
    }

    public void forgotPassword(ActionEvent event) throws Exception
    {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        new ForgotPasswordActivity().start(new Stage());
    }

    public static void callPop(String text)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void updateRoomValidity()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.now().plusDays(1);
        for (Room i : allRooms)
        {
            for (Date[] j : i.getBooked())
            {
                if (j[0].compareTo(java.sql.Date.valueOf(localDate)) >= 0)
                {
                    i.getBooked().remove(j);
                }
            }
        }
        for (User i : allUsers)
        {
            for (String s : i.getBookedRoom().keySet())
            {
                for (ArrayList<Object> j : i.getBookedRoom().get(s))
                {
                    Date dd = (Date) j.get(0);
                    if (dd.compareTo(java.sql.Date.valueOf(localDate)) >= 0)
                    {
                        i.getBookedRoom().get(s).remove(j);
                    }
                }
            }
        }
    }
}