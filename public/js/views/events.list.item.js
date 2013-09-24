define([
    "jquery",
    "underscore",
    "backbone",
    "models/model",
    "views/event",
    "require.text!templates/events.list.item.html"],

function($, _, Backbone, Model, EventView, template) {
    var EventsListItemView = Backbone.View.extend({
        tagName: "li",
        template: _.template(template),

        events: {
            "click": "select"
        },

        render: function() {
            $(this.el).html(this.template({event: this.model}));
            return this;
        },

        select: function(event) {
            this.trigger("selected", this);
            this.show();

            var replaceRoute = _.isUndefined(event);
            this.updateRoute(replaceRoute);

            return false;
        },

        activate: function(item) {
            this.$el.addClass("active");
        },

        deactivate: function() {
            this.$el.removeClass("active");
        },

        show: function() {
            var event = new Model.Event({id: this.model.get("id")});
            var eventView = new EventView({model: event});
            event.fetch({
                success: function(model) {
                    eventView.render();
                }
            });
        },

        updateRoute: function(replaceRoute) {
            window.application.router.navigate("events/" + this.model.get("id"), {replace: replaceRoute});
        }
    });

    return EventsListItemView;
});