define([
    "underscore",
    "backbone",
    "views/schedule/slot.dialog.factory",
    "require.text!templates/slot.html"
],

function(_, Backbone, SlotDialogFactory, template) {

    var SlotView = Backbone.View.extend({

        template: _.template(template),

        initialize: function(stages, data) {
            this.stages = stages;
            this.data = data;
        },

        render: function() {
            this.$el = this.renderCustomTemplate(this.stages, this.data);

            return this;
        },

        renderCustomTemplate: function(stages, data) {
            throw "Abstract method call";
        },

        getType: function() {
            throw "Abstract method call";
        },

        getTemplate: function() {
            throw "Abstract method call";
        }
    });

    return SlotView;
});
