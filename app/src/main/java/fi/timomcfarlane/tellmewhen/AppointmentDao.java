package fi.timomcfarlane.tellmewhen;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Query("SELECT * FROM appointment")
    List<Appointment> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAppointments(Appointment... appointments);

    @Update
    public void updateAppointments(Appointment... appointments);

    @Delete
    public void deleteAppointments(Appointment... appointments);
}
