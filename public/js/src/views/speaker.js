define([
    'jquery',
    'underscore',
    'backbone',
    'models/model',
    'views/speeches.list',
    'require.text!templates/speaker.html',
    'require.text!templates/add.speech.dialog.html'
],
function($, _, Backbone, Model, SpeechesListView, speakerTemplate, addSpeechDialogTemplate) {
    var SpeakerView = Backbone.View.extend({
        ADD_SPEECH_MODAL: "#dlg-speech",

        template: _.template(speakerTemplate),
        tagName : 'div',
        id      : function() {return "speaker-" + this.model.id},

        events: {
            "click button[id^='btn-add-speech-']": "showAddSpeechModal"
        },

        initialize: function(options) {
            this.model = options.model;
            this.eventModel = options.eventModel;
        },

        render: function() {
            this.$el.append(this.template({speaker: this.model}));
            this.renderSpeeches(this.$(".speeches-list"));

            return this;
        },

        renderSpeeches: function(el) {
            var speeches = this.eventModel.getSpeechesForSpeaker(this.model.id);

            new SpeechesListView({
                el: el,
                speaker: this.model,
                speeches: speeches
            }).render();
        },

        showAddSpeechModal: function() {
            var template = _.template(addSpeechDialogTemplate);
            var speeches = this.eventModel.getAvailableSpeeches(this.model.id);

            this.$el.append($(template({speeches: speeches})));

            $(this.ADD_SPEECH_MODAL).modal();
            $(this.ADD_SPEECH_MODAL).on('hidden.bs.modal', this.onDialogClosed.bind(this));

            /*// Initialize controls of the dialog
            $(this.SPEECH_SPEAKER_ID).val(speakerId);
            $(this.SELECT_SPEECH_BLOCK).removeClass("hidden");

            // We have to populate the speech select element with options that has not been selected yet
            var unassignedSpeeches = this.eventModel.getAvailableSpeeches(speakerId);

            var $select = $(view.SPEECHES_SELECT)
                .empty()
                .append("<option value=''>Add New</option>");
            _.each(unassignedSpeeches, function(speech){
                $select.append($("<option value='" + speech.getId() + "'>" + speech.getTitle() + "</option>"));
            });*/
        },

        onDialogClosed: function() {
            var view = this;
            view.$(view.ADD_SPEECH_MODAL).remove();
        }

    });

    return SpeakerView;
});
