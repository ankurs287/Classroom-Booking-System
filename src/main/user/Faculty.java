package main.user;

import main.utilities.Course;

import java.util.ArrayList;

public class Faculty extends User
{
    ArrayList<Course> courses;
    public Faculty(String name, String email, String type, String password)
    {
        super(name, email, type, password);
    }

    public Faculty(){}

    public ArrayList<Course> getCourses()
    {
        return courses;
    }

    public void addCourse(Course course)
    {
        courses.add(course);
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


}
