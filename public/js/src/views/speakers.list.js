define([
    'underscore',
    'jquery',
    'backbone',
    'models/model',
    'views/speaker',
    'backbone.validation',
    'validation/validation.handler',
    'require.text!templates/speakers.list.html'
],
function(_, $, Backbone, Model, SpeakerView, Validation, ValidationHandler, speakersListTemplate) {
    var SpeakersListView = Backbone.View.extend({
        ID_SELECTOR         : "#hdn-speaker-id",
        NAME_SELECTOR       : "#speaker-name",
        POSITION_SELECTOR   : "#speaker-position",
        DESCRIPTION_SELECTOR: "#speaker-description",

        DIALOG_SELECTOR     : "#dlg-speaker",
        ADD_SPEECH_DIALOG   : "#dlg-speech",

        SELECT_SPEECH_BLOCK : "#select-speech-block",
        NEW_SPEECH_BLOCK    : "#new-speech-block",

        SPEECH_SPEAKER_ID   : "#hdn-speech-speaker-id",
        SPEECHES_SELECT     : "#existing-speeches",
        SPEECH_TITLE        : "#new-speech-title",

        template: _.template(speakersListTemplate),

        el: "#speakers",

        events: {
            "click button[id^='btn-edit-speaker-']"     : "showModal",
            "click button[id^='btn-remove-speaker-']"   : "removeSpeaker",
            "click button#save-speaker"                 : "saveSpeaker"
            //"change #existing-speeches"                 : "speechSelected",
            //"click button#save-speech"                  : "addSpeech"
        },

        initialize: function(options) {
            _.bindAll(this,
                "speakerSaved",
                "speakerRemoved",
                "errorSaveSpeaker",
                "errorRemoveSpeaker",
                "dialogHidden"
            );
            this.eventModel = options.eventModel;

            // Event: Speech has been detached from the current speaker
            _.each(this.eventModel.getSpeakers(), function(speaker) {
                speaker.on("speech:detach", function(){
                    this.render();
                }, this);
            }, this);
        },

        render: function() {
            this.$el.html(this.template());
            this.$("#speakers-list").empty();

            _.each(this.eventModel.getSpeakers(), function(speaker, index, list) {
                var speakerView = new SpeakerView({
                    model: speaker,
                    eventModel: this.eventModel
                }).render();
                this.$("#speakers-list").append(speakerView.$el);
            }, this);

            $(this.DIALOG_SELECTOR).on('hidden.bs.modal', this.dialogHidden);

            return this;
        },

        showModal : function(event) {
            var $target = $(event.target),
                view = this,
                buttonId = $target.attr('id'),
                id = buttonId.replace("btn-edit-speaker-", '');

            if (id == "new") {
                $(view.ID_SELECTOR).val('');
                $(view.NAME_SELECTOR).val('');
                $(view.POSITION_SELECTOR).val('');
                $(view.DESCRIPTION_SELECTOR).val('');
            }
            else {
                var speaker = view.eventModel.getSpeaker(id);

                $(view.ID_SELECTOR).val(speaker.getId());
                $(view.NAME_SELECTOR).val(speaker.getName());
                $(view.POSITION_SELECTOR).val(speaker.getPosition());
                $(view.DESCRIPTION_SELECTOR).val(speaker.getDescription());
            }
            $(view.DIALOG_SELECTOR).modal();
        },

        saveSpeaker: function() {
            var view = this,
                id = $(this.ID_SELECTOR).val(),
                attributes = view.collectFields(),
                speaker = isNaN(parseInt(id)) ? null : view.eventModel.getSpeaker(id);

            view.model = speaker
                ? new Model.Speaker(speaker.toJSON(), {eventId: view.eventModel.id})
                : new Model.Speaker({}, {eventId: view.eventModel.id});

            Validation.bind(view, new ValidationHandler("speaker."));

            view.model.set(attributes);

            view.model.save({}, {
                success : view.speakerSaved,
                error   : view.errorSaveSpeaker
            });
        },

        speakerSaved: function(model, respose, options) {
            var view = this;
            view.eventModel.saveSpeaker(model);
            $(view.DIALOG_SELECTOR).modal('hide');
        },

        removeSpeaker: function(event) {
            var $target = $(event.target),
                view = this,
                buttonId = $target.attr('id'),
                id = buttonId.replace("btn-remove-speaker-", '');

            var speaker = view.eventModel.getSpeaker(id);
            speaker.destroy({
                success: view.speakerRemoved,
                error: view.errorRemoveSpeaker
            });
        },

        speakerRemoved: function(model, response, options) {
            var view = this;
            view.eventModel.removeSpeaker(model);
            view.render();
        },

/*        speechSelected: function(event) {
            var view = this,
                $select = $(event.target),
                value = $select.val();
            if (parseInt(value)) {
                $(view.NEW_SPEECH_BLOCK).addClass("hidden");
            }
            else {
                $(view.NEW_SPEECH_BLOCK).removeClass("hidden");
            }

            console.log("selected speech: " + value);
        },*/

/*        addSpeech: function() {
            var view = this,
                speakerId = $(view.SPEECH_SPEAKER_ID).val();

            var speechId = $(this.SPEECHES_SELECT).val();
            var speech;
            if (parseInt(speechId)) {
                console.log("update existing");
                speech = view.eventModel.getSpeech(speechId);
            }
            else {
                speech = new Model.Speech({eventId: view.eventModel.id, speakerId: speakerId});
            }
            speech.setSpeaker(this.eventModel.getSpeaker(speakerId));
            speech.setSpeakerId(speakerId);
            *//*speech.save({}, {
                success: function() {
                    console.log("speech save succeeded");
                },
                error: function() {
                    console.log("Speech save error");
                }
            });*//*
        },*/

        errorSaveSpeaker: function() {
            console.log("Error saving Speaker on the remote server");
        },

        errorRemoveSpeaker: function() {
            console.log("Error removing Speaker from a remote server");
        },

        dialogHidden: function() {
            var view = this;
            view.render();
        },

        collectFields: function() {
            var view = this;
            return {
                name        : $(view.NAME_SELECTOR).val(),
                position    : $(view.POSITION_SELECTOR).val(),
                description : $(view.DESCRIPTION_SELECTOR).val()
            }
        }
    });

    return SpeakersListView;
});
