define([
    'underscore',
    'backbone',
    'models/model',
    'require.text!templates/day.html'
],
function(_, Backbone, Model, dayTemplate) {
    var DayView = Backbone.View.extend({

        template: _.template(dayTemplate),

        initialize: function(options) {
            this.stages = options.stages;
            this.date = options.date;
        },

        render: function() {
            this.$el = $(this.template({
                stages  : this.stages,
                date    : this.date
            }));

            return this;
        }
    });

    return DayView;
});
