define([
    'jquery',
    'underscore',
    'backbone',
    'require.text!templates/stages.list.html'
],

function($, _, Backbone, stagesListTemplate) {

    var StagesListView = Backbone.View.extend({
        template : _.template(stagesListTemplate),
        el : '#stages',

        render : function() {
            this.$el.html(this.template({stages : this.model.get('stages')}));

            $("button[id^='btn-edit-stage-']").click(this, this.showModal);
            $("button[id^='btn-remove-stage-']").click(this, this.removeStage);

            return this;
        },

        showModal : function(event) {
            var $this = $(this),
                view = event.data,
                stages = view.model.get('stages'),
                buttonId = $this.attr('id'),
                stageId = buttonId.replace('btn-edit-stage-', '');

            if (stageId == "new") {
                $("#stage-title").val('');
                $("#stage-capacity").val('');
                $("#stage-id").val('');
            }
            else {
                var stage = _.findWhere(stages, {'id' : parseInt(stageId)});

                $("#stage-title").val(stage.title);
                $("#stage-capacity").val(stage.capacity);
                $("#stage-id").val(stage.id);
            }
            $("#dlg-stage").modal();

            // Set listener for a Save button
            $("#save-stage").one('click', view, view.updateModel);
        },

        removeStage : function(event) {
            var $this = $(this),
                view = event.data,
                stages = view.model.get('stages'),
                buttonId = $this.attr('id'),
                stageId = buttonId.replace('btn-remove-stage-', '');

            var stage = _.findWhere(stages, {'id' : parseInt(stageId)});
            var stageIndex = _.indexOf(stages, stage);

            stages.splice(stageIndex, 1);
            view.model.trigger('change:stages');

            // Render the view after the model has been deleted
            view.render();
        },

        updateModel: function(event) {
            var view = event.data,
                model = view.model,
                stages = model.get('stages'),
                stageId = view.$("#stage-id").val(),
                stageTitle = view.$('#stage-title').val(),
                stageCapacity = view.$('#stage-capacity').val();

            var stage;
            if (stageId) {
                stage = _.findWhere(stages, {'id': parseInt(stageId)});
            }
            else {
                stage = {};
                stages.push(stage);
            }
            stage.title = stageTitle;
            stage.capacity = stageCapacity;

            // @todo Validation required
            model.save({}, {
                success: function(model, response, options) {
                    console.log("Model saved (changes to stages has been applied)");
                },
                error: function(model, xhr, options) {
                    console.log("Error saving updates (stages changed) to a model");
                }
            });
        }
    });

    return StagesListView;
});
