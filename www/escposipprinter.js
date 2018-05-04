var exec = require('cordova/exec');

exports.printAndroid = function (success, error, dataPrint, ipAddress, isShowLogo) {
    exec(success, error, "escposipprinter", "printAndroid", [{
        "dataPrint": dataPrint,
        "ipAddress": ipAddress,
        "isShowLogo": isShowLogo
    }]);
};