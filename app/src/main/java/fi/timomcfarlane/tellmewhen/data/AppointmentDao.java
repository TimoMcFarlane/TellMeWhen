package fi.timomcfarlane.tellmewhen.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import fi.timomcfarlane.tellmewhen.data.model.Appointment;
/**
 * Data Access Object interface used for querying the SQLite database inside
 * Room persistence library.
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
@Dao
public interface AppointmentDao {
    /**
     * Get all appointments that have a date of now and later used for recreating alarms
     * if device was shutdown/booted
     *
     * @return List of matching appointments
     */
    @Query("SELECT * FROM appointment"+
            " WHERE date(date) >= date('now')" +
            " ORDER BY date(date) ASC, time(time) ASC")
    List<Appointment> getAllFutureAppointments();

    /**
     * Get all this weeks appointments
     *
     * @param date First day of given week used to compare and retrieve all this weeks appointments.
     * @return List of requested weeks appointents
     */
    @Query("SELECT * FROM appointment"+
            " WHERE date(date) >= date(:date, '-7 days')"+
            " AND date(date) < date(:date)" +
            " ORDER BY date(date) ASC, time(time) ASC")
    List<Appointment> getAll(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAppointments(Appointment... appointments);

    @Update
    public void updateAppointments(Appointment... appointments);

    @Delete
    public void deleteAppointments(Appointment... appointments);
}
