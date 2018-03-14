package fi.timomcfarlane.tellmewhen;

import java.util.Date;

public class Appointment {
    private String title;
    private String address;
    private Date date;
    private String notes;
    private String time;


    public Appointment(String title){this.title = title;}

    public Appointment(String title, Date date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public Appointment(String title, String address, Date date, String notes, String time) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.notes = notes;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return this.title;
    }
}

