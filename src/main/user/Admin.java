package main.user;

import java.util.ArrayList;
import java.util.HashMap;

public class Admin extends User
{
    public static HashMap<String, ArrayList<ArrayList<Object>>> requests = new HashMap<>(); // roomname: "user start end reason", "user start end reason"

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
