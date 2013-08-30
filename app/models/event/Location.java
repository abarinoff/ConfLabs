package models.event;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class Location extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String title;

    public String address;
}
