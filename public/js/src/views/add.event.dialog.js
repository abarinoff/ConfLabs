define([
    'underscore',
    'jquery',
    'backbone',
    'models/model',
    'require.text!templates/add.event.dialog.html'
],
function(_, $, Backbone, Model, addEventDialogTemplate) {
    var AddEventDialog = Backbone.View.extend({

        el: "#events-sidebar",
        template: _.template(addEventDialogTemplate),
        events: {
            "click #save-new-event" : "saveEvent"
        },

        render: function() {
            var $modal = $(this.template());
            this.$el.append($modal);

            $modal.modal();
            $modal.on('hidden.bs.modal', this.onDialogClosed.bind(this));
        },
        
        saveNewEvent : function(event) {
            var view = event.data,
                eventTitle = view.$("#new-event-title").val(),
                event = new Model.Event({'title': eventTitle});

            event.save({}, {
                success: function(event, response, options) {
                    var id = event.id;
                    view.$el.one('hidden.bs.modal', id, function(event) {
                        var id = event.data;
                        window.application.router.navigate("events/" + id, {trigger: true});
                    });
                    view.$el.modal('hide');
                }
            });
        },

        saveEvent: function() {
            console.log("save new event");
        },

        onDialogClosed: function() {
            console.log("dialog closed, remove the dialog html");
            console.log(this.$("#dlg-add-event").length);
            this.$("#dlg-add-event").remove();
        }

    });

    return AddEventDialog;
});
