package models.event;

import play.db.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class Speaker extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String name;

    public String description;

    public Speaker(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
