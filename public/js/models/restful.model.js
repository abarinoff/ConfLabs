define([
    "underscore",
    "backbone",
    "paginator"],

function(_, Backbone, Paginator) {
    var Model = {};

    Model.Event = Backbone.Model.extend({
        urlRoot: "/events"
    });

    Model.EventCollection = Paginator.clientPager.extend({
        model: Model.Event,

        initialize: function(options) {
            Paginator.clientPager.prototype.initialize.apply(this);
            this.setActiveEventId(options.activeEventId);
        },

        paginator_core: {
            type: "GET",
            dataType: "json",
            url: "/events"
        },

        paginator_ui: {
            firstPage: 1,
            currentPage: 1,
            perPage: 10,
            totalPages: 10,
            pagesInRange: 2
        },

        setActiveEventId: function(eventId) {
            if(_.isString(eventId)) {
                eventId = parseInt(eventId);
            }

            this.activeEventId = eventId;
        },

        resetActiveEventId: function() {
            this.activeEventId = undefined;
        },

        getActiveEventPage: function() {
            var eventIndex = _.chain(this.origModels)
                .map(function(event) {
                    return event.get("id");
                })
                .indexOf(this.activeEventId)
                .value();

            return _.isEqual(eventIndex, -1) ? -1 : Math.ceil((eventIndex + 1) / this.perPage);
        }
    });

    return Model;
});