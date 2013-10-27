define([
    "underscore",
    "backbone",
    "views/add.event.dialog",
    "require.text!templates/events.html"],

function(_, Backbone, AddEventDialog, template) {
    var EventsView = Backbone.View.extend({

        initialize: function(options) {
            this.eventsCollection = options.eventsCollection;
            this.createEventDialog = undefined;
        },

        el: "#content",
        template: _.template(template),
        events: {
            "click #btn-create-event":  "showCreateEventDialog"
        },

        render: function () {
            this.$el.html(this.template());

            return this;
        },

        showCreateEventDialog: function() {
            var view = this;
            view.createEventDialog = this.createEventDialog || new AddEventDialog({parent: this});
            view.createEventDialog.render();
        },

        eventCreated: function(eventModel) {
            this.eventsCollection.trigger("event:created", eventModel);
        }
    });

    return EventsView;
});