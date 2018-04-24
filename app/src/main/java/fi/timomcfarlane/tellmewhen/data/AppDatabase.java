package fi.timomcfarlane.tellmewhen.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import fi.timomcfarlane.tellmewhen.data.model.Appointment;
/**
 * Abstract Class is used as a link between the application and the Room persistence library
 * (SQLite database)
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
@Database(entities = {Appointment.class}, version = 1)
@TypeConverters({DataTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppointmentDao appointmentDao();
}
