requirejs.config({
    shim: {
        'underscore': {
            exports: '_'
        },
        'backbone': {
            deps: [
                'underscore',
                'jquery'
            ],
            exports: 'Backbone'
        },
        'bootstrap': {
            deps: ['jquery'],
            exports: "$.fn.popover"
        }
    },
    paths: {
        'jquery': 'lib/jquery',
        'underscore': 'lib/underscore',
        'backbone': 'lib/backbone',
        'bootstrap': 'lib/bootstrap',
        'require.text': 'lib/require.text'
    },
    enforceDefine: true
});

define(['backbone', 'routers/router'], function(Backbone, Router) {
    new Router();
    Backbone.history.start();
});

