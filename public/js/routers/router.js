define(['jquery', 'backbone', 'views/events', 'models/in.memory.model'], function($, Backbone, EventsView, Model) {

    var Router = Backbone.Router.extend({
        routes: {
            "events": "events",
            "events/:id": "eventDetails"
        },

        initialize: function () {
            this.$content = $("#content");
        },

        events: function () {
            console.log("events");

            var events = new Model.EventCollection();
            var self = this;
            events.fetch({
                success: function (data) {
                    console.log(data);
                    var eventsView = new EventsView({model: data});
                    self.$content.html(eventsView.render().el);
                }
            });
            selectMenuItem("nav-conferences");
        },

        eventDetails: function (id) {
            console.log("Event selected: " + id);
            selectEvent(id);
        }
    });

    var selectMenuItem = function(menuItem) {
        $('.navbar .nav li').removeClass('active');
        if (menuItem) {
            $('#' + menuItem).addClass('active');
        }
    }

    var selectEvent = function(id) {
        $('.events-sidenav li').removeClass('active');
        $('#event-sidenav-' + id).addClass('active');
    }

    return Router;
});