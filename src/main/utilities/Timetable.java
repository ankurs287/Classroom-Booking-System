package main.utilities;

import java.util.ArrayList;

public class Timetable
{
    ArrayList<Course> courses;

    public Timetable(ArrayList<Course> courses)
    {
        this.courses = courses;
    }

    public Timetable() {}

    public ArrayList<Course> getCourses()
    {
        return courses;
    }

    public void addCourse(Course course)
    {
        courses.add(course);
    }
}
