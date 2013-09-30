define([
    "underscore",
    "backbone",
    "views/add.event.dialog",
    "require.text!templates/events.html"],

function(_, Backbone, AddEventDialog, template) {
    var EventsView = Backbone.View.extend({

        el: "#content",
        template: _.template(template),

        render: function () {
            this.$el.html(this.template());
            this.$("#btn-create-event").click(function() {
                new AddEventDialog().render();
            });

            return this;
        }
    });

    return EventsView;
});