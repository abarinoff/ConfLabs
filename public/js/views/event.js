define([
    "jquery",
    "backbone"],

function($, Backbone) {
    var EventView = Backbone.View.extend({
        render: function() {
            $("#description").html("<h1>" + this.model.get("description") + "</h1>");
        }
    });

    return EventView;
});