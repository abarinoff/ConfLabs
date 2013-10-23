define([
    "underscore",
    "backbone",
    "views/schedule/draggable",
    "views/schedule/droppable",
    "views/schedule/multi.droppable",
    "views/schedule/day",
    "views/schedule/unscheduled.items.list",
    "views/schedule/slot.dialog",
    "require.text!templates/schedule.html"
],

function(_, Backbone, Draggable, Droppable, MultiDroppable, DayView, UnscheduledItemsView, SlotDialog, template) {
    var ScheduleView = Backbone.View.extend({
        DAYS_CONTAINER              : "#days-list",
        UNSCHEDULED_ITEMS_CONTAINER : "#unscheduled-items-list",

        el: "#schedule",
        template: _.template(template),

        events: {
            "click #add-day"    : "addDay"
        },

        initialize: function(options) {
            this.eventModel = options.eventModel;
        },

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
            this.$el.html(this.template());
            this.renderUnscheduledItems();
        },

        renderUnscheduledItems: function() {
            var unscheduledItems = this.eventModel.getUnscheduledItemsWithAssignedSpeakers();
            var unscheduledItemsView = new UnscheduledItemsView({speeches: unscheduledItems}).render();

            this.$(this.UNSCHEDULED_ITEMS_CONTAINER).append(unscheduledItemsView.$el);
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
        },

        addDay: function(event) {
            $("body").datepicker("dialog",
                this.getCurrentDate(),
                this.dateSelected.bind(this),
                {},
                this.getDatepickerOffset()
            );
        },

        getCurrentDate: function() {
            var today = new Date(),
                dd = today.getDate(),
                mm = today.getMonth() + 1,
                yy = today.getYear();
            if (dd < 10) { dd = "0" + dd };
            if (mm < 10) { mm = "0" + mm };

            return mm + "/" + "dd" + "/" + yy;
        },

        getDatepickerOffset: function() {
            var buttonOffset = $("#add-day").offset();

            return [
                buttonOffset.left - 180,
                buttonOffset.top + $("#add-day").outerHeight() + 10
            ]
        },

        dateSelected: function(date) {
            console.log("dateSelected");
            var day = new DayView({
                stages: this.eventModel.getStages(),
                date: date
            }).render();

            console.log(day.$el);
            $(this.DAYS_CONTAINER).prepend(day.$el);
        }
    });

    return ScheduleView;
});