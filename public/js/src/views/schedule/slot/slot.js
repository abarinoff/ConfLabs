define([
    "underscore",
    "backbone",
    "views/schedule/slot.dialog.factory",
    "require.text!templates/slot.html"
],

function(_, Backbone, SlotDialogFactory, template) {

    var SlotView = Backbone.View.extend({

        template: _.template(template),

        events: {
            "dblclick": "edit",
            "click button[name='btn-remove-slot']": "removeSlot"
        },

        initialize: function(stages, data) {
            this.stages = stages;
            this.data = data;
        },

        render: function($el) {
            this.$el = $el;

            var customEl = this.renderCustomTemplate(this.stages, this.data);
            this.$el.append(customEl);

            this.delegateEvents();

            return this;
        },

        renderOnUpdate: function() {
            var $old = this.$el;

            this.render();
            $old.replaceWith(this.$el);
        },

        renderTemplate: function() {
            return $(this.template({stages: this.stages, data: this.data}));
        },

        renderCustomTemplate: function(stages, data) {
            throw "Abstract method call";
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

        removeSlot: function() {
            this.delegateEvents();
            this.remove();
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
