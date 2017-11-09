package main.user;

import main.utilities.Course;
import main.utilities.Timetable;

import java.util.ArrayList;
import java.util.Currency;

public class Student extends User
{
    ArrayList<Course> courses = new ArrayList<>();
    Timetable myTimetable = new Timetable();

    public Student(String name, String email, String type, String password, ArrayList<Course> courses, Timetable myTimetable)
    {
        super(name, email, type, password);
        this.courses = courses;
        this.myTimetable = myTimetable;
    }

    public Student()
    {
    }

    public ArrayList<Course> getCourses()
    {
        return courses;
    }

    public void addCourse(Course course)
    {
        courses.add(course);
    }

    public Timetable getMyTimetable()
    {
        return myTimetable;
    }

    public ArrayList<Course> search(String string)
    {
        return new ArrayList<Course>();
    }

    public ArrayList<Course> viewAllCourses()
    {
        return new ArrayList<>();
    }

    public ArrayList<Course> recommendedCourses()
    {
        return new ArrayList<Course>();
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
