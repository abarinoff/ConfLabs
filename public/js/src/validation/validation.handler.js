define([
    "jquery"
],

function($) {
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

    var ValidationHandler = function(attrPrefix) {
        return {
            valid: function(view, attr, selector){
                var fullAttr = attrPrefix + attr;
                var control = new FormControl(view, fullAttr, selector);
                control.unsetError();
            },

            invalid: function(view, attr, error, selector) {
                var fullAttr = attrPrefix + attr;
                var control = new FormControl(view, fullAttr, selector);
                control.setError(error);
            }
        }
    }

    return ValidationHandler;
});