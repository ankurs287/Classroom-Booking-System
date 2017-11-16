package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.time.LocalDate;
import java.util.*;

/*Main Class for login / sign up and database reading*/
public class Main extends Application implements Serializable
{
    private static final String FILENAME = (new File("").getAbsolutePath());
    public static ArrayList<Course> allCourses = new ArrayList<>();
    public static HashSet<Room> allRooms = new HashSet<Room>();
    public static ArrayList<User> allUsers = new ArrayList<>();
    private int otp = -1;

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
        launch(args);
    }

    // sync Database
    private static void syncDB() throws IOException, ParseException
    {
        File f = new File(FILENAME + "/src/database/users.a");
        if (f.exists() && !f.isDirectory())
            allUsers = (ArrayList<User>) deserialize("users.a");

//        f = new File(FILENAME + "/src/database/courses.a");
//        if (f.exists() && !f.isDirectory())
        allCourses = (ArrayList<Course>) deserialize("courses.a");

//        f = new File(FILENAME + "/src/database/rooms.a");
//        if (f.exists() && !f.isDirectory())
        allRooms = (HashSet<Room>) deserialize("rooms.a");

        new Admin();
        f = new File(FILENAME + "/src/database/requests.a");
        if (f.exists() && !f.isDirectory())
        {
            Admin.requests = (HashMap) deserialize("requests.a");
        }
        else serialize(Admin.requests, "requests.a");

        System.out.println("Requests:" + Admin.requests);

        System.out.println("Users: " + Main.allUsers);
        //  All Courses
        System.out.println("Courses: " + Main.allCourses);
        //  All Rooms
        System.out.println("ROOMS: " + Main.allRooms);
    }

    public static void serialize(Object o, String path) throws IOException
    {
        ObjectOutputStream out = null;
        try
        {
            out = new ObjectOutputStream(new FileOutputStream(FILENAME + "/src/database/" + path));
            out.writeObject(o);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        out.close();
    }

    public static Object deserialize(String name) throws IOException
    {
        ObjectInputStream in = null;
        Object temp = null;
        try
        {
            in = new ObjectInputStream(new FileInputStream(FILENAME + "/src/database/" + name));
            temp = (Object) in.readObject();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (in != null) in.close();
        return temp;
    }

    // send OTP for signup process
    public void sendOTP(ActionEvent event) throws Exception
    {
        goToSignup(new ActionEvent());
        if (1 == 1) return;
        if (sName.getText().trim().toString().isEmpty() || sEmail.getText().trim().toString().isEmpty() || sPassword.getText().toString().isEmpty())
        {
            System.out.println("All fields are mandatory.");
            callPop("All fields are mandatory.");
        }
        else if (!validity(sEmail.getText().trim().toString()))
        {
            System.out.println("Input a valid e-mail address.");
            callPop("Input a valid e-mail address.");
        }
        else if (role.getSelectionModel().isEmpty())
        {
            System.out.println("Choose your role.");
            callPop("Choose your role.");
        }
        else if (searchUser(sEmail.getText().trim().toString()))
        {
            System.out.println("User with same Email address already exists.");
            callPop("User with same Email address already exists.");
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
                callPop("OTP sent successfully. Check your e-mail.");
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
                callPop("Try again after sometime.");
            }
        }
    }

    // if OTP is correct go to Sign up activity
    public void goToSignup(ActionEvent event) throws Exception
    {
        User user = null;
        String chk = role.getSelectionModel().getSelectedItem().toString();
        if (!(sOTP.getText().trim().toString().equals("" + otp)))
        {
            System.out.println("Enter correct OTP sent to your Email id.");
            callPop("Enter correct OTP sent to your Email id.");
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
        allUsers.add(user);
        serialize(allUsers, "users.a");

        callPop("Thanks for Signing up. You may login now.");
    }

    // check if user exists
    public static boolean searchUser(String email)
    {
        for (User i : Main.allUsers)
        {
            if (i.getEmail().equals(email))
                return true;
        }
        return false;
    }

    // check if email is correct or not
    public static boolean validity(String trim)
    {
        if (trim.endsWith("@iiitd.ac.in"))
            return true;
        return true;
    }

    // login is credentials are correct
    public void gotoUser(ActionEvent event) throws Exception
    {
        if (lName.getText().trim().isEmpty() || lPassword.getText().isEmpty())
        {
            System.out.println("All fields are mandatory.");
            callPop("All fields are mandatory.");
        }
        else if (!validity(sEmail.getText().trim().toString()))
        {
            System.out.println("Input a valid e-mail address.");
            callPop("Input a valid e-mail address.");
        }
        else
        {
            User user = searchUser(lName.getText().trim().toString().toLowerCase(), lPassword.getText().toString());
            if (user == null)
            {
                System.out.println("Username or Password incorrect.");
                callPop("Username or Password incorrect.");
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
                new StudentActivity().go(new Stage(), (Student) user);
            }
        }
    }

    // check if user exists
    private User searchUser(String name, String pass)
    {
        for (User user : Main.allUsers)
            if (user.getEmail().equals(name) && user.getPassword().equals(pass))
                return user;
        return null;
    }

    // recover forget password
    public void forgotPassword(ActionEvent event) throws Exception
    {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        new ForgotPasswordActivity().start(new Stage());
    }

    // pop ups for wrong input data
    public static void callPop(String text)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    // get updated database
    public static User getUpdate(User user) throws IOException
    {
        Main.allCourses = (ArrayList<Course>) deserialize("courses.a");
        Main.allRooms = (HashSet<Room>) deserialize("rooms.a");
        Main.allUsers = (ArrayList<User>) deserialize("users.a");
        Admin.requests = (HashMap<String, ArrayList<ArrayList<Object>>>) deserialize("requests.a");

        for (User u : Main.allUsers)
        {
            if (u.getEmail().equals(user.getEmail()))
                return u;
        }
        return null;
    }

    // update database
    public static void setUpdate() throws IOException
    {
        serialize(Main.allCourses, "courses.a");
        serialize(Main.allRooms, "rooms.a");
        serialize(Main.allUsers, "users.a");
        serialize(Admin.requests, "requests.a");
    }
}