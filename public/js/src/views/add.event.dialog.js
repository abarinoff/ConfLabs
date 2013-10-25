define([
    'underscore',
    'jquery',
    'backbone',
    'models/model',
    'backbone.validation',
    'validation/validation.handler',
    'require.text!templates/add.event.dialog.html'
],
function(_, $, Backbone, Model, Validation, ValidationHandler, addEventDialogTemplate) {
    var AddEventDialog = Backbone.View.extend({

        el: "#events-sidebar",
        template: _.template(addEventDialogTemplate),
        events: {
            "click #save-new-event"             : "saveEvent",
            "hidden.bs.modal #dlg-add-event"    : "onDialogClosed"
        },

        initialize: function(options) {
            this.parentView = options.parent;
        },

        render: function() {
            var $modal = $(this.template());
            this.$el.append($modal);
            $modal.modal();

            return this;
        },

        saveEvent: function() {
            console.log("save event");
            var view = this,
                eventTitle = view.$("#new-event-title").val();

            this.model = new Model.Event();
            Validation.bind(view, new ValidationHandler("new-event."));

            this.model.set({'title': eventTitle});
            this.model.save({}, {
                success : this.eventSaved.bind(this),
                error   : this.eventSaveError.bind(this)
            });
        },

        eventSaved: function(eventModel) {
            var view = this;
            view.parentView.eventCreated(eventModel);

            view.eventId = eventModel.id;
            view.$("#dlg-add-event").modal('hide');
        },

        eventSaveError: function() {
            console.log("Error trying to save Event");
        },

        onDialogClosed: function() {
            console.log("dialog closed, remove the dialog html");
            this.$("#dlg-add-event").remove();
            if (this.eventId !== undefined) {
                window.application.router.navigate("events/" + this.eventId, {trigger: false});
                this.eventId = undefined;
            }
        }
    });

    return AddEventDialog;
});
