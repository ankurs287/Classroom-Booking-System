package main.utilities;

import main.user.Faculty;
import main.user.User;

import java.util.ArrayList;

public class Course
{
    private String name;
    private ArrayList<Course> prerequisites;
    private ArrayList<String> timeslot;
    private ArrayList<Room> room;
    private ArrayList<String> postConditions;
    private int intendedAudience;
    private int credits;
    private String courseType;
    private String code;
    private String acronym;
    private String instructor;

    public Course(String name, ArrayList<Course> prerequisites, ArrayList<String> timeslot, ArrayList<Room> room,
            ArrayList<String> postConditions, int intendedAudience, int credits, String courseType,
            String code, String acronym, String instructor)
    {
        this.name = name;
        this.prerequisites = prerequisites;
        this.timeslot = timeslot;
        this.room = room;
        this.postConditions = postConditions;
        this.intendedAudience = intendedAudience;
        this.credits = credits;
        this.courseType = courseType;
        this.code = code;
        this.acronym = acronym;
        this.instructor = instructor;
    }

    @Override
    public String toString()
    {
        return "Course{" +
                "name='" + name + '\'' +
                ", prerequisites=" + prerequisites +
                ", timeslot=" + timeslot +
                ", room=" + room +
                ", postConditions=" + postConditions +
                ", intendedAudience=" + intendedAudience +
                ", credits=" + credits +
                ", courseType='" + courseType + '\'' +
                ", code='" + code + '\'' +
                ", acronym='" + acronym + '\'' +
                ", instructor='" + instructor + '\'' +
                '}';
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<Course> getPrerequisites()
    {
        return prerequisites;
    }

    public ArrayList<String> getTimeslot()
    {
        return timeslot;
    }

    public ArrayList<Room> getRoom()
    {
        return room;
    }

    public ArrayList<String> getPostConditions()
    {
        return postConditions;
    }

    public int getIntendedAudience()
    {
        return intendedAudience;
    }

    public int getCredits()
    {
        return credits;
    }

    public String getCourseType()
    {
        return courseType;
    }

    public String getCode()
    {
        return code;
    }

    public String getAcronym()
    {
        return acronym;
    }

    public String getInstructor()
    {
        return instructor;
    }
}
