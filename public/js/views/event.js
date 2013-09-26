define([
    "jquery",
    "backbone",
    "views/stages.list",
    "views/speakers.list",
    "require.text!templates/tabs.html"
],

function($, Backbone, StagesListView, SpeakersListView, tabsTemplate) {
    var EventView = Backbone.View.extend({
        template: _.template(tabsTemplate),

        render: function() {
            var $eventData = $("#event-data"),
                $tabs = this.$el.html(this.template({event: this.model}));

            $eventData.html($tabs);

            new StagesListView({model: this.model}).render();
            new SpeakersListView({model: this.model}).render();
        }
    });

    return EventView;
});