define([
    "underscore",
    "backbone",
    "views/add.event.dialog",
    "require.text!templates/events.html"],

function(_, Backbone, AddEventDialog, template) {
    var EventsView = Backbone.View.extend({

        el: "#content",
        template: _.template(template),
        /*events: {
            "click #btn-create-event":  "showCreateEventDialog"
        },*/

        createEventDialog: undefined,

        render: function () {
            this.$el.html(this.template());
            $("#btn-create-event").click(this, this.showCreateEventDialog);

            return this;
        },

        showCreateEventDialog: function(event) {
            console.log("showCreateEventDialog");
            var view = event.data;
            view.createEventDialog = this.createEventDialog || new AddEventDialog();
            view.createEventDialog.render();
        }
    });

    return EventsView;
});