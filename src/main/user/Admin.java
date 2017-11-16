package main.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Admin extends User implements Serializable
{
    public static HashMap<String, ArrayList<ArrayList<Object>>> requests=new HashMap<>(); // roomname: "user start end reason date", "user start end reason date"

    public Admin(String name, String email, String type, String password)
    {
        super(name, email, type, password);
    }

    public Admin()
    {
    }

    @Override
    public boolean cancelRoomBooking()
    {
        return false;
    }

    @Override
    public boolean bookRoom()
    {
        return false;
    }

    public HashMap<String, ArrayList<ArrayList<Object>>> getRequests()
    {
        return requests;
    }
}
