define([
    "underscore",
    "backbone"
],

function(_, Backbone) {

    var SlotView = Backbone.View.extend({

        initialize: function(data) {
            console.log("initialize");
            this.data = data;
        },

        render: function() {
            this.$el = this.renderTemplate();
            return this;
        },

        renderTemplate: function() {
            var template = this.getTemplate();
            return $(template({data: this.data}));
        },

        getTemplate: function() {
            throw "Abstract method call";
        }
    });

    return SlotView;
});
