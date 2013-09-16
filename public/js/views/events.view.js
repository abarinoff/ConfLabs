var application = application || {};

(function($) {
    application.EventsView = Backbone.View.extend({
        template: _.template($('#events-template').html()),

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            this.$el.html(this.template({events: this.model.models}));
            return this;
        }
    });
})(jQuery);