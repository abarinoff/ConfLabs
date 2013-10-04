package models.event.slot;

import java.util.Date;
import java.util.List;

import play.db.ebean.Model;

import javax.persistence.*;

import com.avaje.ebean.validation.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
/*@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SpeechSlot.class, name = "speech"),
        @JsonSubTypes.Type(value = OrganizationalSlot.class, name="organizational")
})*/
public abstract class Slot extends Model {

    @Id
    public Long id;

    @NotNull
    public Date start;

    @NotNull
    public Date end;

    public Slot(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;

        Slot otherSlot = (Slot) other;

        if (id != null ? !id.equals(otherSlot.id) : otherSlot.id != null) return false;
        if (start != null ? !start.equals(otherSlot.start) : otherSlot.start != null) return false;
        if (end != null ? !end.equals(otherSlot.end) : otherSlot.end != null) return false;

        return true;
    }

    public static Finder<Long, Slot> find = new Finder<Long, Slot>(Long.class, Slot.class);
}
