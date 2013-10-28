define([
    "underscore",
    "backbone",
    "views/schedule/slot.dialog.factory"
],

function(_, Backbone, SlotDialogFactory) {

    var SlotView = Backbone.View.extend({

        initialize: function(stages, data) {
            this.stages = stages;
            this.data = data;
        },

        render: function() {
            this.$el = this.renderTemplate();
            this.$el.on("dblclick", _.bind(this.edit, this));
            return this;
        },

        renderOnUpdate: function() {
            var $old = this.$el;

            this.render();
            $old.replaceWith(this.$el);
        },

        renderTemplate: function() {
            var template = this.getTemplate();
            return $(template({stages: this.stages, data: this.data}));
        },

        edit: function() {
            var type = this.getType();
            var callback = _.bind(this.update, this);

            var dialog = new SlotDialogFactory().build(type, {data: this.data, callback: callback});
            dialog.render();
        },

        update: function(data) {
            this.data = data;
            this.renderOnUpdate();
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
