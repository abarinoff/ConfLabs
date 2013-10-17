define([
    "underscore",
    "backbone",
    "backbone.paginator",
    "backbone.validation"],

function(_, Backbone, Paginator, Validation) {
    var Model = {};

    Model.ErrorHandler = {
        401: function() {
            window.location.replace('/login');
        },

        404: function() {
            window.application.router.navigate("notFound", {trigger: true});
        },

        500: function() {
            window.application.router.navigate("serverError", {trigger: true});
        }
    };

    Model.Location = Backbone.Model.extend({
        initialize: function(attributes, options) {
            this.eventId = options.eventId;
        },

        url: function() {
            return "/events/" + this.eventId + "/location";
        },

        getId: function() {
            return this.get("id");
        },

        getTitle: function() {
            return this.get("title");
        },

        setTitle: function(title) {
            this.set("title", title);
        },

        getAddress: function() {
            return this.get("address");
        },

        setAddress: function(address) {
            this.set("address", address);
        },

        validation: {
            title: {
                required: true,
                msg: "Required"
            }
        }
    });

    Model.Stage = Backbone.Model.extend({
        initialize: function(attributes, options) {
            this.eventId = options.eventId;
        },

        url: function() {
            var url = "/events/" + this.eventId + "/stages";
            if (!this.isNew()) {
                url = url + "/" + this.id;
            }
            return url;
        },

        getId: function() {
            return this.get("id");
        },

        getTitle: function() {
            return this.get("title");
        },

        setTitle: function(title) {
            this.set("title", title);
        },

        getCapacity: function() {
            return this.get("capacity");
        },

        setCapacity: function(capacity) {
            this.set("capacity", capacity);
        },

        validation: {
            title: {
                required: true,
                msg: "Required"
            },
            capacity: {
                required: true,
                pattern: /^[1-9](\d)*$/,
                msg: "Any positive number greater than zero"
            }
        }
    });

    Model.Speaker = Backbone.Model.extend({
        initialize: function(attributes, options) {
            this.eventId = options.eventId;
        },

        url: function() {
            var url = "/events/" + this.eventId + "/speakers";
            if (!this.isNew()) {
                url += "/" + this.id;
            }
            return url;
        },

        getId: function() {
            return this.get("id");
        },

        getName: function() {
            return this.get("name");
        },

        setName: function(name) {
            this.set('name', name);
        },

        setDescription: function(description) {
            this.set('description', description);
        },

        getPosition: function() {
            return this.get("position");
        },

        setPosition: function(position) {
            this.set('position', position);
        },

        getDescription: function() {
            return this.get("description");
        },

        getSpeeches: function(eventModel) {
            //console.log("model's id we get speeches for: " + this.id);
            console.log(this.model);
        },

        validation: {
            name: {
                required: true,
                msg: "Required field"
            },
            position: {},
            description: {}
        }
    });

    Model.Speech = Backbone.Model.extend({
        initialize: function(attributes, options) {
            this.eventId = options.eventId;
            this.speakerId = options.speakerId;
        },

        url: function() {
            var url = "/events/" + this.eventId + "/speakers/" + this.speakerId + "/speeches";
            if (!this.isNew()) {
                url += "/" + this.id;
            }
            console.log("Speech URL is: " + url);
            return url;
        },

        getId: function() {
            return this.id;
        },

        getTitle: function() {
            return this.get('title');
        },

        getSpeakers: function() {
            return this.get('speakers');
        },

        /**
         * Needed for url generation
         */
        setSpeakerId: function(speakerId) {
            this.speakerId = speakerId;
        },

        /**
         * Search through the collection of the speakers of a current speech and if the speaker is found it is updated with a supplied one
         * or new speaker added if there is no speaker
         * @param speaker
         */
        setSpeaker: function(speaker) {
            var existingSpeaker = _.findWhere(this.getSpeakers(), {id: parseInt(speaker.getId())});
            if (existingSpeaker) {
                var index = _.indexOf(this.getSpeakers(), existingSpeaker);
                this.getSpeakers().splice(index, 1, JSON.stringify(speaker));
            }
            else {
                this.getSpeakers().pop(JSON.stringify(speaker));
            }
        }
    });

    Model.Event = Backbone.Model.extend({
        urlRoot: "/events",

        model: {
            location: Model.Location,
            stages: Model.Stage,
            speakers: Model.Speaker,
            speeches: Model.Speech
        },

        parseProperty: function(response, propertyName) {
            var eventId = response.id;
            var embeddedClass = this.model[propertyName];
            var embeddedData = response[propertyName];

            var parsedProperty;

            if(_.isArray(embeddedData)) {
                parsedProperty = this.parseArrayOfObjects(eventId, embeddedClass, embeddedData);
            } else {
                parsedProperty = this.parseObject(eventId, embeddedClass, embeddedData);
            }

            response[propertyName] = parsedProperty;
        },

        parseObject: function(eventId, embeddedClass, embeddedData) {
            return new embeddedClass(embeddedData, {eventId: eventId, parse: true});
        },

        parseArrayOfObjects: function(eventId, embeddedClass, embeddedData) {
            for (var i = 0; i < embeddedData.length; i++) {
                embeddedData[i] = this.parseObject(eventId, embeddedClass, embeddedData[i]);
            }

            return embeddedData;
        },

        parse: function(response) {
            for(var propertyName in this.model) {
                if(!_.isEmpty(response[propertyName])) {
                    this.parseProperty(response, propertyName);
                }
            }

            return response;
        },

        getId: function() {
            return this.get("id");
        },

        getTitle: function() {
            return this.get("title");
        },

        getDescription: function() {
            return this.get("description");
        },

        getLocation: function() {
            return this.get("location");
        },

        setLocation: function(location, options) {
            this.set("location", location, options);
        },

        removeLocation: function() {
            this.unset("location");
        },

        getStage: function(id) {
            return _.findWhere(this.getStages(), {id: parseInt(id)});
        },

        getStages: function() {
            return this.get("stages");
        },

        saveStage: function(stage) {
            var stages = this.getStages(),
                eventStage = _.findWhere(stages, {id: parseInt(stage.id)}),
                stagePos = _.indexOf(stages, eventStage);

            if (stagePos >= 0) {
                stages[stagePos] = stage;
            }
            else {
                stages.push(stage);

            }
        },

        removeStage: function(stage) {
            var stages = this.get('stages'),
                stagePos = _.indexOf(stages, stage);
            if (stagePos >= 0) {
                this.getStages().splice(stagePos, 1);
            }
        },

        getSpeaker: function(id) {
            return _.findWhere(this.getSpeakers(), {id: parseInt(id)});
        },

        getSpeakers: function() {
            return this.get("speakers");
        },

        saveSpeaker: function(speaker) {
            var speakers = this.getSpeakers(),
                updateSpeaker = _.findWhere(speakers, {id: parseInt(speaker.id)}),
                speakerPos = _.indexOf(speakers, updateSpeaker);

            if (speakerPos >= 0) {
                speakers[speakerPos] = speaker;
            }
            else {
                speakers.push(speaker);
            }
        },

        removeSpeaker: function(speaker) {
            var speakers = this.getSpeakers(),
                speaker = this.getSpeaker(speaker.id),
                speakerPos = speaker ? _.indexOf(speakers, speaker) : null;

            speakers.splice(speakerPos, 1);
        },

        getSpeech: function(id) {
            return _.findWhere(this.getSpeeches(), {id: parseInt(id)});
        },

        getSpeeches: function() {
            return this.get('speeches');
        },

        /**
         * Since the speakers do not have the references to associated speeches but speeches do keep references to
         * speakers we have to pass through all the speeches of a given event establishing speaker-to-speech relation
         * @param speakerId
         * @returns {Array}
         */
        getSpeechesForSpeaker: function(speakerId) {
            var speeches = [];
            _.each(this.getSpeeches(), function(speech){
                _.each(speech.get('speakers'), function(speaker) {
                    if (speaker.id == speakerId) {
                        speeches.push(speech);
                    }
                })
            });

            return speeches;
        },

        /**
         * Get the speeches of the current event thar are not assigned to a speaker with a given Id
         * @param speakerId
         */
        getAvailableSpeeches: function(speakerId) {
            return _.difference(this.getSpeeches(), this.getSpeechesForSpeaker(speakerId));
        },

        removeSpeechFromSpeaker: function(speakerId, speechId) {
            var removeIndexes = [];
            var speech = _.findWhere(this.getSpeeches(), {id: parseInt(speechId)});

            // @todo Careful, check for null/undefined values
            _.each(speech.getSpeakers(), function(speaker, index){
                if (speaker.id == speakerId) {
                    removeIndexes.push(index);
                }
            });

            _.each(removeIndexes, function(index) {
                speech.getSpeakers().splice(index, 1);
            });
        },

        saveSpeech: function() {

        },

        removeSpeech: function() {

        },

        validation: {
            title: {
                required: true,
                msg: "Required"
            }
        }
    });

    Model.EventCollection = Paginator.clientPager.extend({
        model: Model.Event,

        initialize: function(options) {
            Paginator.clientPager.prototype.initialize.apply(this);
            this.setActiveEventId(options.activeEventId);
            this.bind("error", this.handleError);
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

        switchToActiveEventPage: function() {
            var eventPage = this.getActiveEventPage();
            eventPage === -1 ? this.pager() : this.goTo(eventPage);
        },

        getEventIndex: function(eventId) {
            var eventIndex = _.chain(this.origModels)
                .map(function (event) {
                    return event.get("id");
                })
                .indexOf(eventId)
                .value();
            return eventIndex;
        },

        getActiveEventPage: function() {
            var eventIndex = this.getEventIndex(this.activeEventId);
            return _.isEqual(eventIndex, -1) ? -1 : Math.ceil((eventIndex + 1) / this.perPage);
        },

        updateActiveEventOnRemoval: function(removedEventIndex) {
            var collection = this.origModels;
            var collectionLength = collection.length;

            var nextActiveEventIndex = removedEventIndex < collectionLength ? removedEventIndex : collectionLength - 1;
            var activeEvent = collection[nextActiveEventIndex];

            this.setActiveEventId(_.isEmpty(activeEvent) ? 0 : activeEvent.id);
        },

        handleError: function(model, error) {
            if(error.status == 401) {
                window.location.replace('/login');
            }
        }
    });

    return Model;
});