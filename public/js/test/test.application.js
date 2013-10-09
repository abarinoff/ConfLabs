var tests = [];

for (var file in window.__karma__.files) {
  if (window.__karma__.files.hasOwnProperty(file)) {
    if (/\.test\.js$/.test(file)) {
      tests.push(file);
    }
  }
}

requirejs.config({
    baseUrl: '/base/src',

    shim: {
        "underscore": {
            exports: "_"
        },

        "backbone": {
            deps: [
                "underscore",
                "jquery"
            ],
            exports: "Backbone"
        },

        "backbone.paginator": {
            deps: ["backbone"],
            exports: "Backbone.Paginator"
        },

        "bootstrap": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        }
    },

    paths: {
        "jquery": "../lib/jquery",
        "underscore": "../lib/underscore",
        "backbone": "../lib/backbone",
        "backbone.paginator": "../lib/backbone.paginator",
        "backbone.validation": "../lib/backbone.validation",
        "bootstrap": "../lib/bootstrap",
        "require.text": "../lib/require.text"
    },

    deps: tests,

    callback: window.__karma__.start
});