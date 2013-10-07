define([
    'underscore',
    'jquery',
    'backbone',
    'models/model',
    'require.text!templates/add.event.dialog.html'
],
function(_, $, Backbone, Model, addEventDialogTemplate) {
    var AddEventDialog = Backbone.View.extend({
        template: _.template(addEventDialogTemplate),

        render: function() {
            this.$el = $(this.template());
            this.$el.appendTo("#events-sidebar");
            this.$el.modal();

            this.$("#save-new-event").click(this, this.saveNewEvent);
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
        }
    });

    return AddEventDialog;
});
