define([
    "views/schedule/droppable"
],

function(Droppable) {

    var MultiDroppable = function(elements, targetClass) {
        Droppable.call(this, elements, targetClass);

        this.markTargetOccupied = function(target) {
        };

        this.isOccupied = function(target) {
            return false;
        };
    }

    return MultiDroppable;
});