package main.utilities;

import main.user.Faculty;
import main.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/*Courses Class to store courses offered by institute.
* */

public class Course implements Serializable
{
    private String name;
    private ArrayList<Course> prerequisites;
    private String[] timeslot;
    private String[] room;
    private ArrayList<String> postConditions;
    private int intendedAudience;
    private int credits;
    private String courseType;
    private String code;
    private String acronym;
    private String instructor;

    public Course(String name, ArrayList<Course> prerequisites, String[] timeslot, String[] room,
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

    public String getName()
    {
        return name;
    }

    public ArrayList<Course> getPrerequisites()
    {
        return prerequisites;
    }

    public String[] getTimeslot()
    {
        return timeslot;
    }

    public void setPostConditions(ArrayList<String> postConditions)
    {
        this.postConditions = postConditions;
    }

    @Override
    public String toString()
    {
        return "Course{" +
                "name='" + name + '\'' +
                ", prerequisites=" + prerequisites +
                ", timeslot=" + Arrays.toString(timeslot) +
                ", room=" + Arrays.toString(room) +
                ", intendedAudience=" + intendedAudience +
                ", credits=" + credits +
                ", courseType='" + courseType + '\'' +
                ", code='" + code + '\'' +
                ", acronym='" + acronym + '\'' +
                ", instructor='" + instructor + '\'' +
                '}';
    }

    public String[] getRoom()
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
