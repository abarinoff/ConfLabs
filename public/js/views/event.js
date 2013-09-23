define([
    "jquery",
    "backbone",
    "require.text!templates/tabs.html"
],

function($, Backbone, tabsTemplate) {
    var EventView = Backbone.View.extend({
        template: _.template(tabsTemplate),

        /*events: {
            "click button[id^='add-new-']": 'addNew'
        },*/

        render: function() {
            /*this.setElement($('#event-tabs-content'));
            this.$el.html((this.template({event: this.model})));*/

            this.$el.html(this.template({event: this.model}));

            this.el = $("#event-data").html(this.$el.html());
            $("button[id^='add-new-']").click(this.addNewToList);
        },

        addNewToList: function() {
            console.log("Add new item to list");
        }
    });

    return EventView;
});