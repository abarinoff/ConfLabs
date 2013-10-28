define([
    'underscore',
    'backbone',
    'models/model',
    'views/schedule/slot/slot.factory',
    'require.text!templates/day.html'
],
function(_, Backbone, Model, SlotFactory, dayTemplate) {
    var DayView = Backbone.View.extend({

        template: _.template(dayTemplate),

        initialize: function(options) {
            this.stages = options.stages;
            this.date = options.date;
            this.slots = options.slots;
            this.slotFactory = new SlotFactory();
        },

        render: function() {
            /*this.$el.html(this.template({
                stages  : this.stages,
                slots   : this.slots,
                date    : this.date
            }));*/

            this.renderSlots();

            return this;
        },

        renderSlots: function() {
            var slotsHtml = '';
            _.each(this.slots, function(slot) {
                slotView = this.slotFactory.build(slot.type, this.stages.length);
            });
        }
    });

    return DayView;
});
