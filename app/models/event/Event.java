package models.event;

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
    public List<Stage> stages;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers = new LinkedList<Speaker>();

    @OneToMany(cascade = CascadeType.ALL)
    public List<Slot> slots;

    public static Finder<Long, Event> find = new Finder<Long, Event>(Long.class, Event.class);
}
