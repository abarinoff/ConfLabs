define([
    "underscore",
    "backbone",
    "views/events.list.item"],

function (_, Backbone, EventsListItemView) {
    var EventsListView = Backbone.View.extend({
        el: "#events-list",

        initialize: function () {
            this.model.on("reset", this.renderItems, this);
        },

        renderItems: function () {
            this.$el.empty();
            this.activeEventItem = undefined;

            this.model.each(_.bind(this.renderItem, this));
        },

        renderItem: function(eventModel) {
            var eventListItem = new EventsListItemView({model: eventModel});
            eventListItem.on("selected", this.itemSelected, this);
            this.$el.append(eventListItem.render().el);

            var activeEventId = this.model.activeEventId;

            if ((!activeEventId && !this.activeEventItem) || (activeEventId == eventModel.get("id"))) {
                this.activeEventItem = eventListItem;
                this.activeEventItem.select();
            }
        },

        itemSelected: function (eventItem) {
            if(!_.isUndefined(this.activeEventItem)) {
                this.activeEventItem.deactivate();
            }

            this.activeEventItem = eventItem;
            this.activeEventItem.activate();
        }
    });

    return EventsListView;
});