define([
    "jquery",
    "underscore",
    "backbone",
    "models/model",
    "views/event",
    "views/remove.event.dialog",
    "require.text!templates/events.list.item.html"],

function($, _, Backbone, Model, EventView, RemoveEventDialog, template) {
    var EventsListItemView = Backbone.View.extend({
        tagName: "li",
        template: _.template(template),

        events: {
            "click": "select",
            "click button#btn-remove-event": "confirmRemoval"
        },

        initialize: function(options) {
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
            this.eventView = new EventView({model: event});
            var changeHandler = _.bind(this.renderFromModel, this, event);

            event.fetch({
                success: _.bind(function(model) {
                    event.on("change:title", changeHandler);
                    this.eventView.render();
                    _.each(event.getSpeeches(), function(speech){
                        speech.on("speech:destroy", function() {
                            // We've already detached the speech from a speaker but we have to remove the speech from the
                            // event's speeches list if it is not referenced by any of the speakers
                            this.onSpeechUnset(speech);
                        }, this);
                    }, event);
                }, this)
            });
        },

        updateRoute: function(replaceRoute) {
            window.application.router.navigate("events/" + this.model.get("id"), {replace: replaceRoute});
        },

        confirmRemoval: function(clickEvent) {
            clickEvent.stopPropagation();

            var callback = _.bind(this.removeEvent, this);
            var dialog = new RemoveEventDialog({model: this.model, callback: callback});
            dialog.render();
        },

        removeEvent: function(clickEvent) {
            var eventIndex = this.model.collection.getEventIndex(this.model.id)

            this.model.destroy({
                success: _.bind(this.onDestroySuccess, this, eventIndex),
                error: _.bind(this.onError, this)
            });
        },

        onDestroySuccess: function(eventIndex) {
            this.trigger("deleted", this, eventIndex);
        },

        onError: function(model, response) {
            Model.ErrorHandler[response.status]();
        }
    });

    return EventsListItemView;
});