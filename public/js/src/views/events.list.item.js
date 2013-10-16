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
            "click": "select",
            "click button[id^='btn-remove-event-']"   : "removeEvent"
        },

        initialize: function(options) {
            console.log("test1");
            console.log(options);
            this.eventsCollection = options.eventsCollection;
        },

        render: function() {
            this.renderFromModel(this.model);
            return this;
        },

        renderFromModel: function(model) {
            $(this.el).html(this.template({event: model}));
        },

        select: function(event) {
            console.log("selected " + this.model.id);
            this.trigger("selected", this);
            this.show();

            var replaceRoute = _.isUndefined(event);
            this.updateRoute(replaceRoute);

            return false;
        },

        removeEvent: function(clickEvent) {
            var buttonId = $(clickEvent.target).attr('id'),
                eventId = buttonId.replace("btn-remove-event-", '');

            console.log("remove event with id: " + eventId);
            clickEvent.stopPropagation();

            this.model.destroy({
                success: _.bind(this.onDestroySuccess, this),
                error: _.bind(this.onError, this)
            });
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
            var changeHandler = _.bind(this.renderFromModel, this, event);

            event.fetch({
                success: function(model) {
                    event.on("change:title", changeHandler);
                    eventView.render();
                }
            });
        },

        updateRoute: function(replaceRoute) {
            window.application.router.navigate("events/" + this.model.get("id"), {replace: replaceRoute});
        },

        onDestroySuccess: function() {
            this.eventsCollection.remove(this.model);
            this.eventsCollection.pager();
        },

        onError: function(model, response) {
            Model.ErrorHandler[response.status]();
        }
    });

    return EventsListItemView;
});