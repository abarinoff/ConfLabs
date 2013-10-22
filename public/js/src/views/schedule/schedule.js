define([
    "underscore",
    "backbone",
    "views/schedule/draggable",
    "views/schedule/droppable",
    "views/schedule/multi.droppable",
    "require.text!templates/schedule.html"
],

function(_, Backbone, Draggable, Droppable, MultiDroppable, template) {
    var ScheduleView = Backbone.View.extend({
        el: "#schedule",
        template: _.template(template),

        render: function () {
            this.renderTemplate();
            this.initializeUnscheduledItems();
            this.initializeUnusedScheduleCells();
            this.initializeUnscheduledItemsList();

            return this;
        },

        renderTemplate: function () {
            this.$el.html(this.template())
        },

        initializeUnscheduledItems: function () {
            var unscheduledItems = this.$(".unscheduled-list-item");
            new Draggable(unscheduledItems);
        },

        initializeUnusedScheduleCells: function () {
            var unusedScheduleCells = this.$(".unused-schedule-table-cell");
            new Droppable(unusedScheduleCells);
        },

        initializeUnscheduledItemsList: function () {
            var unscheduledItemsList = this.$(".unscheduled-list");
            new MultiDroppable(unscheduledItemsList, "unscheduled-list-item");
        }
    });

    return ScheduleView;
});