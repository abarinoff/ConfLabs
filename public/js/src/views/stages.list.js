define([
    'jquery',
    'underscore',
    'backbone',
    'models/model',
    'require.text!templates/stages.list.html'
],

function($, _, Backbone, Model, stagesListTemplate) {

    var StagesListView = Backbone.View.extend({
        DIALOG_SELECTOR: "#dlg-stage",
        ADD_BUTTON_SELECTOR: "button[id^='btn-edit-stage-']",
        REMOVE_BUTTON_SELECTOR: "button[id^='btn-remove-stage-']",

        ID_SELECTOR: "#stage-id",
        TITLE_SELECTOR: "#stage-title",
        CAPACITY_SELECTOR: "#stage-capacity",

        template : _.template(stagesListTemplate),
        el : '#stages',

        initialize: function() {
            _.bindAll(this, "stageSaved", "stageRemoved", "errorSaveStage", "errorRemoveStage");
        },

        render : function() {
            this.$el.html(this.template({stages : this.model.getStages()}));

            $(this.ADD_BUTTON_SELECTOR).click(this, this.showModal);
            $(this.REMOVE_BUTTON_SELECTOR).click(this, this.removeStage);

            return this;
        },

        showModal : function(event) {
            var $this = $(this),
                view = event.data,
                event = view.model,
                buttonId = $this.attr('id'),
                stageId = buttonId.replace('btn-edit-stage-', '');

            if (stageId == "new") {
                $(view.TITLE_SELECTOR).val('');
                $(view.CAPACITY_SELECTOR).val('');
                $(view.ID_SELECTOR).val('');
            }
            else {
                var stage = event.getStage(stageId);

                $(view.TITLE_SELECTOR).val(stage.get('title'));
                $(view.CAPACITY_SELECTOR).val(stage.get('capacity'));
                $(view.ID_SELECTOR).val(stage.id);
            }
            $(view.DIALOG_SELECTOR).modal();

            // Dialog Buttons
            $("#save-stage").one('click', view, view.saveStage);
            $(view.DIALOG_SELECTOR).one('hidden.bs.modal', view, view.dialogHidden);
        },

        removeStage : function(event) {
            var $this = $(this),
                view = event.data,
                eventModel = view.model,
                buttonId = $this.attr('id'),
                stageId = buttonId.replace('btn-remove-stage-', '');

            var stage = eventModel.getStage(stageId);
            stage.destroy({
                success: view.stageRemoved,
                error: view.errorRemoveStage
            });
        },

        saveStage: function(event) {
            var view = event.data,
                eventModel = view.model,
                stageId = view.$(view.ID_SELECTOR).val(),
                stageTitle = view.$(view.TITLE_SELECTOR).val(),
                stageCapacity = view.$(view.CAPACITY_SELECTOR).val();

            var attributes = isNaN(parseInt(stageId)) ? {} : eventModel.getStage(stageId).toJSON(),
                stage = new Model.Stage(attributes, {eventId: eventModel.id});


            stage.set({
                title: stageTitle,
                capacity: stageCapacity
            });

            stage.save({}, {
                success: view.stageSaved,
                error: view.errorSaveStage
            });
        },

        stageSaved: function(model, response, options) {
            var event = this.model;
            event.saveStage(model);
            $(this.DIALOG_SELECTOR).modal('hide');
        },

        dialogHidden: function(event) {
            var view = event.data;
            view.render();
        },

        errorSaveStage: function(model, xhr, options) {
            console.log("Error saving updates (stages changed) to a model");
        },

        stageRemoved: function(model, response, options) {
            var event = this.model;
            event.removeStage(model);
            this.render();
        },

        errorRemoveStage: function(model, xhr, options) {
            console.log("Error trying to remove the stage");
        }
    });

    return StagesListView;
});
