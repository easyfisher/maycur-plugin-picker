/**
  Maycur Image Viewer Plugin
  https://github.com/easyfisher/maycur-plugin-image-viewer

  Copyright (c) Easter Dong 2016
*/

var exec = require('cordova/exec');

var Picker = function() {

}

Picker.show = function() {
    exec(null,
      null,
      "Picker",
      "show",
      [urls, index]
    );
};

module.exports = Picker;