package fi.timomcfarlane.tellmewhen.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import fi.timomcfarlane.tellmewhen.data.model.Appointment;

@Dao
public interface AppointmentDao {
    @Query("SELECT * FROM appointment ORDER BY date(date) ASC, time(time) ASC")
    List<Appointment> getAll();

    // WEEKLY QUERY
    // http://help.grow.com/manipulating-data-with-query-languages/sqlite/common-sqlite-queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAppointments(Appointment... appointments);

    @Update
    public void updateAppointments(Appointment... appointments);

    @Delete
    public void deleteAppointments(Appointment... appointments);
}
