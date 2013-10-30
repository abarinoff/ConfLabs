define([
    "underscore",
    "backbone",
    "views/schedule/draggable",
    "views/schedule/droppable",
    "views/schedule/multi.droppable",
    "views/schedule/day",
    "views/schedule/unscheduled.items.list",
    "views/schedule/slot.dialog.factory",
    "views/schedule/slot/slots.row",
    "require.text!templates/schedule.html"
],

function(_, Backbone, Draggable, Droppable, MultiDroppable, DayView, UnscheduledItemsView, SlotDialogFactory, SlotsRow, template) {
    var ScheduleView = Backbone.View.extend({
        DAYS_CONTAINER              : "#days-list",
        UNSCHEDULED_ITEMS_CONTAINER : "#unscheduled-items-list",

        template: _.template(template),

        events: {
            "click #add-day"    : "onAddDayButtonClicked"
        },

        initialize: function(options) {
            this.eventModel = options.eventModel;
            this.slotBuilder = options.slotBuilder;
        },

        render: function () {
            this.renderTemplate();
            this.initializeUnscheduledItems();
            this.initializeSlotTemplateItems();

            var unusedScheduleCells = this.$(".unused-schedule-table-cell");
            this.initializeUnusedScheduleCells(unusedScheduleCells);
            this.initializeScheduledItems();

            this.initializeUnscheduledItemsList();
            this.initializeScheduleTable(this.$(".schedule-table"), this);

            this.$(".schedule-table tbody").sortable({
                helper: function(event, row) {
                    var $originals = row.children();
                    var $helper = row.clone();

                    $helper.children().each(function(index) {
                        $(this).width($originals.eq(index).width());
                    });

                    return $helper;
                }
            });

            return this;
        },

        renderTemplate: function () {
            this.$el.html(this.template());
            this.renderDays();
            this.renderUnscheduledItems();
        },

        renderUnscheduledItems: function() {
            var unscheduledItems = this.eventModel.getUnscheduledItemsWithAssignedSpeakers();
            var unscheduledItemsView = new UnscheduledItemsView({speeches: unscheduledItems}).render();

            this.$(this.UNSCHEDULED_ITEMS_CONTAINER).append(unscheduledItemsView.$el);
        },

        renderDays: function() {
            var stages = this.eventModel.getStages();

            _.each(this.eventModel.getSlotsByDay(), function(slots, date) {
                var dayView = new DayView({
                    date: date,
                    slots: slots,
                    stages: stages
                }).render();

                this.$(this.DAYS_CONTAINER).append(dayView.$el.contents());
            }, this);
        },

        initializeUnscheduledItems: function () {
            var unscheduledItems = this.$(".unscheduled-list-item");
            new Draggable(unscheduledItems, true);
        },

        initializeScheduledItems: function() {
            var $cells = this.$(".scheduled-list-item");
            new Draggable($cells, true);
        },

        initializeSlotTemplateItems: function() {
            var unscheduledItems = this.$(".slot-template");
            new Draggable(unscheduledItems, false);
        },

        initializeUnusedScheduleCells: function (cells) {
            new Droppable(cells);
        },

        initializeUnscheduledItemsList: function () {
            var unscheduledItemsList = this.$(".unscheduled-list");
            new MultiDroppable(unscheduledItemsList, "unscheduled-list-item");
        },

        initializeScheduleTable: function(element, context) {
            var self = context;
            element.droppable({
                tolerance: "pointer",
                accept: ".slot-template",
                hoverClass: "droppable-item-hover",

                drop: function(event, ui) {
                    var type = ui.helper.attr("type");
                    var callback = _.bind(self.createSlot, self, this, type);
                    var dialog = new SlotDialogFactory().build(type, {callback: callback});
                    dialog.render();
                }
            });
        },

        onAddDayButtonClicked: function() {
            $("body").datepicker("dialog",
                this.getCurrentDate(),
                this.createDay.bind(this),
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

        createDay: function(date) {
            var day = new DayView({
                stages: this.eventModel.getStages(),
                date: date,
                slots: []
            }).render();

            var unscheduledItems = day.$(".slot-template");
            new Draggable(unscheduledItems, true);

            this.initializeScheduleTable(day.$(".schedule-table"), this);

            $(this.DAYS_CONTAINER).prepend(day.$el.contents());
        },

        createSlot: function(target, type, data) {
            var stages = this.eventModel.getStages();
            var slot = this.slotBuilder.buildModel(type, stages, data);

            var slotsRow = new SlotsRow({
                stages  : stages,
                slots   : [slot],
                time    : data.start + " - " + data.end
            });

            var renderedSlotsRow = slotsRow.render();
            renderedSlotsRow.$el.appendTo($(target).find("tbody"));

            var unusedScheduleCells = $(renderedSlotsRow.$el).find(".unused-schedule-table-cell");
            console.log("unusedScheduleCells length: ", $(renderedSlotsRow.$el).find(".unused-schedule-table-cell").length);
            this.initializeUnusedScheduleCells(unusedScheduleCells);
        }
    });

    return ScheduleView;
});