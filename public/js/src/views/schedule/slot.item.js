define([
    'underscore',
    'backbone',
    'require.text!templates/slot.item'
],
function(_, Backbone, template) {

    var SlotItem = Backbone.View.extend({
        template: _.template(template),

        initialize: function(options) {
            this.speech = options.speech;
            //this.stages = options.stages;
        },

        render: function() {
            this.$el = $(this.template({speech: this.speech}));

            return this;
        }
    });

    return SlotItem;
});
