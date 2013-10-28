define([
    'underscore',
    'backbone',
    'models/model',
    'views/schedule/slot/slot.factory',
    'require.text!templates/day.html'
],
function(_, Backbone, Model, SlotViewFactory, dayTemplate) {
    var DayView = Backbone.View.extend({

        template: _.template(dayTemplate),

        initialize: function(options) {
            this.stages = options.stages;
            this.date = options.date;
            this.slots = options.slots;
            this.slotFactory = new SlotViewFactory();
        },

        render: function() {
            this.$el.html(this.template({
                stages  : this.stages,
                date    : this.date
            }));
            this.renderSlots(this.$('tbody'));

            return this;
        },

        renderSlots: function(container) {
            // @todo Discover the way to append the whole array of jQuery's <tr>s to a DOM (i.e. $.add() function)
            // instead of passing an array to append to or returning and array of jquery objects (<tr>s)

            _.each(this.slots, function(slot) {
                var slotView = this.slotFactory.build(slot.type, this.stages, slot);
                slotView.render();
                container.append(slotView.$el);
            }, this);
        }
    });

    return DayView;
});
