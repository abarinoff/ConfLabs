package models.event;

import com.avaje.ebean.Ebean;
import org.codehaus.jackson.annotate.JsonIgnore;
import play.db.ebean.Model;

import javax.persistence.*;

import com.avaje.ebean.validation.NotEmpty;

import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;

@Entity
public class Stage extends AbstractModel {

    @Id
    public Long id;

    @NotEmpty
    public String title;

    public int capacity;

    @Version
    @JsonIgnore
    public Timestamp lastUpdate;

    /*@ManyToOne
    @JsonIgnore
    public Event event;*/

    public Stage(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;

        Stage otherStage = (Stage) other;

        if (id != null ? !id.equals(otherStage.id) : otherStage.id != null) return false;
        if (title != null ? !title.equals(otherStage.title) : otherStage.title != null) return false;
        if (capacity != otherStage.capacity) return false;

        return true;
    }

    public static Finder<Long, Stage> find = new Finder<Long, Stage>(Long.class, Stage.class);

    @Override
    public void merge(AbstractModel stageToMerge) {
        Stage stage = ((Stage) stageToMerge);
        this.id = stage.id;
        this.title = stage.title;
        this.capacity = stage.capacity;
    }

    @Override
    public Long getModelId() {
        return id;
    }
}
