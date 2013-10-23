define([
    "underscore",
    "jquery",
    "jquery.ui",
    "views/schedule/draggable"
],

function(_, $, jqueryUi, Draggable) {

    var Droppable = function(elements, targetClass) {
        var OCCUPIED_CLASS = "droppable-item-occupied";
        var self = this;

        elements.droppable({
            hoverClass: "droppable-item-hover",
            tolerance: "pointer",

            accept: function(source) {
                return !self.isOccupied($(this)) && !$(source).hasClass("slot-template");
            },

            drop: function(event, ui) {
                var source = ui.draggable;

                var target = self.cloneSource(source);
                self.addTargetClass(target);
                self.makeTargetDraggable(target);
                self.removeSource(source);

                target.appendTo(this);
                self.markSourceUnoccupied(ui.helper.parent());
                self.markTargetOccupied($(this))
            }
        });

        this.cloneSource = function(source) {
            var content = source.contents().clone();
            return $("<span></span>").append(content);
        };

        this.removeSource = function(source) {
            $(source).remove();
        };

        this.addTargetClass = function(target) {
            if(!_.isUndefined(targetClass)) {
                target.addClass(targetClass);
            }
        };

        this.makeTargetDraggable = function(target) {
            new Draggable(target, true);
        };

        this.markTargetOccupied = function(target) {
            target.addClass(OCCUPIED_CLASS);
        };

        this.markSourceUnoccupied = function(target) {
            target.removeClass(OCCUPIED_CLASS);
        };

        this.isOccupied = function(target) {
            return target.hasClass(OCCUPIED_CLASS);
        };
    };

    return Droppable;
});