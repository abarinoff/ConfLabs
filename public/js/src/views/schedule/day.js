define([
    'underscore',
    'backbone',
    'models/model',
    'views/schedule/slot/slot.factory',
    'views/schedule/slot/slots.row',
    'require.text!templates/day.html'
],
function(_, Backbone, Model, SlotViewFactory, SlotsRow, template) {
    var DayView = Backbone.View.extend({

        template: _.template(template),

        initialize: function(options) {
            this.eventModel = options.eventModel;
            //this.stages = options.stages;
            this.stages = this.eventModel.getStages();
            this.date = options.date;
            this.slotsByTime = this.sortByTime(options.slots);
            this.slotFactory = new SlotViewFactory();
        },

        render: function() {
            this.renderTable();
            this.renderSlotRows(this.$('tbody'));

            return this;
        },

        renderTable: function() {
            this.$el.html(this.template({
                stages  : this.stages,
                date    : this.date
            }));
        },

        renderSlotRows: function(container) {
            _.each(this.slotsByTime, function(slotsPerTimeSpan, key) {
                var slotsRow = new SlotsRow({
                    time    : key,
                    //stages  : this.stages,
                    eventModel: this.eventModel,
                    slots   : slotsPerTimeSpan
                }).render();

                container.append(slotsRow.$el);
            }, this);
        },

        sortByTime: function(slots) {
            var sortedSlots = {};
            _.each(slots, function(slot) {
                var key = slot.getTimeSpan();
                sortedSlots[key] = sortedSlots[key] || [];
                sortedSlots[key].push(slot);
            });

            return sortedSlots;
        }
    });

    return DayView;
});
