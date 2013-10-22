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
        },

        render: function() {
            this.$el.append(this.template({speaker: this.model}));
            this.speechesView = this.renderSpeeches(this.$(".speeches-list"));

            return this;
        },

        renderSpeeches: function(el) {
            return new SpeechesListView({
                el: el,
                speaker: this.model,
                eventModel: this.eventModel
            }).render();
        },

        addSpeech: function() {
            this.speechesView.addSpeech();
        }
    });

    return SpeakerView;
});
