define([
    "underscore",
    "jquery",
    "backbone",
    "require.text!templates/remove.event.dialog.html"
],

function (_, $, Backbone, template) {
    var RemoveEventDialog = Backbone.View.extend({
        PARENT_SELECTOR: "#events-sidebar",
        CONFIRM_BUTTON_SELECTOR: "#btn-remove-event-confirm",

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

        renderTemplate: function () {
            this.$el = $(this.template({event: this.model}));
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

    return RemoveEventDialog;
});
