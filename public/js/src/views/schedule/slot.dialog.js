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

            $('#slot-start-time').timepicker({
                defaultTime: '10:00',
                minuteStep: 5,
                showInputs: false,
                showMeridian: false
            });

            $('#slot-end-time').timepicker({
                defaultTime: '11:00',
                minuteStep: 5,
                showInputs: false,
                showMeridian: false
            });

            this.attachEventHandlers();
            this.showDialog();
        },

        confirm: function () {
            this.attachConfirmationHandler();
            this.hideDialog();
        },

        renderTemplate: function () {
            this.$el = $(this.template());
            this.$el.appendTo(this.PARENT_SELECTOR);
        },

        attachEventHandlers: function () {
            this.$el.one('hidden.bs.modal', _.bind(this.remove, this));
            this.$(this.CONFIRM_BUTTON_SELECTOR).click(_.bind(this.confirm, this));
        },

        attachConfirmationHandler: function () {
            if(!_.isUndefined(this.callback)) {
                this.$el.one("hidden.bs.modal", this.callback);
            }
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