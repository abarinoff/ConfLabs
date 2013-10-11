define([
    "underscore",
    "backbone",
    "require.text!templates/events.html"],

function(_, Backbone, template) {
    var EventsView = Backbone.View.extend({

        el: "#content",
        template: _.template(template),

        render: function () {
            this.$el.html(this.template());

            // Bootstrap affix
            $(".events-sidebar").affix({
                offset: {
                    top: 0,
                    bottom: 40
                }
            });

            return this;
        }
    });

    return EventsView;
});