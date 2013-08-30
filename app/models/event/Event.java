package models.event;

import models.authentication.User;
import play.db.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
public class Event extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String title;

    public String description;

    @OneToOne
    public Location location;

/*
    @OneToMany(cascade = CascadeType.ALL)
    public List<Stage> stages = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL)
    public List<Slot> slots = new LinkedList<>();
*/

    public static Finder<Long, Event> finder = new Finder<>(Long.class, Event.class);
}
