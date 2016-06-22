/*
 this script is forked from:
 https://github.com/TooTallNate/node-applescript

 Copyright (c) 2010 Nathan Rajlich
 Copyright (c) 2016 Yuichiroh Matsubayashi

 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following
 conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
 */

var spawnSync = require("child_process").spawnSync;
var spawn = require("child_process").spawn;

exports.Parsers = require("./applescript-parser");
var parse = exports.Parsers.parse;

// Path to 'osascript'. By default search PATH.
exports.osascript = "osascript";


applescript = function () {

    // Execute a String as AppleScript.
    this.execStringSync = function (str) {
        return runApplescriptSync(str);
    };

    // Execute a String as AppleScript.
    this.execString = function (str, callback) {
        return runApplescript(str, callback);
    };


    function runApplescriptSync(strOrPath) {
        args = [];

        // args get added to the end of the args array
        args.push("-ss"); // To output machine-readable text.

        var interpreter = spawnSync(exports.osascript, args, {input: strOrPath});

        var result = interpreter.stdout;

        // If the exit code was something other than 0, we're gonna
        // return an Error object.
        return result;
    }


    function runApplescript(strOrPath, args, callback) {
        var isString = false;
        if (!Array.isArray(args)) {
            callback = args;
            args = [];
            isString = true;
        }

        // args get added to the end of the args array
        args.push("-ss"); // To output machine-readable text.
        if (!isString) {
            // The name of the file is the final arg if 'execFile' was called.
            args.push(strOrPath);
        }
        var interpreter = spawn(exports.osascript, args);

        bufferBody(interpreter.stdout);
        bufferBody(interpreter.stderr);

        interpreter.on('exit', function (code) {
            var result = parse(interpreter.stdout.body);
            var err;
            if (code) {
                // If the exit code was something other than 0, we're gonna
                // return an Error object.
                err = new Error(interpreter.stderr.body);
                err.appleScript = strOrPath;
                err.exitCode = code;
            }
            if (callback) {
                callback(err, result, interpreter.stderr.body);
            }
        });

        if (isString) {
            // Write the given applescript String to stdin if 'execString' was called.
            interpreter.stdin.write(strOrPath);
            interpreter.stdin.end();
        }
    }

    function bufferBody(stream) {
        stream.body = "";
        stream.setEncoding("utf8");
        stream.on("data", function (chunk) {
            stream.body += chunk;
        });
    }
};