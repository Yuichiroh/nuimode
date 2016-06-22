var fs = require('fs');

io = function () {
    this.readFromFile = function (file) {
        return fs.readFileSync(file);
    };

    this.writeToFile = function (file, data) {
        fs.writeFileSync(file, data);
    };
};