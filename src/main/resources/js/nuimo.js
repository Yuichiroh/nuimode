// /*
//  this script is forked from:
//  https://github.com/brendonparker/nuimo-node-demo
//
//  Copyright (c) 2016 Brendon
//  Copyright (c) 2016 Yuichiroh Matsubayashi
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.
//  */
//
// var net = require('net');
// var noble = require('noble');
// var fs = require('fs');
// // var server = require("./server");
//
// var Nuimo = function () {
//
//     var ledMatrix = {};
//     var battery = {};
//     var connect;
//     // this.ps = [];
//
//     var server = net.createServer(function (conn) {
//         console.log('server-> tcp server created');
//         conn.write("connected");
//         connect = conn;
//         console.log("conn", conn);
//         console.log("connect", connect);
//
//         conn.on('data', function (data) {
//             console.log('server-> ' + data + ' from ' + conn.remoteAddress + ':' + conn.remotePort);
//             conn.write('server -> Repeating: ' + data);
//             fields = data.toString().split(":");
//             n.writeToLEDs(fields[0], fields[1], fields[2], fields[3])
//         });
//
//         conn.on('close', function () {
//             console.log('server-> client closed connection');
//             n.disconnect();
//         });
//     }).listen(3000);
//
//     this.start = function (connection) {
//         var config = require("./nuimo_config.json");
//         var uuids = config.map((c, i, arr) => c.uuid);
//         connect = connection;
//         this.init(uuids)
//     };
//
//     this.setConn = function (conn) {
//         connect = conn;
//     };
//
//     function sendToNuimode(string) {
//         console.log("sending", this.connect);
//         if (this.connect) {
//             console.log(string);
//             this.connect.write(string);
//         }
//     }
//
//     var SERVICES = {
//         // BATTERY_STATUS: '0000180f00001000800000805f9b34fb',
//         BATTERY_STATUS: '180F',
//         // DEVICE_INFORMATION: '0000180a00001000800000805f9b34fb',
//         DEVICE_INFORMATION: '180A',
//         LED_MATRIX: 'f29b1523cb1940f3be5c7241ecb82fd1',
//         USER_INPUT: 'f29b1525cb1940f3be5c7241ecb82fd2'
//     };
//
//     var CHARACTERISTICS = {
//         // BATTERY: '00002a1900001000800000805f9b34fb',
//         BATTERY: '2a19',
//         // DEVICE_INFO: '00002a2900001000800000805f9b34fb',
//         DEVICE_INFO: '2a29',
//         LED_MATRIX: 'f29b1524cb1940f3be5c7241ecb82fd1',
//         ROTATION: 'f29b1528cb1940f3be5c7241ecb82fd2',
//         BUTTON_CLICK: 'f29b1529cb1940f3be5c7241ecb82fd2',
//         SWIPE: 'f29b1527cb1940f3be5c7241ecb82fd2',
//         FLY: 'f29b1526cb1940f3be5c7241ecb82fd2'
//     };
//
//     const EVENTS = {
//         CONNECTED: 'Connected',
//         DISCONNECTED: 'Disconnected'
//     };
//
//     this.disconnect = function () {
//         this.ps.forEach((p,i,arr) => p.disconnect());
//         noble.startScanning(['180F', '180A'], false);
//     };
//
//     this.writeToLEDs = function (uuid, data, brightness, duration) {
//         if (ledMatrix[uuid]) {
//             ledMatrix[uuid].write(createDataForLedMatrix(data, brightness, duration));
//         } else {
//             console.log("Can't writeToLEDs yet.")
//         }
//     };
//
//     this.batteryStatus = function (uuid) {
//         if (battery[uuid]) {
//             battery[uuid].read((error, data) =>
//                sendToNuimode(JSON.stringify([uuid, data[0]]))
//             );
//         }
//         else
//             sendToNuimode(JSON.stringify([uuid, 0]));
//     };
//
//     function createDataForLedMatrix(data, brightness, duration) {
//         if (arguments.length != 3) {
//             throw 'createDataForLedMatrix requires three arguments';
//         }
//
//         var strData = '';
//         if (data instanceof Array) {
//             strData = data.join('');
//         } else {
//             strData = data;
//         }
//         var tempArr = strData.split('').filter(x => x === '1' || x === '0');
//
//         if (strData.length != 81)
//             throw 'data must be 81 bits';
//         if (brightness < 0 || brightness > 255)
//             throw 'brightness must be between 0 and 255';
//         if (duration < 0 || duration > 255)
//             throw 'duration must be between 0 and 255';
//
//         var output = [];
//
//         while (tempArr.length > 0) {
//             var temp = parseInt(tempArr.splice(0, 8).reverse().join(''), 2);
//             output.push(temp);
//         }
//
//         output.push(brightness);
//         output.push(duration);
//
//         return new Buffer(output);
//     }
//
//     this.init = function (uuids) {
//         noble.on('stateChange', state => {
//             if (state === 'poweredOn') {
//                 return noble.startScanning(['180F', '180A'], false);
//             }
//             else
//                 return noble.stopScanning();
//         });
//
//         console.log("init", uuids);
//
//         noble.on('discover', p => {
//             console.log('Found device with local name: ' + p.advertisement.localName);
//             console.log('├ uuid: ' + p.uuid);
//             console.log('└ advertising the following service uuid\'s: ' + p.advertisement.serviceUuids);
//             if (uuids.indexOf(p.uuid) > -1) {
//                 p.connect(err => {
//                     if (err) return;
//                     this.ps.push(p);
//                     p.discoverServices([SERVICES.DEVICE_INFORMATION, SERVICES.BATTERY_STATUS, SERVICES.LED_MATRIX, SERVICES.USER_INPUT], (err, services) => {
//                         for (var service of services) {
//                             var nuimoChars = Object.keys(CHARACTERISTICS).map(prop => CHARACTERISTICS[prop]);
//                             service.discoverCharacteristics(nuimoChars, (err, characteristics) => {
//                                 characteristics.forEach(c => {
//                                     if (c.uuid === CHARACTERISTICS.LED_MATRIX) {
//                                         ledMatrix[p.uuid] = c;
//                                     }
//                                     else if (c.uuid === CHARACTERISTICS.BATTERY) {
//                                         battery[p.uuid] = c;
//                                         this.batteryStatus(p.uuid);
//                                     }
//                                     if (c.properties.indexOf('notify') > -1) {
//                                         c.on('read', (data, isNotification) => {
//                                             // yuima.nuimo.NuimoManager().handler(p.uuid, c.uuid, data);
//                                             sendToNuimode(JSON.stringify([p.uuid, c.uuid, data]));
//                                         });
//                                         c.notify(true);
//                                     } else if (c.properties.indexOf('write') > -1) {
//                                         // sendToNuimode(JSON.stringify([p.uuid, c.uuid]));
//                                         // yuima.nuimo.NuimoManager().handler(p.uuid, c.uuid);
//                                     }
//                                 });
//                             });
//                         }
//                         // yuima.nuimo.NuimoManager().handler(p.uuid, EVENTS.CONNECTED);
//                         sendToNuimode(JSON.stringify([p.uuid, EVENTS.CONNECTED]));
//                     });
//                 });
//             }
//             p.once('disconnect', () => {
//                 // yuima.nuimo.NuimoManager().handler(p.uuid, EVENTS.DISCONNECTED);
//                 sendToNuimode(JSON.stringify([p.uuid, EVENTS.DISCONNECTED]));
//                 noble.startScanning(['180F', '180A'], false);
//             });
//         });
//     };
// };
//
// var nuimo = new Nuimo();
// nuimo.start("");