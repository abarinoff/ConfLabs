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
                    // position at which we switch to affix from affix-top (?)
                    /*top: function() {
                        var offsetTop = $(".events-sidebar").offset().top,
                            sidebarMargin = parseInt($("#list-events").css('margin-top'), 10),
                            navHeight = $("#navbar").height();

                        return (this.top = offsetTop - navHeight - sidebarMargin)
                    },*/
                    top: 0,
                    bottom: 81
                    /*bottom: function() {
                        return (this.bottom = $('footer').outerHeight(true))
                    }*/
                }
            });

            return this;
        }
    });

    return EventsView;
});