define([
    "jquery",
    "jquery.ui"
],

function($, jqueryUi) {

    var Draggable = function(elements) {
        var DRAGGABLE_ITEM_CLASS = "draggable-item";
        var DRAGGABLE_ITEM_Z_INDEX = 10;

        elements.draggable({
            revert: "invalid",
            helper: "clone",
            zIndex: DRAGGABLE_ITEM_Z_INDEX,

            start: function (event, ui) {
                $(this).hide();
                ui.helper.addClass(DRAGGABLE_ITEM_CLASS);
            },

            stop: function () {
                $(this).show();
            }
        });
    };

    return Draggable;
});