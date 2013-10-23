define([
    "underscore",
    "backbone",
    "views/add.event.dialog",
    "require.text!templates/events.html"],

function(_, Backbone, AddEventDialog, template) {
    var EventsView = Backbone.View.extend({

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
            console.log("create event button clicked");

            new AddEventDialog().render();
        }
    });

    return EventsView;
});