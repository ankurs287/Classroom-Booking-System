package main;

import main.utilities.Course;
import main.utilities.Room;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static main.Main.deserialize;
import static main.Main.serialize;

/* Initial Class to create database*/

public class Creator
{
    private static final String FILENAME = (new File("").getAbsolutePath());

    public static void main(String[] args) throws IOException, ParseException
    {
        File f1 = new File(FILENAME + "/src/database/courses.a");
        File f2 = new File(FILENAME + "/src/database/rooms.a");
        if ((!f1.exists()) || ((!f2.exists())))
            syncCSV(); // serialize rooms and courses from csv
    }

    private static void syncCSV() throws IOException, ParseException
    {
        System.out.println("Synced from CSV");
        // All Courses & All Rooms
        BufferedReader br = new BufferedReader(new FileReader(FILENAME + "/src/database/timetable/finalap.csv"));
        String line = null;
        br.readLine();
        while ((line = br.readLine()) != null) // one course per line
        {
            String[] data = line.split(",");

            String[] tempTimeslot = new String[5];
            String[] tempRooms = new String[5];

            for (int i = 6; i <= 10; i++)
            {
                if (data[i].indexOf("$") == -1)
                {
                    tempTimeslot[i - 6] = "-";
                    tempRooms[i - 6] = "-";
                }
                else
                {
                    data[i] = data[i].replace("$", " ");
                    tempTimeslot[i - 6] = data[i].split(" ")[0];
                    tempRooms[i - 6] = data[i].split(" ")[1];

                    Main.allRooms.add(new Room(data[i].split(" ")[1], 100));

                    SimpleDateFormat format = new SimpleDateFormat("H:mm");
                    for (Room r : Main.allRooms)
                    {
                        if (r.getName().equals(tempRooms[i - 6]))
                        {
                            String[] tis = tempTimeslot[i - 6].split("-");
                            if (Integer.parseInt(tis[0].split(":")[0]) < 8)
                            {
                                tis[0] = Integer.parseInt(tis[0].split(":")[0]) + 12 + ":" + tis[0].split(":")[1];
                            }
                            if (Integer.parseInt(tis[1].split(":")[0]) < 8)
                            {
                                tis[1] = Integer.parseInt(tis[1].split(":")[0]) + 12 + ":" + tis[1].split(":")[1];
                            }

                            ArrayList<ArrayList<Date[]>> temp = r.getTimeIntevals();
                            temp.get(i - 6).add(new Date[]{format.parse(tis[0]), format.parse(tis[1])});
                            break;
                        }
                    }
                }
            }
            Course temp = new Course(data[1], new ArrayList<Course>(), tempTimeslot, tempRooms, new ArrayList<String>(),
                    100, Integer.parseInt(data[4]), data[0], data[2], data[5], data[3]);

            Main.allCourses.add(temp);
        }

//        All Courses Post Condition
        br = new BufferedReader(new FileReader(FILENAME + "/src/database/timetable/Post-Conditions_Monsoon-2016.csv"));
        line = null;
        br.readLine();
        while ((line = br.readLine()) != null) // one course per line
        {
            String[] data = line.split(",");
            for (Course i : Main.allCourses)
            {
                if (i.getName().toLowerCase().equals(data[1].toLowerCase()))
                {
                    i.setPostConditions(new ArrayList<String>()
                    {{
                        add(data[3]);
                        add(data[4]);
                        add(data[5]);
                        add(data[6]);
                        add(data[7]);
                    }});
                    break;
                }
            }
        }
        System.out.println(Main.allCourses.toString());

        serialize(Main.allRooms, "rooms.a");
        serialize(Main.allCourses, "courses.a");
    }
}
