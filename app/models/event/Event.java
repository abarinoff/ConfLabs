package models.event;

import com.avaje.ebean.Ebean;
import models.authentication.User;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;
import play.db.ebean.Model;
import com.avaje.ebean.validation.NotEmpty;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;

import models.event.slot.Slot;

@Entity
public class Event extends Model {

    @JsonView(util.JsonView.ShortView.class)
    @Id
    public Long id;

    @JsonView(util.JsonView.ShortView.class)
    @NotEmpty
    public String title;

    @JsonView(util.JsonView.FullView.class)
    public String description;

    @JsonView(util.JsonView.FullView.class)
    @OneToOne(cascade = CascadeType.ALL)
    public Location location;

    @JsonView(util.JsonView.FullView.class)
    @OneToMany(cascade = CascadeType.ALL)
    public List<Stage> stages = new LinkedList<Stage>();

    @JsonView(util.JsonView.FullView.class)
    @OneToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers = new LinkedList<Speaker>();

    @JsonView(util.JsonView.FullView.class)
    @OneToMany(cascade = CascadeType.ALL)
    public List<Speech> speeches = new LinkedList<Speech>();

    @JsonView(util.JsonView.FullView.class)
    @OneToMany(cascade = CascadeType.ALL)
    public List<Slot> slots = new LinkedList<Slot>();

    @JsonIgnore
    @ManyToOne
    public User user;

    @JsonIgnore
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
        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (location != null ? !location.equals(event.location) : event.location != null) return false;
        if (!slots.equals(event.slots)) return false;
        if (!speakers.equals(event.speakers)) return false;
        if (!speeches.equals(event.speeches)) return false;
        if (!stages.equals(event.stages)) return false;
        if (!title.equals(event.title)) return false;

        return true;
    }

    public Stage getStageById(Long id) {
        for(Stage stage : stages) {
            if (stage.id.equals(id)) {
                return stage;
            }
        }

        return null;
    }

    public Speaker getSpeakerById(Long id) {
        for(Speaker speaker : speakers) {
            if (speaker.id.equals(id)) {
                return speaker;
            }
        }

        return null;
    }

    @Override
    public void delete() {
        Ebean.delete(slots);
        super.delete();
    }
}