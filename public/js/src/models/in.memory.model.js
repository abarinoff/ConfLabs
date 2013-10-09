define([
    "backbone",
    "backbone.paginator",
    "models/restful.model"],

function(Backbone, Paginator, RestfulModel) {
    var Model = {};

    Model.Event = RestfulModel.Event.extend({
        initialize : function() {
            this.on('change:stages', function() {
                console.log("Model changed");
            });
        },

        sync: function(method, model, options) {
            if (method === "read") {
                Model.store.findById(parseInt(this.id), function (data) {
                    options.success(data);
                });
            }
        }

    });

    Model.EventCollection = RestfulModel.EventCollection.extend({
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
            {"id": 1, "title": "IT Weekend 2013", "description": "Cool event 1",
                "stages":[
                    {"id": 1, "title": "stage 1.1", "capacity": "101"},
                    {"id": 2, "title": "stage 1.2", "capacity": "102"},
                    {"id": 3, "title": "stage 1.3", "capacity": "103"}
                ],
                "speakers" : [
                    {"id" : 1, "name" : "John Doe", "position": "Engineer", "description": "Proud to have the courage to give a speech"},
                    {"id" : 5, "name" : "Andy Dufrehne", "position": "Lawyer", "description": "Proud of his persistence"},
                    {"id" : 6, "name" : "Roy Colman", "position": "Vice President", "description": "Experience in running companies of a medium and large scale"},
                    {"id" : 7, "name" : "Kent Back", "position": "IT Guru", "description": "Some famous guy in IT"},
                    {"id" : 8, "name" : "Kavin Mitnik", "position": "Hacker", "description": "The name is self-explanatory"}
                ],
                "speeches" : [
                    {"id" : 1, "title" : "Java for the Win"},
                    {"id" : 2, "title" : "JavaScript is not Java"},
                    {"id" : 3, "title" : "Mastering closures"},
                    {"id" : 4, "title" : "Demystifying Prototypes"}
                ],
                "location" : {
                    "title": "President hotel",
                    "address": "вул. Госпітальна, 12, Київ, 01023, Україна"
                }
            },
            {"id": 2, "title": "XP Days 2013", "description": "Cool event 2",
                "stages":[
                    {"id": 4, "title": "stage 2.1", "capacity": "201"},
                    {"id": 5, "title": "stage 2.2", "capacity": "202"}
                ],
                "speakers" : [
                    {"id" : 2, "name" : "John Smith", "position": "Senior Engineer", "description": "Proud to be able to manage people"},
                    {"id" : 9, "name" : "Ken Arnold", "position": "Technical Lead", "description": "One of the creators of Java Programming language"},
                    {"id" : 10, "name" : "James Gosling", "position": "Guru", "description": "The father of the Java programming language"}
                ],
                "speeches" : [
                    {"id" : 5, "title" : "Java Generics"},
                    {"id" : 6, "title" : "Multithreading"},
                    {"id" : 7, "title" : "Java IO"},
                    {"id" : 8, "title" : "All you want to now but afraid to ask"}
                ]
            },
            {"id": 3, "title": "JDay Lviv 2013", "description": "Cool event 3",
                "stages":[
                    {"id": 6, "title": "stage 3.1", "capacity": "301"},
                    {"id": 7, "title": "stage 3.2", "capacity": "302"},
                    {"id": 8, "title": "stage 3.3", "capacity": "303"},
                    {"id": 9, "title": "stage 3.4", "capacity": "304"}
                ],
                "speakers" : [
                    {"id" : 3, "name" : "Ben Richards", "position": "Story Hero", "description": "Just the Running Man"}
                ]},
            {"id": 4, "title": "IT Weekend 2012", "description": "Cool event 4",
                "stages":[
                    {"id": 10, "title": "stage 4.1", "capacity": "401"}
                ],
                "speakers" : [
                    {"id" : 4, "name" : "Dan Killian", "position": "Chief of Executive", "description": "Proud of being athletic"}
                ]},
            {"id": 5, "title": "XP Days 2012", "description": "Cool event 5",
                "stages":[
                    {"id": 11, "title": "stage 5.1", "capacity": "501"}
                ]},
            {"id": 6, "title": "JDay Lviv 2012", "description": "Cool event 6",
                "stages":[{"id": 12, "title": "stage 6.1", "capacity": "601"}
            ]},
            {"id": 7, "title": "IT Weekend 2011", "description": "Cool event 7",
                "stages":[{"id": 13, "title": "stage 7.1", "capacity": "701"}
            ]},
            {"id": 8, "title": "XP Days 2011", "description": "Cool event 8",
                "stages":[{"id": 14, "title": "stage 8.1", "capacity": "801"}
            ]},
            {"id": 9, "title": "JDay Lviv 2011", "description": "Cool event 9",
                "stages":[{"id": 15, "title": "stage 9.1", "capacity": "901"}
            ]},
            {"id": 10, "title": "IT Weekend 2010", "description": "Cool event 10",
                "stages":[{"id": 16, "title": "stage 10.1", "capacity": "1001"}
            ]},
            {"id": 11, "title": "XP Days 2010", "description": "Cool event 11",
                "stages":[{"id": 17, "title": "stage 11.1", "capacity": "1101"}
            ]},
            {"id": 12, "title": "JDay Lviv 2010", "description": "Cool event 12",
                "stages":[{"id": 18, "title": "stage 12.1", "capacity": "1201"}
            ]}
        ];
    }

    Model.store = new MemoryStore();

    return Model;
});