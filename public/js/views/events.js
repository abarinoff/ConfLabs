define(['jquery', 'underscore', 'backbone', 'require.text!templates/events.html'], function($, _, Backbone, eventsTemplate) {
    var EventsView = Backbone.View.extend({
        template: _.template(eventsTemplate),

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            this.$el.html(this.template({events: this.model.models}));
            return this;
        }
    });

    return EventsView;
});