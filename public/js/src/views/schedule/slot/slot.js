define([
    "underscore",
    "backbone"
],

function(_, Backbone) {

    var SlotView = Backbone.View.extend({

        initialize: function(stages, data) {
            this.stages = stages;
            this.data = data;
        },

        render: function() {
            this.$el = this.renderTemplate();
            return this;
        },

        renderTemplate: function() {
            var template = this.getTemplate();
            return $(template({stages: this.stages, data: this.data}));
        },

        getTemplate: function() {
            throw "Abstract method call";
        }
    });

    return SlotView;
});
