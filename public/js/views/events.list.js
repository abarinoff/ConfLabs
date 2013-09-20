define([
    "backbone",
    "views/events.list.item"],

function (Backbone, EventsListItemView) {
    var EventsListView = Backbone.View.extend({
        el: "#events-list",

        initialize: function () {
            this.model.on("add", this.renderEventListItem, this);
        },

        renderEventListItem: function (eventModel) {
            var eventListItem = new EventsListItemView({model: eventModel});
            this.$el.append(eventListItem.render().el);

            var activeEventId = this.options.activeEventId;

            if ((!activeEventId && !window.application.activeEvent) || (activeEventId == eventModel.get("id"))) {
                window.application.activeEvent = eventListItem;
                window.application.activeEvent.select();
            }
        }
    });

    return EventsListView;
});