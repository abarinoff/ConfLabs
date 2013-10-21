define([
    "underscore",
    "backbone",
    "require.text!templates/schedule.html"
],

function(_, Backbone, template) {
    var ScheduleView = Backbone.View.extend({
        el: "#schedule",
        template: _.template(template),

        render: function () {
            this.$el.html(this.template());

            this.$(".draggable").draggable({
                revert: "invalid"
            });

            this.$(".droppable-table-cell").droppable({
                hoverClass: "drop-over",

                drop: function(event, ui) {
                    var draggable = ui.draggable;
                    var content = draggable.contents().clone();

                    var scheduleElement = $("<span></span>");
                    scheduleElement.append(content);
                    scheduleElement.appendTo(this);

                    scheduleElement.draggable({
                        revert: "invalid",
                        helper: "clone",
                        zIndex: 2
                    });

                    $(draggable).remove();
                }
            });

            this.$(".droppable-list").droppable({
                hoverClass: "drop-over",

                drop: function(event, ui) {
                    var draggable = ui.draggable;
                    var content = draggable.contents().clone();

                    var scheduleElement = $("<span></span>");
                    scheduleElement.addClass("list-group-item");
                    scheduleElement.append(content);
                    scheduleElement.appendTo(this);

                    scheduleElement.draggable({
                        revert: "invalid"
                    });

                    $(draggable).remove();
                }
            });

            return this;
        }
    });

    return ScheduleView;
});