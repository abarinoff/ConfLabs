package models.event;

import play.db.ebean.Model;

import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class Stage extends Model {

    @Id
    public Long id;

    public String title;
}
