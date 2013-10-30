define([
    'underscore',
    'backbone',
    'views/schedule/slot/slot.factory',
    "views/schedule/slot.dialog.factory",
    'require.text!templates/slot.html'
], function(_, Backbone, SlotFactory, SlotDialogFactory, template) {

    var SlotsRow = Backbone.View.extend({
        initialize: function(options) {
            this.stages = options.stages;
            this.slots = options.slots;
            this.time = options.time;
            this.slotFactory = new SlotFactory();
        },

        template: _.template(template),

        events: {
            "click button[name='btn-remove-slot']": "removeSlot",
            "dblclick": "edit"
        },

        render: function() {
            this.$el = $(this.template({
                time: this.time
            }));
            this.$el.append(this.renderSlots());

            this.delegateEvents();

            return this;
        },

        renderSlots: function() {
            var slotViews = this.instantiateSlotViews();
            var views = [];
            if (this.slots.length > 0 && slotViews[0].TYPE === "speech") {
                _.each(this.stages, function(stage) {
                    var empty = true;
                    _.each(this.slots, function(slot, slotIndex){
                        if (slot.getStage() !== undefined && stage.getId() === slot.getStage().id) {
                            empty = false;
                            views.push(slotViews[slotIndex].$el);
                        }
                    }, this);
                    if (empty) {
                        views.push($("<td class='unused-schedule-table-cell'></td>"));
                    }
                }, this);
            }
            else {
                _.each(slotViews, function(slotView) {
                    views.push(slotView.$el);
                });
            }

            return views;
        },

        removeSlot: function() {
            this.delegateEvents();
            this.remove();
        },

        edit: function() {
            var type = this.slots.length > 0 ? this.slots[0].getSlotType() : undefined;
            var data = {
                start   : this.slots[0].getStartTime(),
                end     : this.slots[0].getEndTime(),
                title   : this.slots[0].getTitle()
            };

            var callback = _.bind(this.update, this);

            var dialog = new SlotDialogFactory().build(type, {data: data, callback: callback});
            dialog.render();
        },

        update: function(data) {
            this.data = data;
            _.each(this.slots, function(slot) {
                slot.setStartTime(data.start);
                slot.setEndTime(data.end);
                slot.setTitle(data.title);
            });
            this.time = this.slots[0].getStartTime() + " - " + this.slots[0].getEndTime();
            this.renderOnUpdate();
        },

        renderOnUpdate: function() {
            var $old = this.$el;

            this.render();
            $old.replaceWith(this.$el);
        },

        instantiateSlotViews: function() {
            var slotViews = [];
            _.each(this.slots, function(slot){
                var view = this.slotFactory.buildView(slot.getSlotType(), this.stages, {slot: slot}).render(this.$el);
                slotViews.push(view);
            }, this);

            return slotViews;
        }
    });

    return SlotsRow;
});
