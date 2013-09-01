package models.event;

import com.avaje.ebean.validation.NotEmpty;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class Location extends Model {

    @Id
    public Long id;

    @NotEmpty
    public String title;

    public String address;

    public static Finder<Long, Location> find = new Finder<Long, Location>(Long.class, Location.class);
}
