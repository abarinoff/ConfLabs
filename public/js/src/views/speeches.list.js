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

        SELECT_SPEECH_BLOCK : "#select-speech-block",
        NEW_SPEECH_BLOCK    : "#new-speech-block",

        template: _.template(speechesTemplate),

        events : {
            "click button[id^='edit-speech-']"          : "editSpeech",
            "click button[id^='remove-speech-']"        : "removeSpeech",
            "click button#save-speech"                  : "saveSpeech"
        },

        initialize: function(options) {
            this.speeches = options.speeches;
            this.speaker = options.speaker;
            this.availableSpeeches = options.availableSpeeches;
        },

        render: function() {
            this.$el.html(this.template({
                speakerId: this.speaker.getId(),
                speeches: this.speeches
            }));

            return this;
        },

        addSpeech: function() {
            this.showAddSpeechModal();
        },

        editSpeech: function(event) {
            var view = this,
                speechId = this.extractSpeechId(event.target);

            console.log("Edit speech clicked, handler belongs to SpeechesListView, speech id: " + speechId);
         },

        removeSpeech: function(event) {
            var view = this,
                speechId = this.extractSpeechId(event.target);

            var speech = this.getSpeechById(speechId);
            this.speaker.detachSpeech(speech);
        },

        getSpeechById: function(id) {
            return _.findWhere(this.speeches, {id: parseInt(id)});
        },

        extractSpeechId: function(selector) {
            var elementId = $(selector).attr('id');
            return elementId.substr(elementId.lastIndexOf("-") + 1);
        },

        showAddSpeechModal: function() {
            var template = _.template(addSpeechDialogTemplate);
            var speeches = this.availableSpeeches;

            this.$el.append($(template({speeches: speeches})));

            $(this.ADD_SPEECH_MODAL).modal();
            $(this.ADD_SPEECH_MODAL).on('hidden.bs.modal', this.onDialogClosed.bind(this));

            this.setDialogControls();
        },

        saveSpeech: function() {
            console.log("save speech for speaker " + this.speaker.id);
            var speechId = $(this.SPEECH_SELECT).val();
            var speech = undefined;
            if (!speechId) {
                console.log("Creating a new speech");
                speech = new Model.Speech();
            }
            else {
                console.log("Looking for an existing speech");
                speech = _.findWhere(this.speeches, {id: speechId});
            }
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
        }
    });

    return SpeechesListView;
});
