define([
    "jquery",
    "backbone",
    "views/details",
    "views/stages.list",
    "views/speakers.list",
    "views/location",
    "require.text!templates/tabs.html"
],

function($, Backbone, DetailsView, StagesListView, SpeakersListView, LocationView, tabsTemplate) {
    var EventView = Backbone.View.extend({
        template: _.template(tabsTemplate),

        render: function() {
            var $eventData = $("#event-data"),
                $tabs = this.$el.html(this.template({event: this.model}));

            $eventData.html($tabs);

            new DetailsView({model: this.model}).render();
            new StagesListView({eventModel: this.model}).render();
            new SpeakersListView({eventModel: this.model}).render();
            new LocationView({eventModel: this.model}).render();
        }
    });

    return EventView;
});