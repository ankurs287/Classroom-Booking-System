package main.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
/* User class to store users (Admin, Faculty, Student)
* */
public abstract class User implements Serializable
{
    private String name;
    private String email;
    private String type;
    private String password;
    private HashMap<String, ArrayList<ArrayList<Object>>> bookedRoom; // roomname: "start end reason requestStatus", "start end reason requestStatus"

    public User(String name, String email, String type, String password)
    {

        this.name = name;
        this.email = email;
        this.type = type;
        this.password = password;
        bookedRoom = new HashMap<>();
    }

    public User()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getType()
    {
        return type;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", password='" + password + '\'' +
                ",bookedroom=" + bookedRoom + '\'' +
                '}';
    }

    public boolean roomAvailaibility()
    {
        return true;
    }

    public abstract boolean cancelRoomBooking();

    public abstract boolean bookRoom();

    public void viewAllRooms()
    {

    }

    public void getAvailableRooms()
    {

    }

    public HashMap<String, ArrayList<ArrayList<Object>>> getBookedRoom()
    {
        return bookedRoom;
    }
}
