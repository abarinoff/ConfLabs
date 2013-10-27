define([
    "jquery",
    "backbone",
    "views/events",
    "views/events.list",
    "views/events.pagination",
    "models/model"],

function($, Backbone, EventsView, EventsListView, EventsPaginationView, Model) {

    var Router = Backbone.Router.extend({
        routes: {
            "events": "events",
            "events/:id": "eventDetails",
            "serverError": "serverError",
            "notFound": "notFound"
        },

        events: function () {
            this.eventDetails();
        },

        eventDetails: function (id) {
            var events = new Model.EventCollection({activeEventId: id});
            events.pager();

            var eventsView = new EventsView({eventsCollection: events});
            eventsView.render();

            var eventsListView = new EventsListView({model: events});
            var eventsPaginationView = new EventsPaginationView({model: events});

            events.fetch({
                success: function() {
                    events.switchToActiveEventPage();
                }
            });

            selectMenuItem("nav-conferences");
        },

        serverError: function() {
            $("#content").html("<b>Server Error</b>")
        },

        notFound: function() {
            $("#content").html("<b>Not Found</b>")
        }
    });

    var selectMenuItem = function(menuItem) {
        $(".navbar .nav li").removeClass("active");
        if (menuItem) {
            $("#" + menuItem).addClass("active");
        }
    }

    return Router;
});