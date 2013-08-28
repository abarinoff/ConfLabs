package models;

import play.db.ebean.Model;
import com.avaje.ebean.validation.NotEmpty;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "users") // explicit name is specified as "user" is a keyword in Postgress and it will cause conflicts while deploying to Heroku
public class User extends Model {

    @Id
    public Long id;

    @NotEmpty
    public String email;

    @NotEmpty
    public String password;

/*
    @OneToMany(cascade = CascadeType.ALL)
    public List<Event> events = new LinkedList<>();
*/

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static Finder<Long, User> find = new Finder<>(Long.class, User.class);
}
