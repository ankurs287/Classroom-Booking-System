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
import java.io.IOException;
import java.util.Properties;

import static main.Main.*;
/*To recover forgotten password*/
public class ForgotPasswordActivity extends Application
{
    private int otp = -1;

    @FXML
    private TextField fEmail, fOTP;
    @FXML
    private PasswordField fPassword;
    @FXML
    private Button getotp, resetbtn;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("forgotpasswordactivity.fxml"));
        primaryStage.setTitle("My Classroom");
        Scene scene = new Scene(root, 344, 205);
        scene.getStylesheets().add(getClass().getResource("../resources/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // get OTP to E-mail
    public void getOTP(ActionEvent event)
    {
        if (fEmail.getText().trim().toString().isEmpty() || fPassword.getText().toString().isEmpty())
        {
            System.out.println("All fields are mandantory");
            Main.callPop("All fields are mandantory");
        }
        else if (!searchUser(fEmail.getText().trim().toString()))
        {
            System.out.println("Email ID does not exist");
            Main.callPop("Email ID does not exist");
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
                Main.callPop("OTP sent successfully. Check you e-mail.");
                fEmail.setDisable(true);
                getotp.setDisable(true);
                fOTP.setDisable(false);
                resetbtn.setDisable(false);

            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Try again after sometime.");
                Main.callPop("Try again after sometime.");
            }
        }
    }

    // check if OTP is correct and reset password
    public void reset(ActionEvent event) throws IOException
    {
        if (!fOTP.getText().toString().equals("" + otp))
        {
            System.out.println("Please Enter Correct OTP sent to your Email id");
            Main.callPop("Please Enter Correct OTP sent to your Email id");
            return;
        }
        for (User i : allUsers)
        {
            if (i.getEmail().equals(fEmail.getText().trim().toString()))
            {
                i.setPassword(fPassword.getText().toString());
                System.out.println("Password Changed Succesfully.");
                Main.callPop("Password Changed Succesfully.");
                return;
            }
        }
        setUpdate();
    }

    // go back t login/sign up
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
