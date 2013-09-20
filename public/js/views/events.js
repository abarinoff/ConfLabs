define([
    "underscore",
    "backbone",
    "require.text!templates/events.html"],

function(_, Backbone, template) {
    var EventsView = Backbone.View.extend({

        el: "#content",
        template: _.template(template),

        render: function () {
            this.$el.html(this.template());
            return this;
        }
    });

    return EventsView;
});