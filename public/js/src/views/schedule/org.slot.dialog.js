define([
    'underscore',
    'views/schedule/slot.dialog',
    'require.text!templates/org.slot.dialog.html'
],
function(_, SlotDialogView, dialogTemplate) {
    var OrgSlotDialogView = SlotDialogView.extend({

        slotDialogTemplate: _.template(dialogTemplate),

        render: function() {
            SlotDialogView.prototype.render.call(this);

            this.$("#slot-child-fields").append(this.slotDialogTemplate());
        },

        // Override
        attachConfirmationHandler: function () {
            var data = {
                start: $("#slot-start-time").val(),
                end: $("#slot-end-time").val(),
                title: $("#slot-title").val()
            };
            var handler = _.bind(this.confirmationHandler, this, data);

            this.$el.one("hidden.bs.modal", handler);
        }
    });

    return OrgSlotDialogView;
});
