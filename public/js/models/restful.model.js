define([
    "backbone"],

function(Backbone) {
    var Model = {};

    Model.Event = Backbone.Model.extend({
        urlRoot: "/events"
    });

    Model.EventCollection = Backbone.Collection.extend({
        model: Model.Event,
        url: "/events"
    });

    return Model;
});