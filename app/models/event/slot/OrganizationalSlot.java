package models.event.slot;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.avaje.ebean.validation.NotEmpty;

@Entity
@DiscriminatorValue("org")
public class OrganizationalSlot extends Slot {

    @NotEmpty
    public String title;

    public OrganizationalSlot(String title, Date date, String startTime, String endTime) {
        super(date, startTime, endTime);
        this.title = title;
    }
}
