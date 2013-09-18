define(['backbone'], function(Backbone) {

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
            for (var i = 0; i < this.events.length; i++) {
                if (this.events[i].id === id) {
                    event = this.events[i];
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
    }

    Model.store = new MemoryStore();

    return Model;
});