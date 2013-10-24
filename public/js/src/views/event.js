define([
    "jquery",
    "backbone",
    "views/details",
    "views/stages.list",
    "views/speakers.list",
    "views/schedule/schedule",
    "views/schedule/slot/slot.builder",
    "views/location",
    "require.text!templates/tabs.html"
],

function($, Backbone, DetailsView, StagesListView, SpeakersListView, ScheduleView, SlotBuilder, LocationView, tabsTemplate) {
    var EventView = Backbone.View.extend({
        template: _.template(tabsTemplate),

        render: function() {
            var $eventData = $("#event-data"),
                $tabs = this.$el.html(this.template({event: this.model}));

            $eventData.html($tabs);

            new DetailsView({model: this.model}).render();
            new StagesListView({eventModel: this.model}).render();
            new SpeakersListView({eventModel: this.model}).render();
            new ScheduleView({eventModel: this.model, slotBuilder: new SlotBuilder()}).render();
            new LocationView({eventModel: this.model}).render();
        }
    });

    return EventView;
});