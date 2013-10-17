define([
    'jquery',
    'underscore',
    'backbone',
    'models/model',
    'require.text!templates/speeches.list.html'
], function($, _, Backbone, Model, speechesTemplate) {
    var SpeechesListView = Backbone.View.extend({
        template: _.template(speechesTemplate),

        initialize: function(options) {
            this.speeches = options.speeches;
            this.speaker = options.speaker;
        },

        render: function() {
            this.$el.html(this.template({speakerId: this.speaker.getId(), speeches: this.speeches}));
            return this;
        }
    });

    return SpeechesListView;
});
