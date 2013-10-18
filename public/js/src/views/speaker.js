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
            this.model = options.model;
            this.eventModel = options.eventModel;
        },

        render: function() {
            var $speakerHtml = $(this.template({speaker: this.model}));
            this.renderSpeeches($speakerHtml.find(".speeches-list"));
            this.$el.append($speakerHtml);

            return this;
        },

        renderSpeeches: function(el) {
            var speeches = this.eventModel.getSpeechesForSpeaker(this.model.id);

            new SpeechesListView({
                el: el,
                speaker: this.model,
                speeches: speeches
            }).render();
        }
    });

    return SpeakerView;
});
