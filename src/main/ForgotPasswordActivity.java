package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.user.User;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static main.Main.allUsers;
import static main.Main.searchUser;

public class ForgotPasswordActivity extends Application
{
    private int otp = -1;

    @FXML
    private TextField fEmail;
    @FXML
    private TextField fOTP;
    @FXML
    private PasswordField fPassword;
    @FXML
    private Button getotp;
    @FXML
    private Button resetbtn;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("forgotpasswordactivity.fxml"));
        primaryStage.setTitle("My Classroom");
        primaryStage.setScene(new Scene(root, 300, 160));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void getOTP(ActionEvent event)
    {
        if (fEmail.getText().trim().toString().isEmpty() || fPassword.getText().toString().isEmpty())
        {
            System.out.println("All fields are mandantory");
        }
        else if (!searchUser(fEmail.getText().trim().toString()))
        {
            System.out.println("Email ID does not exist");
        }
        else
        {
            // send otp to email address.
            int randomPIN = (int) (Math.random() * 9000) + 1000;
            otp = randomPIN;
            final String user = "classroom.booking.system@gmail.com";//change accordingly
            final String password = "ankur+anvit";//change accordingly

            String to = fEmail.getText().trim().toString();//change accordingly

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
                fEmail.setDisable(true);
                getotp.setDisable(true);
                fOTP.setDisable(false);
                resetbtn.setDisable(false);

            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Try again after sometime.");
            }
        }
    }

    public void reset(ActionEvent event)
    {
        if (!fOTP.getText().toString().equals("" + otp))
        {
            System.out.println("Please Enter Correct OTP sent to your Email id");
            return;
        }
        for (User i : allUsers)
        {
            if (i.getEmail().equals(fEmail.getText().trim().toString()))
            {
                i.setPassword(fPassword.getText().toString());
                System.out.println("Password Changed Succesfully.");
                return;
            }
        }
    }

    public void goHome(ActionEvent event)
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
}
