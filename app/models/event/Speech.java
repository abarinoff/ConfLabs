package models.event;

import play.db.ebean.Model;
import com.avaje.ebean.validation.NotEmpty;

import javax.persistence.*;

import java.util.List;
import java.util.LinkedList;

@Entity
public class Speech extends Model {

    @Id
    public Long id;

    @NotEmpty
    public String title;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers = new LinkedList<Speaker>();

    public Speech(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Speech speech = (Speech) o;

        if (!id.equals(speech.id)) return false;
        if (!speakers.equals(speech.speakers)) return false;
        if (!title.equals(speech.title)) return false;

        return true;
    }

    public static Finder<Long, Speech> find = new Finder<Long, Speech>(Long.class, Speech.class);
}
