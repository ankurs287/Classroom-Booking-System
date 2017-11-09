package main.user;

import main.utilities.Course;

import java.util.ArrayList;

public class Faculty extends User
{
    ArrayList<Course> courses;

    public Faculty(String name, String email, String type, String password, ArrayList<Course> courses)
    {
        super(name, email, type, password);
        this.courses = courses;
    }

    public Faculty(ArrayList<Course> courses)
    {
        this.courses = courses;
    }

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
