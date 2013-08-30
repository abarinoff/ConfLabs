package models.event;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Speech extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String title;

    @ManyToMany
    public List<Speaker> speakers;

    public static Finder<Long, Speech> find = new Finder<Long, Speech>(Long.class, Speech.class);
}
