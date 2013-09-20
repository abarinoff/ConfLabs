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
            var replaceRoute = typeof event === "undefined";

            this.deactivate(window.application.activeEvent);
            this.activate(this);
            this.show();
            this.updateRoute(replaceRoute);

            return false;
        },

        activate: function(item) {
            window.application.activeEvent = item;
            item.$el.addClass("active");
        },

        deactivate: function(item) {
            if(item) {
                item.$el.removeClass("active");
            }
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