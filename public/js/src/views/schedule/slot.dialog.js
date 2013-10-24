define([
    "underscore",
    "jquery",
    "backbone",
    "require.text!templates/slot.dialog.html"
],

function(_, $, Backbone, template) {

    var SlotDialog = Backbone.View.extend({
        PARENT_SELECTOR: "body",
        CONFIRM_BUTTON_SELECTOR: "#btn-slot-save",

        template: _.template(template),

        initialize: function(options) {
            this.callback = options.callback;
        },

        render: function() {
            this.renderTemplate();
            this.attachEventHandlers();
            this.showDialog();
        },

        confirm: function () {
            this.attachConfirmationHandler();
            this.hideDialog();
        },

        confirmationHandler: function(data) {
            if(!_.isUndefined(this.callback)) {
                this.callback(data);
            }
        },

        renderTemplate: function () {
            this.$el = $(this.template());
            this.$el.appendTo(this.PARENT_SELECTOR);
        },

        attachEventHandlers: function () {
            $('#slot-start-time').timepicker({
                defaultTime: '9:00',
                minuteStep: 5,
                showInputs: false,
                showMeridian: false
            });

            $('#slot-end-time').timepicker({
                defaultTime: '10:00',
                minuteStep: 5,
                showInputs: false,
                showMeridian: false
            });

            this.$el.one('hidden.bs.modal', _.bind(this.remove, this));
            this.$(this.CONFIRM_BUTTON_SELECTOR).click(_.bind(this.confirm, this));
        },

        attachConfirmationHandler: function () {
            var data = {
                start: $("#slot-start-time").val(),
                end: $("#slot-end-time").val()};
            var handler = _.bind(this.confirmationHandler, this, data);

            this.$el.one("hidden.bs.modal", handler);
        },

        showDialog: function () {
            this.$el.modal();
        },

        hideDialog: function () {
            this.$el.modal("hide");
        }
    });

    return SlotDialog;
});