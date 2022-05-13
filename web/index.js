#!/usr/bin/env node
// 1.call npm inatall first
// 2.call `node ./index.js` 
// or `npm run serve`
// default part:1024
const express = require('express');
const app = express();
const request = require("request");
const fs = require('fs');
const url = require('whatwg-url');
const { ClientRequest } = require('http');
//const { request, request } = require('http');
const user_agent = ("Mozilla/5.0 (Linux; Android 11; PECM30)" +
    " AppleWebKit/537.36 (KHTML, like Gecko) " +
    "Chrome/99.0.4844.73 Mobile Safari/537.36");
const API = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";
let g_d = false;
function download(file, target, handle) {
    let total = 0;
    let got = 0;
    let buf = fs.createWriteStream(target, {
        'encoding': 'binary',
        'mode': 0777,
        autoClose: true
    })
    let req = request(file, {
        method: 'GET',
    })
    req.pipe(buf);
    req.on('response', (data) => {
        total = parseInt(data.headers['content-length'])
    })
    req.on('data', (buf0) => {
        g_d = true;
        got += buf0.length;
        handle(String(got * 100 / total) + '%');
    });
    const end = ((x) => {
        g_d = false;
        buf.close((ex) => {
            console.log('buf.close', ex);
        })
        console.log('===done', x)
    });
    req.on('complete', end);
    req.on('end', end);
}
(function () {
    app.use((req, res, next) => {
        req.body = null;
        let bodystr = '';
        let goon = true;
        res.buf = [];
        req.on('data', (data) => {
            console.log('on pro '+data)
            for (const k in data) {
                if (k.startsWith('utf8')) {
                    bodystr += data[k](data);
                    break;
                }
            }
        })
        req.on("end", () => {
            req.body = bodystr;
        });
        req.on("error", (e) => {
            console.log('req one', e);
            goon = false;
            res.sendStatus(500);
            res.end('500 bad-gateway' + e)
        })
        //req.headers['host'] = host;
        req.headers['referer'] = undefined;
        req.headers['user-agent'] = 'Mozilla/5.0 (Linux; Android 11; PECM30) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.73 Mobile Safari/537.36';
        res.append('Access-Control-Allow-Origin', ['*']);
        res.append('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
        res.append('Access-Control-Allow-Headers', '*');
        console.log(req.method + ' ' + req.url);
        //console.log(req.header('referer'))
        if (goon) {
            try{
                next();
            }catch(e){
                console.log(e);
            }
        }
    })

    app.get('/', (_, res) => {
        res.set('Content-Type', 'text/plain;charset=utf-8')
        res.send('hello world 你好世界')
    })
    app.get('/api', function (req, res) {
        res.set('Content-Type', 'application/json;charset=utf-8');
        let id = req.query['id'];
        if (id === undefined) {
            id = '';
        }
        req.on('end', () => {
            request(API + id, {
                method: 'GET',
                headers: {
                    "User-Agent": user_agent,
                    "Accept": "*",
                    "HOST": "www.iesdouyin.com"
                }
            }, (err, resp, body) => {
                if (err !== null || resp === null || resp === undefined) {
                    res.send("{\"<err>\":true}")
                    return;
                }
                res.send(body);
            })
        })
    });
    app.post('/api', (
        req, res
    ) => {
        res.set('Content-Type', 'application/json;charset=utf-8')
        //res.send('{}')
        let rtn = { code: -1, msg: "server error" }
        req.on('end', () => {
            let body = String(req.body);
            if (body.startsWith('http://') || body.startsWith('https://')) {
                rtn.code = 0;
                rtn.msg = "成功";
                rtn.data = { url: body };
                request(body, {
                    headers: {
                        'User-Agent': user_agent
                    }
                }, (err, resp, body) => {
                    if (err !== null || resp === null || resp === undefined) {
                        let send = JSON.stringify({
                            code: 500,
                            msg: String(err),
                            data: {}
                        })
                        res.send(send);
                        return;
                    }
                    if (resp.statusCode == 200) {
                        rtn.data.path = resp.request.uri.pathname.substring("/share/video".length)
                        let send = JSON.stringify(rtn)
                        res.send(send);
                    } else if (resp.statusCode == 302) {
                        rtn.data.location = String(resp.headers.location);
                        let send = JSON.stringify(rtn)
                        res.send(send);
                    } else {
                        let send = JSON.stringify({
                            code: resp.statusCode,
                            data: '' + resp.statusCode,
                            msg: String(resp.statusMessage)
                        });
                        res.send(send);
                    }
                })
            } else {
                rtn.code = 1;
                rtn.msg = "not a valid url."
            }
            //res.set('Content-Type','application/json;charset=utf-8')
        })
    })
    app.get('/video', function (req, res) {
        let test = true;
        if (test) {
            let url0 = req.query['url'];
            if (url0 === undefined|| url0 === null){
                res.status(404).write('Not found')
                res.end()
                return;
            }
            let parsed = url.parseURL(url0);
            if(parsed === null||parsed === undefined){
                res.status(404).write('Not found')
                res.end()
                return;
            }
            let download = request(url0,{
                'headers':{
                    'User-Agent':user_agent,
                    'Host':parsed.host,
                },
                'method':'GET',
            });
            download.on('request',()=>{
                //let type = req.getHeader('Content-Type');
                res.status(200);
            });
            download.on('response',(resp)=>{
                let length = resp.headers['content-length']
                let type = resp.headers['content-type']
                if (type == undefined){
                    type = 'video/mp4'
                }
                res.setHeader('Content-Type',type)
                if (length != undefined){
                    res.setHeader('Content-Length',length)
                }
                resp.on('data',(data)=>{
                    res.write(data);
                })
                resp.on('end',()=>{
                    res.end();
                })
            })
        }
    })
    let server = app.listen(1024,'', () => {
        let host = server.address().address
        let port = server.address().port
        console.log("应用实例，访问地址为 http://%s:%s", host, port)
    });
})();
