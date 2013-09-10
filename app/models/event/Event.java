package models.event;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;
import com.avaje.ebean.validation.NotEmpty;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;

import models.event.slot.Slot;

@Entity
public class Event extends Model {

    @Id
    public Long id;

    @NotEmpty
    public String title;

    public String description;

    @OneToOne(cascade = CascadeType.ALL)
    public Location location;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Stage> stages = new LinkedList<Stage>();

    @OneToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers = new LinkedList<Speaker>();

    @OneToMany(cascade = CascadeType.ALL)
    public List<Slot> slots;

    public static Finder<Long, Event> find = new Finder<Long, Event>(Long.class, Event.class);

    public Event(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Event event = (Event) o;

        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        if (!id.equals(event.id)) return false;
        if (location != null ? !location.equals(event.location) : event.location != null) return false;
        if (slots != null ? !slots.equals(event.slots) : event.slots != null) return false;
        if (speakers != null ? !speakers.equals(event.speakers) : event.speakers != null) return false;
        if (stages != null ? !stages.equals(event.stages) : event.stages != null) return false;
        if (!title.equals(event.title)) return false;

        return true;
    }

    @Override
    public void delete() {
        Ebean.delete(slots);
        super.delete();
    }
}
