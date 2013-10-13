define([
    'underscore',
    'jquery',
    'backbone',
    'models/model',
    'require.text!templates/add.event.dialog.html'
],
function(_, $, Backbone, Model, addEventDialogTemplate) {
    var AddEventDialog = Backbone.View.extend({
        DIALOG_SELECTOR: ".modal",
        SAVE_EVENT_BUTTON_SELECTOR: "#save-new-event",
        EVENT_TITLE_SELECTOR: "#new-event-title",

        initialize: function() {
            _.bindAll(this, "eventCreated");
        },

        el: "#create-event-dialog",
        events: {
            "click #save-new-event": "createEvent"
        },

        template: _.template(addEventDialogTemplate),

        render: function() {
            this.$el.html(this.template());
            this.$(this.DIALOG_SELECTOR).modal();

            return this.el;
        },
        
        createEvent : function() {
            var view = this,
                eventTitle = view.$(this.EVENT_TITLE_SELECTOR).val(),
                event = new Model.Event({'title': eventTitle});

            event.save({}, {
                success: view.eventCreated
            });
        },

        eventCreated: function(event, response, options) {
            var view = this,
                id = event.id;

            view.$(view.DIALOG_SELECTOR).one('hidden.bs.modal', id, function(event) {
                var id = event.data;
                window.application.router.navigate("events/" + id, {trigger: true});
            });
            view.$(view.DIALOG_SELECTOR).modal('hide');
        }
    });

    return AddEventDialog;
});
