define([
    'jquery',
    'underscore',
    'backbone',
    'models/model',
    'require.text!templates/speeches.list.html',
    'require.text!templates/add.speech.dialog.html'
], function($, _, Backbone, Model, speechesTemplate, addSpeechDialogTemplate) {

    var SpeechesListView = Backbone.View.extend({
        ADD_SPEECH_MODAL    : "#dlg-speech",
        SPEECH_SELECT       : "#existing-speeches",
        SPEECH_TITLE        : "#new-speech-title",
        SPEECH_ID           : "#hdn-speech-id",

        SELECT_SPEECH_BLOCK : "#select-speech-block",
        NEW_SPEECH_BLOCK    : "#new-speech-block",

        template: _.template(speechesTemplate),

        events : {
            "click button[id^='edit-speech-']"          : "editSpeech",
            "click button[id^='remove-speech-']"        : "removeSpeech",
            "click button#save-speech"                  : "saveSpeech"
        },

        initialize: function(options) {
            this.speaker = options.speaker;
            this.eventModel = options.eventModel;

            // Aliases for speeches of a current speaker and speeches that can be assigned to a current speaker
            this.speeches = this.eventModel.getSpeechesForSpeaker(this.speaker.id);
            this.availableSpeeches = this.eventModel.getAvailableSpeeches(this.speaker.id);
        },

        render: function() {
            this.$el.html(this.template({
                speakerId: this.speaker.getId(),
                speeches: this.speeches
            }));

            return this;
        },

        addSpeech: function() {
            this.showAddSpeechModal(false);
        },

        saveSpeech: function() {
            var speechId, speech;

            if ($(this.SPEECH_SELECT).length > 0) {
                speechId = $(this.SPEECH_SELECT).val();
                if (!speechId) {
                    var speechTitle = $(this.SPEECH_TITLE).val();
                    speech = new Model.Speech({
                        title: speechTitle}, {
                        eventId: this.speaker.eventId,
                        speakerId: this.speaker.id
                    });
                }
                else {
                    speech = _.findWhere(this.availableSpeeches, {id: parseInt(speechId)});
                }
            }
            else {
                speechId = $(this.SPEECH_ID).val();
                speech = _.findWhere(this.speeches, {id: parseInt(speechId)});
                speech.setTitle($(this.SPEECH_TITLE).val());
            }
            speech.setSpeakerId(this.speaker.id);

            speech.save({}, {
                success: this.speechSaved.bind(this),
                error: this.speechSaveError.bind(this)
            });
        },

        speechSaved: function(speechModel) {
            var view = this;

            // We have to add a new speech to the Speeches array of the current speaker as well as to the Event's speeches array
            this.speaker.saveSpeech(speechModel);
            this.eventModel.saveSpeech(speechModel);

            view.$(view.ADD_SPEECH_MODAL).modal('hide');
        },

        speechSaveError: function() {
            console.log("Error saving a new speech");
        },

        editSpeech: function(event) {
            var view = this,
                speechId = this.extractSpeechId(event.target);

            var speech = _.findWhere(this.speeches, {id: parseInt(speechId)});
            this.showAddSpeechModal(true, {
                id: speechId,
                title: speech.getTitle()
            });
        },

        removeSpeech: function(event) {
            var view = this,
                speechId = this.extractSpeechId(event.target),
                speech = this.getSpeechById(speechId);

            speech.setSpeakerId(this.speaker.id);
            speech.destroy({
                success: this.speechDestroyed.bind(this),
                error: this.speechDestroyError.bind(this)
            });
        },

        speechDestroyed: function(speechModel) {
            this.speaker.unsetSpeech(speechModel);
            this.eventModel.onSpeechUnset(speechModel);

            this.eventModel.trigger("speech:changed");
        },

        speechDestroyError: function() {
            console.log("Error deleting given speech");
        },

        getSpeechById: function(id) {
            return _.findWhere(this.speeches, {id: parseInt(id)});
        },

        extractSpeechId: function(selector) {
            var elementId = $(selector).attr('id');
            return elementId.substr(elementId.lastIndexOf("-") + 1);
        },

        showAddSpeechModal: function(edit, speech) {
            edit = edit || false;
            speech = speech || {id: '', title: ''}

            var template = _.template(addSpeechDialogTemplate);
            var speeches = this.availableSpeeches,
                dialogContent = $(template({
                    speeches: speeches,
                    edit    : edit,
                    speech  : speech
                }));
            this.$el.append(dialogContent);

            $(this.ADD_SPEECH_MODAL).modal();
            $(this.ADD_SPEECH_MODAL).on('hidden.bs.modal', this.onDialogClosed.bind(this));

            this.setDialogControls();
        },

        setDialogControls: function() {
            $(this.SPEECH_SELECT).change(this, function(event){
                var view = event.data,
                    $select = $(this);

                if ($select.val() === '') {
                    $(view.NEW_SPEECH_BLOCK).removeClass("hidden");
                }
                else {
                    $(view.NEW_SPEECH_BLOCK).addClass("hidden");
                }
            });
        },

        onDialogClosed: function() {
            var view = this;
            view.$(view.ADD_SPEECH_MODAL).remove();
            view.eventModel.trigger("speech:changed");
        }
    });

    return SpeechesListView;
});
