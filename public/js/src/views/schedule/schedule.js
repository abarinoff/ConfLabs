define([
    "underscore",
    "backbone",
    "views/schedule/draggable",
    "views/schedule/droppable",
    "views/schedule/multi.droppable",
    "views/schedule/slot.dialog",
    "require.text!templates/schedule.html"
],

function(_, Backbone, Draggable, Droppable, MultiDroppable, SlotDialog, template) {
    var ScheduleView = Backbone.View.extend({
        el: "#schedule",
        template: _.template(template),

        render: function () {
            this.renderTemplate();
            this.initializeUnscheduledItems();
            this.initializeSlotTemplateItems();
            this.initializeUnusedScheduleCells();
            this.initializeUnscheduledItemsList();

            this.$(".schedule-table").droppable({
                tolerance: "pointer",
                accept: ".slot-template",
                hoverClass: "droppable-item-hover",

                drop: function(event, ui) {
                    console.log("dropped to table");
//                    var callback = _.bind(this.removeEvent, this);
                    var dialog = new SlotDialog({callback: function() {
                        console.log("slot created");
                    }});
                    dialog.render();

                }
            });
            return this;
        },

        renderTemplate: function () {
            this.$el.html(this.template())
        },

        initializeUnscheduledItems: function () {
            var unscheduledItems = this.$(".unscheduled-list-item");
            new Draggable(unscheduledItems, true);
        },

        initializeSlotTemplateItems: function() {
            var unscheduledItems = this.$(".slot-template");
            new Draggable(unscheduledItems, false);
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