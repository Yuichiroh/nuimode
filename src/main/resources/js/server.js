/*
 this script is forked from:
 https://github.com/brendonparker/nuimo-node-demo

 Copyright (c) 2016 Brendon
 Copyright (c) 2016 Yuichiroh Matsubayashi

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

var net = require('net');
var noble = require('noble');
var fs = require('fs');
var port = 3000;

class Nuimo {
    constructor(port) {
        this.ledMatrix = {};
        this.battery = {};
        this.connect = null;
        this.ps = [];

        this.SERVICES = {
            // BATTERY_STATUS: '0000180f00001000800000805f9b34fb',
            BATTERY_STATUS: '180F',
            // DEVICE_INFORMATION: '0000180a00001000800000805f9b34fb',
            DEVICE_INFORMATION: '180A',
            LED_MATRIX: 'f29b1523cb1940f3be5c7241ecb82fd1',
            USER_INPUT: 'f29b1525cb1940f3be5c7241ecb82fd2'
        };

        this.CHARACTERISTICS = {
            // BATTERY: '00002a1900001000800000805f9b34fb',
            BATTERY: '2a19',
            // DEVICE_INFO: '00002a2900001000800000805f9b34fb',
            DEVICE_INFO: '2a29',
            LED_MATRIX: 'f29b1524cb1940f3be5c7241ecb82fd1',
            ROTATION: 'f29b1528cb1940f3be5c7241ecb82fd2',
            BUTTON_CLICK: 'f29b1529cb1940f3be5c7241ecb82fd2',
            SWIPE: 'f29b1527cb1940f3be5c7241ecb82fd2',
            FLY: 'f29b1526cb1940f3be5c7241ecb82fd2'
        };

        this.EVENTS = {
            CONNECTED: 'Connected',
            DISCONNECTED: 'Disconnected'
        };

        this.server = net.createServer(conn => this.server_hander(conn));

        this.server.listen(port);
    }

    start() {
        let config = require("./nuimo_config.json");
        let uuids = config.map((c, i, arr) => c.uuid);
        this.init(uuids)
    }

    writeToLEDs(uuid, data, brightness, duration) {
        if (this.ledMatrix[uuid]) {
            this.ledMatrix[uuid].write(this.createDataForLedMatrix(data, brightness, duration));
        } else {
            console.log("Can't writeToLEDs yet.")
        }
    }

    server_hander(conn) {
        console.log('server-> tcp server created');
        // conn.write("connected");
        this.connect = conn;
        let that = this;
        this.ps.forEach((p, i, arr) =>
            that.sendToNuimode(p.uuid, that.EVENTS.CONNECTED)
        );
        conn.on('data', (data) => {
            console.log('server-> ' + data + ' from ' + conn.remoteAddress + ':' + conn.remotePort);
            // conn.write('server -> Repeating: ' + data);
            let fields = data.toString().split(":");
            if (fields[1] === "led") {
                that.writeToLEDs(fields[0], fields[2], parseInt(fields[3]), parseInt(fields[4]))
            }
            else {
                that.batteryStatus(fields[0]);
            }
        });

        conn.on('close', () => {
            console.log('server-> client closed connection');
            noble.startScanning(['180F', '180A'], false);
        });
    }

    disconnect() {
        this.ps.forEach((p, i, arr) =>
            p.disconnect()
        );
        this.ps = [];
        this.connect = null;
        noble.startScanning(['180F', '180A'], false);
    }

    sendToNuimode(pid, sid, data) {
        if (this.connect) {
            let msg;
            if (data) {
                let info = JSON.parse(JSON.stringify(data));
                if (info instanceof Object) {
                    info = info["data"];
                }
                else info = [info];
                msg = JSON.stringify({"pId": pid, "sId": sid, "data": info});
            }
            else {
                msg = JSON.stringify({"pId": pid, "sId": sid, "data": []});
            }
            // console.log(msg);
            this.connect.write(msg + ";");
        }
    }

    batteryStatus(uuid) {
        let that = this;
        if (this.battery[uuid]) {
            this.battery[uuid].read((error, data) =>
                    data[0]
                // that.sendToNuimode(uuid, that.CHARACTERISTICS.BATTERY, data[0])
            );
        }
        else
            that.sendToNuimode(uuid, that.CHARACTERISTICS.BATTERY, 0);
    }

    createDataForLedMatrix(data, brightness, duration) {
        if (arguments.length != 3) {
            throw 'createDataForLedMatrix requires three arguments';
        }

        let strData = '';
        if (data instanceof Array) {
            strData = data.join('');
        } else {
            strData = data;
        }
        let tempArr = strData.split('').filter(x => x === '1' || x === '0');

        if (strData.length != 81)
            throw 'data must be 81 bits';
        if (brightness < 0 || brightness > 255)
            throw 'brightness must be between 0 and 255';
        if (duration < 0 || duration > 255)
            throw 'duration must be between 0 and 255';

        let output = [];

        while (tempArr.length > 0) {
            let temp = parseInt(tempArr.splice(0, 8).reverse().join(''), 2);
            output.push(temp);
        }

        output.push(brightness);
        output.push(duration);

        return new Buffer(output);
    }

    init(uuids) {
        let that = this;
        noble.on('stateChange', state => {
            if (state === 'poweredOn')
                return noble.startScanning(['180F', '180A'], false);
            else return noble.stopScanning();
        });

        console.log("init", uuids);

        noble.on('discover', p => {
            console.log('Found device with local name: ' + p.advertisement.localName);
            console.log('├ uuid: ' + p.uuid);
            console.log('└ advertising the following service uuid\'s: ' + p.advertisement.serviceUuids);
            if (uuids.indexOf(p.uuid) > -1) {
                p.connect(err => {
                    if (err) return;
                    console.log("connected:", p.uuid);
                    p.discoverServices([that.SERVICES.DEVICE_INFORMATION, that.SERVICES.BATTERY_STATUS, that.SERVICES.LED_MATRIX, that.SERVICES.USER_INPUT], (err, services) => {
                        for (let service of services) {
                            let nuimoChars = Object.keys(that.CHARACTERISTICS).map(prop => that.CHARACTERISTICS[prop]);
                            service.discoverCharacteristics(nuimoChars, (err, characteristics) => {
                                characteristics.forEach(c => {
                                    if (c.uuid === that.CHARACTERISTICS.LED_MATRIX) {
                                        that.ledMatrix[p.uuid] = c;
                                    }
                                    else if (c.uuid === that.CHARACTERISTICS.BATTERY) {
                                        that.battery[p.uuid] = c;
                                        that.batteryStatus(p.uuid);
                                    }
                                    if (c.properties.indexOf('notify') > -1) {
                                        c.on('read', (data, isNotification) => {
                                            that.sendToNuimode(p.uuid, c.uuid, data);
                                        });
                                        c.notify(true);
                                    } else if (c.properties.indexOf('write') > -1) {
                                        that.sendToNuimode(p.uuid, c.uuid);
                                    }
                                });
                            });
                        }
                    });
                    that.sendToNuimode(p.uuid, that.EVENTS.CONNECTED);
                    that.ps.push(p);
                });
            }
            let puuid = p.uuid;
            p.once('disconnect', () => {
                that.sendToNuimode(puuid, that.EVENTS.DISCONNECTED);
                that.ps.filter((elm, i) => elm !== p);
                noble.startScanning(['180F', '180A'], false);
            });
        });
    }
}

var nuimo = new Nuimo(port);
nuimo.start();
