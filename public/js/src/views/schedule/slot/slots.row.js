define([
    'underscore',
    'backbone',
    'views/schedule/slot/slot.factory',
    'require.text!templates/slot.html'
], function(_, Backbone, SlotFactory, template) {

    var SlotsRow = Backbone.View.extend({
        initialize: function(options) {
            this.stages = options.stages;
            this.slots = options.slots;
            this.time = options.time;
            this.slotFactory = new SlotFactory();
        },

        template: _.template(template),

        render: function() {
            this.$el = $(this.template({
                time: this.time
            }));
            this.$el.append(this.renderSlots());

            return this;
        },

        renderSlots: function() {
            var slotViews = this.instantiateSlotViews();

            var views = [];
            if (this.slots.length > 0 && slotViews[0].TYPE === "speech") {
                _.each(this.stages, function(stage) {
                    var empty = true;
                    _.each(this.slots, function(slot, slotIndex){
                        if (stage.getTitle() === slot.getStage().title) {
                            empty = false;
                            views.push(slotViews[slotIndex].$el);
                        }
                    }, this);
                    if (!empty) {
                        views.push($("<td></td>"));
                    }
                }, this);
            }

            return views;
        },

        instantiateSlotViews: function() {
            var slotViews = [];
            _.each(this.slots, function(slot){
                var view = this.slotFactory.build(slot.getSlotType(), this.stages, {slot: slot}).render(this.$el);
                slotViews.push(view);
            }, this);

            return slotViews;
        }
    });

    return SlotsRow;
});