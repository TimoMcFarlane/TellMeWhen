package fi.timomcfarlane.tellmewhen.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import fi.timomcfarlane.tellmewhen.data.model.Appointment;

@Database(entities = {Appointment.class}, version = 1)
@TypeConverters({DataTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppointmentDao appointmentDao();
}
