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
            "events/:id": "eventDetails"
        },

        events: function () {
            this.eventDetails();
        },

        eventDetails: function (id) {
            var events = new Model.EventCollection({activeEventId: id});
            events.pager();

            var eventsView = new EventsView();
            eventsView.render();

            var eventsListView = new EventsListView({model: events});
            var eventsPaginationView = new EventsPaginationView({model: events});

            events.fetch({
                success: function() {
                    var eventPage = events.getActiveEventPage(id);
                    eventPage === -1 ? events.pager() : events.goTo(eventPage);
                }
            });

            selectMenuItem("nav-conferences");
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