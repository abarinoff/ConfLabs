package models.event;

import com.avaje.ebean.Ebean;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;
import com.avaje.ebean.validation.NotEmpty;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;

import models.event.slot.Slot;
import util.ModelMergeHelper;

@Entity
public class Event extends AbstractModel {

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
    public static Finder<Long, Event> find = new Finder<Long, Event>(Long.class, Event.class);

    @JsonIgnore
    @Version
    public Timestamp lastUpdate;

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

    @Override
    public void delete() {
        Ebean.delete(slots);
        super.delete();
    }

    // @todo Use parametrization
    @Override
    public void merge(AbstractModel eventToMerge) {
        Event event = ((Event) eventToMerge);

        this.title = event.title;
        this.description = event.description;

        List<Stage> stagesToRemove = mergeOneToManyAssociations(this.stages, event.stages);
        ModelMergeHelper.resetSlotStageReference(stagesToRemove);
        for(Stage stage : stagesToRemove) {
            stage.delete();
        }

        // Stages
        /*List<Stage> stagesToMerge = new LinkedList<Stage>(event.stages);
        List<Stage> stagesToRemove = new LinkedList<Stage>();

        for(int i = 0; i < stages.size(); i++) {
            Stage stage = stages.get(i);
            for (int j = 0; j < stagesToMerge.size(); j++) {
                Stage mergeStage = stagesToMerge.get(j);
                if (stage.getModelId() == mergeStage.getModelId()) {
                    stage.merge(mergeStage);
                    stagesToMerge.remove(j);
                    break;
                }
                if (j >= stagesToMerge.size() - 1) {
                    stagesToRemove.add(stage);
                }
            }
        }

        // Remove from Original collection elements that are not present in Supplied collection
        stages.removeAll(stagesToRemove);
        stages.addAll(stagesToMerge);

        ModelMergeHelper.resetSlotStageReference(stagesToRemove);

        for(Stage stage : stagesToRemove) {
            stage.delete();
        }*/
    }

    @Override
    public Long getModelId() {
        return id;
    }
}