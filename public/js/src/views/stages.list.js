define([
    'jquery',
    'underscore',
    'backbone',
    'models/model',
    'backbone.validation',
    'validation/validation.handler',
    'require.text!templates/stages.list.html'
],

function($, _, Backbone, Model, Validation, ValidationHandler, stagesListTemplate) {

    var StagesListView = Backbone.View.extend({
        DIALOG_SELECTOR: "#dlg-stage",

        ID_SELECTOR: "#stage-id",
        TITLE_SELECTOR: "#stage-title",
        CAPACITY_SELECTOR: "#stage-capacity",

        template : _.template(stagesListTemplate),

        events: {
            "click #save-stage"                     : "saveStage",
            "click button[id^='btn-edit-stage-']"   : "showModal",
            "click button[id^='btn-remove-stage-']" : "removeStage"
        },

        initialize: function(options) {
            _.bindAll(this, "stageSaved", "stageRemoved", "errorSaveStage", "errorRemoveStage", "dialogHidden");
            this.eventModel = options.eventModel;
        },

        render : function() {
            this.$el.html(this.template({stages : this.eventModel.getStages()}));

            return this;
        },

        showModal : function(event) {
            var $target = $(event.target),
                view = this,
                event = view.eventModel,
                buttonId = $target.attr('id'),
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

            $(view.DIALOG_SELECTOR).one('hidden.bs.modal', view, view.dialogHidden);
        },

        removeStage : function(event) {
            var $target = $(event.target),
                view = this,
                eventModel = view.eventModel,
                buttonId = $target.attr('id'),
                stageId = buttonId.replace('btn-remove-stage-', '');

            var stage = eventModel.getStage(stageId);
            stage.destroy({
                success: view.stageRemoved,
                error: view.errorRemoveStage
            });
        },

        saveStage: function() {
            var view = this,
                stageId = $(view.ID_SELECTOR).val(),
                attributes = view.collectFields(),
                stage = isNaN(parseInt(stageId)) ? null : view.eventModel.getStage(stageId);

            view.model = stage
                ? new Model.Stage(stage.toJSON(), {eventId: view.eventModel.id})
                : new Model.Stage({}, {eventId: view.eventModel.id});

            Validation.bind(view, new ValidationHandler("stage."));

            view.model.set(attributes);

            view.model.save({}, {
                success: view.stageSaved,
                error: view.errorSaveStage
            });
        },

        stageSaved: function(model, response, options) {
            var view = this;
            view.eventModel.saveStage(model);
            $(view.DIALOG_SELECTOR).modal('hide');
        },

        dialogHidden: function() {
            var view = this;
            view.render();
        },

        stageRemoved: function(model, response, options) {
            var view = this;
            view.eventModel.removeStage(model);
            view.render();
        },

        errorSaveStage: function(model, xhr, options) {
            console.log("Error saving updates (stages changed) to a model");
        },

        errorRemoveStage: function(model, xhr, options) {
            console.log("Error trying to remove the stage");
        },

        collectFields: function() {
            var view = this;
            return {
                title:      $(view.TITLE_SELECTOR).val(),
                capacity:   $(view.CAPACITY_SELECTOR).val()
            }
        }
    });

    return StagesListView;
});
