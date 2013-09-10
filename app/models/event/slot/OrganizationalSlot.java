package models.event.slot;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

import com.avaje.ebean.validation.NotEmpty;

@Entity
@DiscriminatorValue("org")
public class OrganizationalSlot extends Slot {

    @NotEmpty
    public String title;

    public OrganizationalSlot(Date start, Date end, String title) {
        super(start, end);
        this.title = title;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;

        OrganizationalSlot otherSlot = (OrganizationalSlot) other;

        if (title != null ? !title.equals(otherSlot.title) : otherSlot.title != null) return false;

        return true;
    }
}
