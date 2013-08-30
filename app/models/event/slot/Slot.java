package models.event.slot;

import java.util.Date;

import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import com.avaje.ebean.validation.NotEmpty;

@Entity
@Inheritance
public abstract class Slot extends Model {

    @Id
    public Long id;

    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date date;

    @NotEmpty
    public String startTime;

    @NotEmpty
    public String endTime;

    public Slot(Date date, String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }
}
