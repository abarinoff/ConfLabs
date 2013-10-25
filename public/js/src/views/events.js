define([
    "underscore",
    "backbone",
    "views/add.event.dialog",
    "require.text!templates/events.html"],

function(_, Backbone, AddEventDialog, template) {
    var EventsView = Backbone.View.extend({

        initialize: function(options) {
            this.eventsCollection = options.eventsCollection;
        },

        el: "#content",
        template: _.template(template),
        events: {
            "click #btn-create-event":  "showCreateEventDialog"
        },

        createEventDialog: undefined,

        render: function () {
            this.$el.html(this.template());

            return this;
        },

        showCreateEventDialog: function(event) {
            console.log("showCreateEventDialog");
            var view = this;
            view.createEventDialog = this.createEventDialog || new AddEventDialog({parent: this});
            view.createEventDialog.render();
        },

        eventCreated: function(eventModel) {
            console.log("event created, add it to collection");
            this.eventsCollection.add(eventModel);
            this.eventsCollection.trigger("event:created");
        }
    });

    return EventsView;
});