define([
    'underscore',
    "views/schedule/slot/slot",
    'require.text!templates/org.slot.html'
],

function(_, SlotView, template) {

    var OrgSlotView = SlotView.extend({
        TYPE: "org",

        customTemplate: _.template(template),

        getType: function() {
            return this.TYPE;
        },

        renderCustomTemplate: function(stages, data) {
            return $(this.customTemplate({stages: stages, data: data}));
        }
    });

    return OrgSlotView;
});
