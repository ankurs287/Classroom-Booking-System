package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.user.Admin;
import main.user.Faculty;
import main.user.Student;
import main.user.User;
import main.utilities.Course;
import main.utilities.Room;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class Main extends Application implements Serializable
{
    private static final String FILENAME = (new File("").getAbsolutePath());
    static ArrayList<Course> allCourses = new ArrayList<>();
    static ArrayList<Room> allRooms = new ArrayList<>();
    public static ArrayList<User> allUsers = new ArrayList<>();
    private int otp = -1;

    @FXML
    private GridPane pane;

    @FXML
    private TextField sName;
    @FXML
    private TextField sEmail;
    @FXML
    private PasswordField sPassword;
    @FXML
    private ComboBox role;
    @FXML
    private TextField sOTP;
    @FXML
    private Button getOTP;
    @FXML
    private Button signup;

    @FXML
    private TextField lName;
    @FXML
    private PasswordField lPassword;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("My Classroom");
        primaryStage.setScene(new Scene(root, 400, 280));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException
    {
        syncDB(); //sync Database
        launch(args);
    }

    private static void syncDB() throws IOException
    {
        // Users Record
        File directory = new File(FILENAME + "/src/database/users/");
        for (File file : directory.listFiles())
        {
            if (file.getName().endsWith(".a"))
            {
                User temp = deserialize(file.getName());
                allUsers.add(temp);
            }
        }
        System.out.println(allUsers);

        // All Courses
        BufferedReader br = new BufferedReader(new FileReader(FILENAME + "/src/database/timetable/timetable.csv"));
        String line = null;
        br.readLine();
        while ((line = br.readLine()) != null)
        {
            String[] data = line.split(",");

            ArrayList<String> tempTimeslot = new ArrayList<>();
            for (int i = 6; i <= 10; i++)
                tempTimeslot.add(data[i]);

            Course temp = new Course(data[1], new ArrayList<Course>(), tempTimeslot, new ArrayList<Room>(), new ArrayList<String>(),
                    100, Integer.parseInt(data[4]), data[0], data[2], data[5], data[3]);

            allCourses.add(temp);
        }
        System.out.println(allCourses);

        // All rooms

    }

    private static User deserialize(String name) throws IOException
    {
        ObjectInputStream in = null;
        User temp = null;
        try
        {
            in = new ObjectInputStream(new FileInputStream(FILENAME + "/src/database/users/" + name));
            temp = (User) in.readObject();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        in.close();
        return temp;
    }

    public void sendOTP(ActionEvent event) throws Exception
    {
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
            final String password = "ankur+anvit";//change accordingly

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
            return;
        }
        if (chk.equals("Admin"))
        {
            user = new Admin(sName.getText().trim().toString(), sEmail.getText().trim().toString(), "Admin", sPassword.getText().toString());
        }
        else if (chk.equals("Faculty"))
        {
            user = new Faculty(sName.getText().trim().toString(), sEmail.getText().trim().toString(), "Faculty", sPassword.getText().toString());
        }
        else
        {
            user = new Student(sName.getText().trim().toString(), sEmail.getText().trim().toString(), "Student", sPassword.getText().toString());
        }
        serialize(user);
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
        if(trim.endsWith("@iiitd.ac.in"))
            return true;
        return true;
    }

    public void serialize(User user) throws IOException
    {
        ObjectOutputStream out = null;
        try
        {
            out = new ObjectOutputStream(new FileOutputStream(FILENAME + "/src/database/users/" + user.getEmail() + ".a"));
            out.writeObject(user);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        out.close();
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
            User user = searchUser(lName.getText().trim().toString(), lPassword.getText().toString());
            if (user == null)
            {
                System.out.println("Username or Password incorrect.");
                return;
            }

            String chk = user.getType();
            if (chk.equals("Admin"))
            {
                ((Node) (event.getSource())).getScene().getWindow().hide();
                new AdminActivity().start(new Stage());
            }
            else if (chk.equals("Faculty"))
            {
                ((Node) (event.getSource())).getScene().getWindow().hide();
                new FacultyActivity().start(new Stage());
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
}