package models.event;

import models.event.slot.Slot;
import models.event.slot.SpeechSlot;
import org.codehaus.jackson.annotate.JsonIgnore;
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

    @JsonIgnore
    @ManyToMany(mappedBy = "speeches")
    public List<Speaker> speakers = new LinkedList<Speaker>();

    public Speech(String title) {
        this.title = title;
    }

    @Override
    public void delete() {
        Slot slot = Slot.find
                .where()
                .eq("speech_id", id)
                .findUnique();

        if (slot != null) {
            ((SpeechSlot) slot).speech = null;
            slot.update();
        }

        deleteManyToManyAssociations("speakers");
        super.delete();
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
