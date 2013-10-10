define([
    "underscore",
    "backbone",
    "require.text!templates/details.html"
],

function(_, Backbone, template) {
    var DetailsView = Backbone.View.extend({
        template : _.template(template),
        el : '#details',

        render: function() {
            console.log("details render");
            var renderedTemplate = this.template({event: this.model});
            this.$el.html(renderedTemplate);
            return this;
        }
    });

    return DetailsView;
});