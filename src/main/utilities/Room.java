package main.utilities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Room implements Serializable
{
    String name;
    int Capacity;
    ArrayList<ArrayList<Date[]>> timeIntevals; //TT
    ArrayList<Date[]> booked; // booked by user
    public Room(String name, int capacity)
    {
        this.name = name;
        Capacity = capacity;

        timeIntevals = new ArrayList<>();
        for (int i = 0; i < 7; i++)
        {
            timeIntevals.add(new ArrayList<>());
        }

        booked = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }

    public int getCapacity()
    {
        return Capacity;
    }

    public static int compute(String j) throws ParseException
    {
        if (Integer.parseInt(j.split(":")[0]) < 8)
        {
            j = Integer.parseInt(j.split(":")[0]) + 12 + ":" + j.split(":")[1];
        }

        String time1 = "08:00:00";
        String time2 = j + ":00";

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(time1);
        Date date2 = format.parse(time2);
        long diff = (date2.getTime() - date1.getTime()) / 60000;
        diff /= 30; // scale

        return (int) diff;
    }

    public ArrayList<Date[]> getBooked()
    {
        return booked;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        return getName().equals(room.getName());
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public String toString()
    {
        return "Room{" +
                "name='" + name + '\'' +
                '}';
    }

    public static String print(ArrayList<ArrayList<Date[]>> a)
    {
        int row, column;
        String aString = "";
        SimpleDateFormat format = new SimpleDateFormat("H:mm");
        for (int i = 0; i < 7; i++)
        {
            aString += (i + ": \n");
            for (int j = 0; j < a.get(i).size(); j++)
            {
                aString += (format.format(a.get(i).get(j)[0]) + " - " + format.format(a.get(i).get(j)[1]) + " , ");
            }
            aString += "\n";
        }
        return aString;
    }

    public ArrayList<ArrayList<Date[]>> getTimeIntevals()
    {
        return timeIntevals;
    }
}
