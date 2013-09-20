define([
    "jquery",
    "backbone",
    "views/events",
    "views/events.list",
    "models/model"],

function($, Backbone, EventsView, EventsListView, Model) {

    var Router = Backbone.Router.extend({
        routes: {
            "events": "events",
            "events/:id": "eventDetails"
        },

        events: function () {
            console.log("Events");
            this.eventDetails();
        },

        eventDetails: function (id) {
            var events = new Model.EventCollection();

            var eventsView = new EventsView();
            eventsView.render();

            var eventsListView = new EventsListView({model: events, activeEventId: id});
            events.fetch();

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