define([
    'jquery',
    'underscore',
    'backbone',
    'models/model',
    'require.text!templates/speeches.list.html'
], function($, _, Backbone, Model, speechesTemplate) {

    var SpeechesListView = Backbone.View.extend({
        template: _.template(speechesTemplate),

        events : {
            "click button[id^='remove-speech-']"        : "removeSpeech",
            "click button[id^='edit-speech-']"          : "editSpeech"
        },

        initialize: function(options) {
            this.speeches = options.speeches;
            this.speaker = options.speaker;
        },

        render: function() {
            this.$el.html(this.template({
                speakerId: this.speaker.getId(),
                speeches: this.speeches
            }));

            return this;
        },

        editSpeech: function(event) {
            var view = this,
                speechId = this.extractSpeechId(event.target);

            console.log("Edit speech clicked, handler belongs to SpeechesListView, speech id: " + speechId);
         },

        removeSpeech: function(event) {
            var view = this,
                speechId = this.extractSpeechId(event.target);
            console.log("Remove speech clicked, handler belongs to SpeechesListView, speech id: " + speechId);

            // debug
            this.speaker.unsetSpeech();
            // end debug
        },

        extractSpeechId: function(selector) {
            var elementId = $(selector).attr('id');
            return elementId.substr(elementId.lastIndexOf("-") + 1);
        }
    });

    return SpeechesListView;
});
