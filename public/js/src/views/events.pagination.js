define([
    "underscore",
    "backbone",
    "require.text!templates/events.pagination.html"],

function(_, Backbone, template) {
    var EventsPaginationView = Backbone.View.extend({
        el: "#events-pagination",
        template: _.template(template),

        events: {
            "click a.previous": "showPrevious",
            "click a.next": "showNext",
            "click a.page": "showPage"
        },

        initialize: function() {
            this.model.on("reset", this.render, this);
        },

        render: function() {
            this.$el.html(this.template(this.model.info()));
            return this;
        },

        showPrevious: function(event) {
            event.preventDefault();
            this.model.resetActiveEventId();
            this.model.previousPage();
        },

        showNext: function(event) {
            event.preventDefault();
            this.model.resetActiveEventId();
            this.model.nextPage();
        },

        showPage: function(event) {
            event.preventDefault();
            var page = $(event.target).text();
            this.model.resetActiveEventId();
            this.model.goTo(page);
        }
    });

    return EventsPaginationView;
});