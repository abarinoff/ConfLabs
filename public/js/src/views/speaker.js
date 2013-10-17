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

        initialize: function(options) {
            this.speaker = options.speaker;
            this.eventModel = options.eventModel;
        },

        render: function() {
            var $speakerHtml = $(this.template({speaker: this.speaker}));
            var speechesListView = this.renderSpeeches($speakerHtml.find(".speeches-list"));
            this.$el.append($speakerHtml);

            return this;
        },

        renderSpeeches: function(el) {
            var speeches = this.eventModel.getSpeechesForSpeaker(this.speaker.id),
                speechesListView = new SpeechesListView({el: el, speaker: this.speaker, speeches: speeches}).render();

            return speechesListView;
        }
    });

    return SpeakerView;
});
