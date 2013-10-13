define([
    "underscore",
    "backbone",
    "views/add.event.dialog",
    "require.text!templates/events.html"],

function(_, Backbone, AddEventDialog, template) {
    var EventsView = Backbone.View.extend({
        CREATE_EVENT_BUTTON: "#btn-create-event",

        el: "#content",
        dialogView: null,
        template: _.template(template),

        render: function () {
            this.$el.html(this.template());
            this.$(this.CREATE_EVENT_BUTTON).click(this, this.createEvent);

            return this;
        },
        
        createEvent: function(event) {
            var view = event.data;
            if (view.dialogView) {
                view.dialogView.undelegateEvents();
            }
            view.dialogView = new AddEventDialog();
            view.dialogView.render();
        }
    });

    return EventsView;
});