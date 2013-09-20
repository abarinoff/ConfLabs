define([
    "backbone"],

function(Backbone) {
    var Model = {};

    Model.Event = Backbone.Model.extend({

        sync: function(method, model, options) {
            if (method === "read") {
                Model.store.findById(parseInt(this.id), function (data) {
                    options.success(data);
                });
            }
        }

    });

    Model.EventCollection = Backbone.Collection.extend({
        model: Model.Event,

        sync: function(method, model, options) {
            if (method === "read") {
                options.success(Model.store.events);
            }
        }
    });

    var MemoryStore = function () {
        this.findById = function (id, callback) {
            var event = null;
            for (var i = 0; i < this.eventsDetails.length; i++) {
                if (this.eventsDetails[i].id === id) {
                    event = this.eventsDetails[i];
                    break;
                }
            }
            callback(event);
        }

        this.events = [
            {"id": 1, "title": "IT Weekend 2013"},
            {"id": 2, "title": "XP Days 2013"},
            {"id": 3, "title": "JDay Lviv 2013"},
            {"id": 4, "title": "IT Weekend 2012"},
            {"id": 5, "title": "XP Days 2012"},
            {"id": 6, "title": "JDay Lviv 2012"},
            {"id": 7, "title": "IT Weekend 2011"},
            {"id": 8, "title": "XP Days 2011"},
            {"id": 9, "title": "JDay Lviv 2011"},
            {"id": 10, "title": "IT Weekend 2010"},
            {"id": 11, "title": "XP Days 2010"},
            {"id": 12, "title": "JDay Lviv 2010"}
        ];

        this.eventsDetails = [
            {"id": 1, "title": "IT Weekend 2013", "description": "Cool event 1"},
            {"id": 2, "title": "XP Days 2013", "description": "Cool event 2"},
            {"id": 3, "title": "JDay Lviv 2013", "description": "Cool event 3"},
            {"id": 4, "title": "IT Weekend 2012", "description": "Cool event 4"},
            {"id": 5, "title": "XP Days 2012", "description": "Cool event 5"},
            {"id": 6, "title": "JDay Lviv 2012", "description": "Cool event 6"},
            {"id": 7, "title": "IT Weekend 2011", "description": "Cool event 7"},
            {"id": 8, "title": "XP Days 2011", "description": "Cool event 8"},
            {"id": 9, "title": "JDay Lviv 2011", "description": "Cool event 9"},
            {"id": 10, "title": "IT Weekend 2010", "description": "Cool event 10"},
            {"id": 11, "title": "XP Days 2010", "description": "Cool event 11"},
            {"id": 12, "title": "JDay Lviv 2010", "description": "Cool event 12"}
        ];
    }

    Model.store = new MemoryStore();

    return Model;
});