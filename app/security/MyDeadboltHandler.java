package security;

import models.authentication.User;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.core.models.Subject;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUserIdentity;

public class MyDeadboltHandler extends AbstractDeadboltHandler {

	@Override
	public Result beforeAuthCheck(final Http.Context context) {
		if (PlayAuthenticate.isLoggedIn(context.session())) {
			return null;
		} else {
			context.flash().put("error", Messages.get("handler.loginfirst"));
            return unauthorized();
		}
	}

	@Override
	public Subject getSubject(final Http.Context context) {
		final AuthUserIdentity u = PlayAuthenticate.getUser(context);
		// Caching might be a good idea here
		return User.findByAuthUserIdentity(u);
	}

	@Override
	public DynamicResourceHandler getDynamicResourceHandler(
			final Http.Context context) {
		return null;
	}

	@Override
	public Result onAuthFailure(final Http.Context context,
			final String content) {
		// if the user has a cookie with a valid user and the local user has
		// been deactivated/deleted in between, it is possible that this gets
		// shown. You might want to consider to sign the user out in this case.
		return forbidden("Forbidden");
	}
}
