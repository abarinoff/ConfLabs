package models.event;

import play.db.ebean.Model;
import play.data.validation.Constraints;

import java.util.List;
import javax.persistence.*;

import models.event.slot.Slot;

@Entity
public class Event extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String title;

    public String description;

    @OneToOne
    public Location location;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Stage> stages;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Slot> slots;

    public static Finder<Long, Event> find = new Finder<Long, Event>(Long.class, Event.class);
}
