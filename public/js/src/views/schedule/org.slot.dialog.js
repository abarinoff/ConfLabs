define([
    'underscore',
    'views/schedule/slot.dialog',
    'require.text!templates/org.slot.dialog.html'
],
function(_, SlotDialog, template) {
    var OrgSlotDialog = SlotDialog.extend({

        customTemplate: _.template(template),

        renderCustomTemplate: function() {
            this.$("#slot-child-fields").append(this.customTemplate());
        },

        renderData: function() {
            SlotDialog.prototype.renderData.call(this);

            var title = _.isUndefined(this.data) ? "" : this.data.title;
            $("#slot-title").val(title);
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

    return OrgSlotDialog;
});
