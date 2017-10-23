package main.utilities;

public class Room
{
    String name;
    int Capacity;
    int[][] status; // [i]==0:monday ,[][j] j==0: 8-8:30

    public String getName()
    {
        return name;
    }

    public int getCapacity()
    {
        return Capacity;
    }

    public int[][] getStatus()
    {
        return status;
    }

    public void setStatus(int[][] status)
    {
        this.status = status;
    }
}
