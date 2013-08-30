package models.event.slot;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class OrganizationalSlot extends Slot {

    @Constraints.Required
    public String title;

    protected OrganizationalSlot(String title, Date date, String startTime, String endTime) {
        super(date, startTime, endTime);
    }
}
