package fi.timomcfarlane.tellmewhen;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "appointment")
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "date")
    public String date;
    @ColumnInfo(name = "time")
    private String time;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "notes")
    private String notes;
    @ColumnInfo(name = "alarms")
    private String alarms;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public String getAlarms() {
        return alarms;
    }

    public void setAlarms(String alarms) {
        this.alarms += "," + alarms;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", category='" + category + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    public Appointment(){}

    public Appointment(String title, String date, String time, String category) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public Appointment(String title, String address, String date, String time, String category) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public Appointment(String title, String address, String date, String time, String notes, String category) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.category = category;
    }
}

