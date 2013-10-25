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

        events: {
            "click a#details-tab" : "renderDetails",
            "click a#stages-tab" : "renderStages",
            "click a#speakers-tab" : "renderSpeakers",
            "click a#schedule-tab" : "renderSchedule",
            "click a#location-tab" : "renderLocation"
        },

        initialize: function() {
            this.detailsView = new DetailsView({model: this.model});
            this.stagesListView = new StagesListView({eventModel: this.model});
            this.speakersListView = new SpeakersListView({eventModel: this.model});
            this.scheduleView = new ScheduleView({eventModel: this.model, slotBuilder: new SlotBuilder()});
            this.locationView = new LocationView({eventModel: this.model});
        },

        render: function() {
            var $eventData = $("#event-data"),
                $tabs = this.$el.html(this.template({event: this.model}));

            $eventData.html($tabs);
            this.renderSchedule();
        },

        renderDetails: function() {
            this.renderSubView(this.detailsView, "#details");
        },

        renderStages: function() {
            this.renderSubView(this.stagesListView, "#stages");
        },

        renderSpeakers: function() {
            this.renderSubView(this.speakersListView, "#speakers");
        },

        renderSchedule: function() {
            this.renderSubView(this.scheduleView, "#schedule");
        },

        renderLocation: function() {
            this.renderSubView(this.locationView, "#location");
        },

        renderSubView: function(view, containerSelector) {
            view.render();
            $(containerSelector).append(view.$el);
        }
    });

    return EventView;
});