package models.event.slot;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import java.util.Date;

@Entity
@Inheritance
public abstract class Slot extends Model {

    @Id
    public Long id;

    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date date;

    @Constraints.Required
    public String startTime;

    @Constraints.Required
    public String endTime;

    protected Slot(Date date, String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }
}
