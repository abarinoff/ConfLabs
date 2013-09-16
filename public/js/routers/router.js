var application = application || {};

(function() {
    application.Router = Backbone.Router.extend({
        routes: {
            "events": "events",
            "events/:id": "eventDetails"
        },

        initialize: function () {
            this.$content = $("#content");
        },

        events: function () {
            console.log("events");

            var events = new application.EventCollection();
            var self = this;
            events.fetch({
                success: function (data) {
                    console.log(data);
                    var eventsView = new application.EventsView({model: data});
                    self.$content.html(eventsView.render().el);
                }
            });
            selectMenuItem("nav-conferences");
        },

        conferenceDetails: function (id) {
        }
    });

    var selectMenuItem = function(menuItem) {
        $('.navbar .nav li').removeClass('active');
        if (menuItem) {
            $('#' + menuItem).addClass('active');
        }
    }

    application.router = new application.Router();
    Backbone.history.start();
})();