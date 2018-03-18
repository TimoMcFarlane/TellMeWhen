package fi.timomcfarlane.tellmewhen;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Appointment.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppointmentDao appointmentDao();
}
