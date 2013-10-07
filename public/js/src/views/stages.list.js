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
            }
            else {
                var stage = _.findWhere(stages, {'id' : parseInt(stageId)});

                $("#stage-title").val(stage.title);
                $("#stage-capacity").val(stage.capacity);
            }
            $("#dlg-stage").modal();
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
        }
    });

    return StagesListView;
});
