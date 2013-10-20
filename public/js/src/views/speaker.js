define([
    'jquery',
    'underscore',
    'backbone',
    'models/model',
    'views/speeches.list',
    'require.text!templates/speaker.html'
],
function($, _, Backbone, Model, SpeechesListView, speakerTemplate) {
    var SpeakerView = Backbone.View.extend({

        template: _.template(speakerTemplate),
        tagName : 'div',
        id      : function() {return "speaker-" + this.model.id},

        events  : {
            "click button[id^='btn-add-speech-']"       : "addSpeech"
        },

        speechesView: undefined,

        initialize: function(options) {
            this.model = options.model;
            this.eventModel = options.eventModel;
            console.log(this.eventModel);
        },

        render: function() {
            this.$el.append(this.template({speaker: this.model}));
            this.speechesView = this.renderSpeeches(this.$(".speeches-list"));

            return this;
        },

        renderSpeeches: function(el) {
            var speeches = this.eventModel.getSpeechesForSpeaker(this.model.id);

            return new SpeechesListView({
                el: el,
                speaker: this.model,
                speeches: speeches,
                availableSpeeches: this.eventModel.getAvailableSpeeches(this.model.id)
            }).render();
        },

        addSpeech: function() {
            this.speechesView.addSpeech();
        }
    });

    return SpeakerView;
});
