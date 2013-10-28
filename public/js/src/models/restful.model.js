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

        getSpeeches: function() {
            return this.get('speeches');
        },

        setSpeeches: function(speeches) {
            this.set('speeches', speeches);
        },

        saveSpeech: function(speech) {
            var item = _.findWhere(this.getSpeeches(), {id: parseInt(speech.id)});
            if (item !== undefined) {
                var position = _.indexOf(this.getSpeeches(), item);
                this.getSpeeches()[position] = speech.toJSON();
            }
            else {
                this.getSpeeches().push(speech.toJSON());
            }
        },

        unsetSpeech: function(speechModel) {
            var speeches = this.getSpeeches(),
                item = _.findWhere(this.getSpeeches(), {id: parseInt(speechModel.id)});
            if (item !== undefined) {
                var index = _.indexOf(speeches, item);
                speeches.splice(index, 1);
            }
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

            return url;
        },

        getId: function() {
            return this.id;
        },

        getTitle: function() {
            return this.get('title');
        },

        setTitle: function(title) {
            this.set('title', title);
        },

        getSpeakers: function() {
            return this.get('speakers');
        },

        setSpeakerId: function(speakerId) {
            this.speakerId = speakerId;
        }
    });

    Model.Slot = Backbone.Model.extend({
        getSpeech: function() {
            return this.get('speech');
        },

        extractDate: function(datetime) {
            return datetime.split(" ")[0];
        },

        extractTime: function(datetime) {
            return datetime.split(" ")[1];
        },

        getStartDate: function() {
            var startDate = this.get('start');
            return this.extractDate(startDate);
        },

        getEndDate: function() {
            var endDate = this.get('end');
            return this.extractDate(endDate);
        },

        getStartTime: function() {
            var startTime = this.get('start');
            return this.extractTime(startTime);
        },

        getEndTime: function() {
            var endTime = this.get('end');
            return this.extractTime(endTime);
        },

        prepareForScheduleTable: function() {
            var slot = this.toJSON();
            slot.timeSpan = this.getStartTime() + " - " + this.getEndTime();

            return slot;
        },

        hasSpeech: function(speechModel) {
            return this.getSpeech() && this.getSpeech().id === speechModel.id;
        }
    });

    Model.Event = Backbone.Model.extend({
        urlRoot: "/events",

        model: {
            location: Model.Location,
            stages: Model.Stage,
            speakers: Model.Speaker,
            speeches: Model.Speech,
            slots: Model.Slot
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
                speaker.set('speeches', []);
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

        getSpeechesForSpeaker: function(speakerId) {
            var speeches = [];
            var speaker = this.getSpeaker(speakerId);
            _.each(speaker.getSpeeches(), function(speakersSpeech) {
                _.each(this.getSpeeches(), function(speech) {
                    if (speech.id == speakersSpeech.id) {
                        speeches.push(speech);
                    }
                });
            }, this);

            return speeches;
        },

        getAvailableSpeeches: function(speakerId) {
            return _.difference(this.getSpeeches(), this.getSpeechesForSpeaker(speakerId));
        },

        getSlots: function() {
            return this.get('slots');
        },

        /**
         * Get the speeches that are not referenced by any of the slots
         */
        getUnscheduledItems: function() {
            var speeches = [];
            _.each(this.getSpeeches(), function(speech) {
                var unscheduled = true;
                _.each(this.getSlots(), function(slot) {
                    if (slot.hasSpeech(speech)) {
                        unscheduled = false;
                    }
                }, this);
                if (unscheduled) {
                    speeches.push(speech);
                }
            }, this);

            return speeches;
        },

        getUnscheduledItemsWithAssignedSpeakers: function() {
            var speechModels = this.getUnscheduledItems(),
                speeches = [];
            _.each(speechModels, function(speechModel) {
                var speechObj = speechModel.toJSON();
                speechObj.speakers = [];
                speeches.push(speechObj);
                _.each(this.getSpeakers(), function(speaker) {
                    _.each(speaker.getSpeeches(), function(speech) {
                        if (speech.id === speechModel.id) {
                            speechObj.speakers.push({
                                name        : speaker.getName()
                            });
                        }
                    });
                });
            }, this);

            return speeches;
        },

        saveSpeech: function(speechModel) {
            var speeches = this.getSpeeches(),
                item = _.findWhere(speeches, {id: parseInt(speechModel.id)});
            if (item !== undefined) {
                var index = _.indexOf(item);
                speeches[index] = speechModel;
            }
            else {
                speeches.push(speechModel);
            }
        },

        removeSpeech: function(speechModel) {
            var speeches = this.getSpeeches(),
                item = _.findWhere(speeches, {id: parseInt(speechModel.id)});
            if (item !== undefined) {
                var index = _.indexOf(speeches, item);
                speeches.splice(index, 1);
            }
        },

        // Remove the given speech from Event speeches array in case there are no speakers referencing it anymore
        onSpeechUnset: function(speechModel) {
            var occurrences = 0;
            _.each(this.getSpeakers(), function(speaker) {
                _.each(speaker.getSpeeches(), function(speechObj) {
                    if (speechObj.id == speechModel.getId()) {
                        occurrences++;
                    }
                })
            });
            if (occurrences == 0) {
                this.removeSpeech(speechModel);
            }
        },

        getSlotsByDay : function() {
            var slotsPerDays = {}, slots = this.getSlots();

            _.invoke(slots, function() {
                var date = this.getStartDate();
                slotsPerDays[date] = slotsPerDays[date] || [];
                slotsPerDays[date].push(this.prepareForScheduleTable());
            }, slotsPerDays);

            // Sort by days and by time inside the days
            var sortedKeys = _.sortBy(_.keys(slotsPerDays), function(key) {
                return key;
            });

            var sortedSlotsPerDays = {};
            _.each(sortedKeys, function(key) {
                sortedSlotsPerDays[key] = _.sortBy(slotsPerDays[key], function(slot){
                    return slot.timeSpan;
                });
            });

            return sortedSlotsPerDays;
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