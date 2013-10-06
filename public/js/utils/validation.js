define([
    "jquery",
    "underscore",
    "validation"
],

function($, _, Validation) {
    var FormControl =  function(view, attr, selector) {
        var selectorValue = attr.replace(".", "-");
        var control = view.$('[' + selector + '="' + selectorValue + '"]');
        var group = control.parents(".form-group");
        var help = group.find(".help-block");

        this.setError = function(error) {
            group.addClass("has-error");

            help.text(error);
            help.removeClass("hidden");
        }

        this.unsetError = function() {
            group.removeClass("has-error");

            help.text("");
            help.addClass("hidden");
        }
    }

    Validation.configure({
        selector: 'id'
    });

    _.extend(Validation.callbacks, {
        valid: function(view, attr, selector){
            var control = new FormControl(view, attr, selector);
            control.unsetError();
        },

        invalid: function(view, attr, error, selector) {
            var control = new FormControl(view, attr, selector);
            control.setError(error);
        }
    });
});