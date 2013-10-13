define([
    "underscore",
    "backbone",
    "views/add.event.dialog",
    "require.text!templates/events.html"],

function(_, Backbone, AddEventDialog, template) {
    var EventsView = Backbone.View.extend({
        el: "#content",
        events: {
            "click #btn-create-event" : "createEvent"
        },
        
        template: _.template(template),

        render: function () {
            this.$el.html(this.template());

            return this;
        },
        
        createEvent: function() {
            new AddEventDialog().render();
        }
    });

    return EventsView;
});