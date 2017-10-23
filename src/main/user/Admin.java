package main.user;

public class Admin extends User
{
    public Admin(String name, String email, String type, String password)
    {
        super(name, email, type, password);
    }

    public Admin(){}

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

    public void getRoomsRequests()
    {

    }
}
