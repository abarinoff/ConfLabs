define([
    'underscore',
    'backbone',
    'require.text!templates/unscheduled.speeches.list.html'
], function(_, Backbone, unscheduledItemsTemplate) {

    var UnscheduledItems = Backbone.View.extend({
        initialize: function(options) {
            this.speeches = options.speeches;
        },

        template: _.template(unscheduledItemsTemplate),

        render: function() {
            this.$el = this.template({speeches: this.speeches});

            return this;
        }
    });

    return UnscheduledItems;
});
