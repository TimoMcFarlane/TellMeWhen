package fi.timomcfarlane.tellmewhen;

import java.util.Date;

public class Appointment {
    private String title;
    private String address;
    private Date date;
    private String notes;
    private String time;
    private String category;

    public Appointment(String title){this.title = title;}

    public Appointment(String title, Date date, String time, String category) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public Appointment(String title, String address, Date date, String time, String category) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public Appointment(String title, String address, Date date, String time, String notes, String category) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.category = category;
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


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return this.title;
    }
}

