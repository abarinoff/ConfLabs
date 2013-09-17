package models.event;

import com.avaje.ebean.validation.NotEmpty;
import org.codehaus.jackson.annotate.JsonIgnore;
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

    @JsonIgnore
    public static Finder<Long, Location> find = new Finder<Long, Location>(Long.class, Location.class);

    public Location(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Location location = (Location) o;

        if (address != null ? !address.equals(location.address) : location.address != null) return false;
        if (!id.equals(location.id)) return false;
        if (!title.equals(location.title)) return false;

        return true;
    }
}
