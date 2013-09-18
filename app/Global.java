import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import models.authentication.SecurityRole;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;

import models.authentication.User;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;
import play.mvc.Call;
import resolvers.DefaultResolver;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
        super.onStart(app);

		PlayAuthenticate.setResolver(new DefaultResolver());

		initializeDatabase();
	}

	private void initializeDatabase() {
        if (SecurityRole.find.findRowCount() == 0) {
            for (final String roleName : Arrays
                    .asList(controllers.Application.USER_ROLE)) {
                final SecurityRole role = new SecurityRole();
                role.roleName = roleName;
                role.save();
            }
        }

        if(User.find.findRowCount() == 0) {
            Map<String, List> all = (Map<String, List>) Yaml.load("initial-data.yml");
            Ebean.save(all.get("users"));
        }
	}
}