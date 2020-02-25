var uuid = require('uuid');
/**
 *
 var url: String,


//{GET/POST/PUT}，请求服务器的方法，默认GET
var method: String = "GET",

//当method=POST的时候使用，默认 application/x-www-form-urlencoded
postContentType: String = "application/x-www-form-urlencoded",

当method=POST的时候使用，传入编码后的数组
var formData: String = "",

//让手机端的浏览器调用指定的代理服务器
 //1. 类型: HTTPS/SOCKS5
 //2. IP: 代理的IP地址
 //3. PORT: 代理的端口
 //4. PROXY_USERNAME: 代理的用户名，可为空
 //5. PROXY_PASSWORD: 代理的密码，可为空
 var proxy: String = "",

 //可同时传入多个headers，用于模拟其它设备或者 其它环境
 var sendHeaders: String = "",

  //用于控制返回结果的时候会同时返回网页请求 的头, 类似如下结果
var getHeaders: Boolean = true,

//即返回当前网页的URL。 因为某些网页会进行 javascript跳转到新的URL，网页最终停留的网址和请求的网址并不一样。默认总是返回 当前的URL
var getUrl: Boolean = true,

//即每次请求时浏览器会清空所有的本地 Cookie
var clearCookie: Boolean = true,

//传入base64编码后的javascript代码用于执行当 前网页操作
var javascriptCode: String = "",

//是否需要返回当前网页截图
var screenshot: Boolean = false,

//正则表达式， 如果被访问的页面进行某种形式的 跳转(javascript重定向，meta refresh等)，如果跳转的网页符合block_url_pattern ，则直接返回网址
var blockUrlPattern: String = "",

//正则表达式列表， 如果被访问的页面 有多个XHR请求，并且XHR请求的网址符合​block_xhr_request_pattern，则返回并且 拦截XHR的请求数据
var blockXhrRequestPattern: String = ""
 */
module.exports = class SocketHandler {

    constructor(req, res, next) {
        this.req = req;
        this.res = res;
        this.next = next;
        this.requestParams = this.req.body;
        console.log(this.requestParams);
        this.devicesID = this.requestParams.devicesID;

        /**
         * 需要爬取的url链接
         */
        this.url = this.requestParams.url;
        /**
         *如果不传请求参数，默认为 get请求
         */
        if (this.requestParams.method) {
            this.method = this.requestParams.method;
        } else {
            this.method = "GET";
        }

        /**
         * 如果是POST请求， 但是不传postContentType
         * 默认为application/x-www-form-urlencoded
         */
        if (this.requestParams.postContentType) {
            this.postContentType = this.requestParams.postContentType;
        } else {
            this.postContentType = "application/x-www-form-urlencoded";
        }
        /**
         * 如果请求是POST，
         * 可以传人body
         * 格式为“name=hc&age=12” ,key=value形式不同参数之间用&链接
         */
        this.formData = this.requestParams.formData ? this.requestParams.formData : "";
        /**
         * 代理参数
         * 不传 默认不打开代理
         * 格式为json
         * {
         *  type: HTTPS/SOCKS5
            address: 代理的IP地址
            port: 代理的端口
            username: 代理的用户名，可为空
            password: 代理的密码，可为空
         * }
         */
        this.proxy = this.requestParams.proxy ? this.requestParams.proxy : "";
        /**
         * 请求头
         * 格式为 key=value形式，多个参数以&链接
         * Content-Type=application/x-www-form-urlencoded&User-Agent=mobile&RequestId=21294324923
         */
        this.sendHeaders = this.requestParams.sendHeaders ? this.requestParams.sendHeaders : "";
        /**
         * 默认ture，返回 响应头
         */
        this.getHeaders = this.requestParams.getHeaders ? this.requestParams.getHeaders : true;
        /**
         * 默认 ture，即返回当前网页的URL。 因为某些网页会进行 javascript跳转到新的URL，网页最终停留的网址和请求的网址并不一样。默认总是返回 当前的URL
         */
        this.getUrl = this.requestParams.getUrl ? this.requestParams.getUrl : true;
        /**
         * 默认每次请求清除cookies，默认ture
         */
        this.clearCookie = this.requestParams.clearCookie ? this.requestParams.clearCookie : true;
        /**
         * js注入脚本。可传可不传
         */
        this.javascriptCode = this.requestParams.javascriptCode ? this.requestParams.javascriptCode : "";
        /**
         * 是否需要返回当前页面截屏，默认false
         */
        this.screenshot = this.requestParams.screenshot ? this.requestParams.screenshot : false;
        /**
         *正则表达式， 如果被访问的页面进行某种形式的 跳转(javascript重定向，meta refresh等)，如果跳转的网页符合block_url_pattern ，则直接返回网址
         */
        this.blockUrlPattern = this.requestParams.blockUrlPattern ? this.requestParams.blockUrlPattern : "";
        /**
         * 正则表达式列表， 如果被访问的页面 有多个XHR请求， 并且XHR请求的网址符合​ block_xhr_request_pattern， 则返回并且 拦截XHR的请求数据
         */
        this.blockXhrRequestPattern = this.requestParams.blockXhrRequestPattern ? this.requestParams.blockXhrRequestPattern : "";
        /**
         * 随机唯一请求id，用于设置本次请求的socket监听
         */
        this.uuid4socketEvent = uuid.v4();
        /**
         * page等待时间，时间到再返回数据
         */
        this.pageWait = this.requestParams.pageWait ? this.requestParams.pageWait : 0;
    };
    /**
     * 移除所有socket里的监听器
     */
    removeAllLitenerByUUID = function (uuid4socketEvent) {
        console.log("this.uuid4socketEvent" + uuid4socketEvent);
    };
    handler() {
        try {
            /**
             * 如果设备没有连接，直接return
             */
            var responseDats = [];
            let requestDatas = JSON.stringify({
                url: this.url,
                method: this.method,
                postContentType: this.postContentType,
                formData: this.formData,
                proxy: this.proxy,
                sendHeaders: this.sendHeaders,
                getHeaders: this.getHeaders,
                getUrl: this.getUrl,
                clearCookie: this.clearCookie,
                javascriptCode: this.javascriptCode,
                screenshot: this.screenshot,
                blockUrlPattern: this.blockUrlPattern,
                blockXhrRequestPattern: this.blockXhrRequestPattern,
                pageWait: this.pageWait,
                uuid4socketEvent: this.uuid4socketEvent
            });
            console.log(requestDatas);

            if (global.sockets.size == 0) {
                this.res.json({
                    code: 40000,
                    msg: "node device connected",
                    data: ""
                })

                return
            }

            if (this.devicesID) {
                if (global.sockets
                    .get(this.devicesID) == null) {
                    this.res.json({
                        code: 40001,
                        msg: 'devices did not connected,please connect the devices and try again',
                        data: ''
                    });
                    global.resLogger.info({
                        headers: this.res._header,
                        code: 40001,
                        msg: 'devices did not connected,please connect the devices and try again',
                        data: ''
                    });
                    return;
                };

                let client = global.sockets
                    .get(this.devicesID)
                    .emit('hook_params', requestDatas)
                    .once(this.uuid4socketEvent, (datas) => {
                        responseDats.push(JSON.parse(datas));
                        responseDats.push(JSON.parse(datas));

                        clearTimeout(timer);
                        let encode = new TextEncoder('utf-8');
                        console.log(datas);
                        this.res.status(200).json({
                            msg: "success",
                            code: 200,
                            data: responseDats
                        });
                        global.resLogger.info({
                            headers: this.res._header,
                            data: datas
                        });
                    });

                /**
                 * 如果30s还没有回馈 直接返回timeout给请求客户端
                 */
                let timer = setTimeout(() => {
                    global.resLogger.info({
                        headers: this.res._header,
                        code: 408,
                        msg: 'Request TimeOut',
                        data: ''
                    });

                    this.res.status(200).json({
                        code: 408,
                        msg: 'Request TimeOut',
                        data: ''
                    });

                    client.removeAllListeners(this.uuid4socketEvent, () => {
                        console.log('remove success')
                    });
                    console.log('time delay 3000ms');
                }, 50000);
            } else {
                // for (i; i < global.userInfo.length; i++) {
                global.sockets.forEach(((client) => {
                    client.emit('hook_params', requestDatas)
                        .once(this.uuid4socketEvent, (datas) => {
                            responseDats.push(JSON.parse(datas));
                            console.log("current length =" + responseDats.length + "global size :" + global.sockets.size);
                            if (responseDats.length == global.sockets.size) {
                                clearTimeout(timer);
                                console.log(datas);
                                this.res.status(200).json({
                                    msg: "success",
                                    code: 200,
                                    data: responseDats
                                });
                                global.resLogger.info({
                                    headers: this.res._header,
                                    data: datas
                                });
                            }
                        });
                }));
                // global.sockets[i]
                // }

                /**
                 * 如果30s还没有回馈 直接返回timeout给请求客户端
                 */
                let timer = setTimeout(() => {

                    if (responseDats.length != global.sockets.length && responseDats.length > 0) {
                        this.res.status(200).json({
                            msg: "success",
                            code: 200,
                            data: responseDats
                        });
                        return
                    } else {
                        this.res.status(200).json({
                            code: 408,
                            msg: 'Request TimeOut',
                            data: ''
                        });
                    }
                    global.resLogger.info({
                        headers: this.res._header,
                        code: 408,
                        msg: 'Request TimeOut',
                        data: responseDats
                    });
                    this.removeAllLitenerByUUID(this.uuid4socketEvent);
                    global.sockets.forEach((client) => {
                        client.removeAllListeners(this.uuid4socketEvent, () => {
                            console.log('remove success')
                        });
                    });
                    console.log('time delay 3000ms');
                }, 50000);
            }

        } catch (error) {
            global.errLogger.error(error);
            console.log(error);
            this.res.json({
                code: 500,
                msg: "server internal error",
                data: ''
            })
        }
    }

}