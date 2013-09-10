package models.event;

import play.db.ebean.Model;

import javax.persistence.Id;
import javax.persistence.Entity;

import com.avaje.ebean.validation.NotEmpty;

@Entity
public class Speaker extends Model {

    @Id
    public Long id;

    @NotEmpty
    public String name;

    public String position;

    public String description;

    public Speaker(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;

        Speaker otherSpeaker = (Speaker) other;

        if (id != null ? !id.equals(otherSpeaker.id) : otherSpeaker.id != null) return false;
        if (name != null ? !name.equals(otherSpeaker.name) : otherSpeaker.name != null) return false;
        if (position != null ? !position.equals(otherSpeaker.position) : otherSpeaker.position != null) return false;
        if (description != null ? !description.equals(otherSpeaker.description) : otherSpeaker.description != null) return false;

        return true;
    }

    public static Finder<Long, Speaker> find = new Finder<Long, Speaker>(Long.class, Speaker.class);
}
