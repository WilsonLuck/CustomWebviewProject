| 错误码  | 错误解释  |
| ------------ | ------------ |
|  408 |  接口超时|
|  500|  服务器内部错误|
|  40000|  无设备连接|
|  40001 | 缺失参数  |

# 写在前面
[Java正则与PhP区别，blockUrl/Xhr正则 请按照模版修改后再传入](https://www.cnblogs.com/Renyi-Fan/p/9074705.html)
```
 php url拦截:
 拦截Regex: /daih.php\?(.*)/
 java url拦截:
 拦截Regex: daih.php\\?(.*)
 
 php xhr拦截:
 /index.php\?aeroxada(.*)/
 java xhr拦截:
 index.php\\?aeroxada(.*)
```
# 获取设备列表

**请求URL：** 
- ` http://ip:3000/devices `
  
**请求方式：**
- GET 

**请求参数参数：** 

 **返回示例**

``` 
  {
    "code": 200,
	"msg":"success"
	"data":[
		{
  		"deviceBrand":"OnePlus",
  	 	"devicesID":"ucHuyQ6fAQ7je59UAAAA",
  		"ipAddress":"192.168.0.108",
 		"systemModel":"ONEPLUS A6010"
		},
		{
  		"deviceBrand":"RedMi",
  	 	"devicesID":"ucHuyQ6fAQ7je59UAAAA",
  		"ipAddress":"192.168.0.109",
 		"systemModel":"RedMi X1"
		},
		]
  }
```

 **返回参数说明** 

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|devicesID |string   |设备ID，用于向具体设备发送指令  |
|ipAddress |string   |设备IP  |
|systemModel |string   |设备型号  |
|deviceBrand |string   |设备品牌  |

# 发送指令
**请求URL：** 
- ` http://ip:3000/webHook `
  
**请求方式：**
- POST 

**请求参数参数** 

|参数名|类型|是否必须|说明|
|:-----  |:-----|:-----|:----------                           |
|devicesID |string   |flase|设备ID，用于向具体设备发送指令，如果不填，默认向所有设备发送指令  |
|url |string   |true|目标URL，“http://127.0.0.1:3000/getTest/”  |
|method |string   |flase|请求参数，GET、POST  |
|postContentType |string   |flase|默认为：application/x-www-form-urlencoded  |
|formData |string   |如果为POST请求，则此参数为ture| 如果请求是POST，需要传入请求参数</br>格式为:</br>“name=hc&age=12” ,key=value形式，不同参数之间用&链接  |
|proxy |string   |flase| 代理参数不传 默认不打开代理格式为json</br>{</br>type: "HTTPS" //HTTPS/SOCKS</br>	address: "192.168.1.1"//代理的IP地址</br>	port: 8000//代理的端口</br>	username:"account"//代理的用户名，可为空</br>	password:"123456"// 代理的密码，可为空</br>}|
|sendHeaders |string   |flase|请求头</br>格式为 key=value形式，多个参数以&链接</br>"Content-Type=application/x-www-form-urlencoded&User-Agent=mobile&RequestId=21294324923"  |
|getHeaders |bool   |flase|默认ture，需要返回响应头  |
|getUrl |bool   |flase|默认ture，需要返回当前加载的URL  |
|clearCookie |bool   |flase|默认ture，每次请求前都需要清除cookie  |
|javascriptCode |string   |flase|需要执行的js注入脚本  |
|screenshot |bool   |flase|默认false，是否需要返回当前页面截屏base64编码后的字符串，  |
|blockUrlPattern |string   |flase|正则表达式， 如果被访问的页面进行某种形式的 跳转(javascript重定向，meta refresh等)，如果跳转的网页符合block_url_pattern ，则直接返回网址|
|javascriptCode |string   |flase|正则表达式列表， 如果被访问的页面 有多个XHR请求， 并且XHR请求的网址符合​ block_xhr_request_pattern， 则返回并且 拦截XHR的请求数据  |
|pageWait |int   |flase|页面等待时间，等待后才会返还数据，毫秒表示，例如2s后再返回数据则填 2000  |
 **返回示例**

```
 {
    "msg": "success",
    "code": 200,
    "data": [
        {
            "html": "<html lang=\"en\"><head>\n  <script src=\"https://unpkg.com/ajax-hook/dist/ajaxhook.min.js\"></script> \n  <script language=\"JavaScript\">\n    // This only works if `open` and `send` are called in a synchronous way\n    // That is, after calling `open`, there must be no other call to `open` or\n    // `send` from another place of the code until the matching `send` is called.\n    requestID = null;\n    XMLHttpRequest.prototype.reallyOpen = XMLHttpRequest.prototype.open;\n    XMLHttpRequest.prototype.reallySend = XMLHttpRequest.prototype.send;\n    XMLHttpRequest.prototype.send = function (body) {\n        HTMLOUT.xhrSend(requestID, body);\n        this.reallySend(body);\n    };\n    XMLHttpRequest.prototype.open = function (method, url, async, user, password) {\n        requestID = generateRandom()\n        HTMLOUT.xhrOpen(method, url, requestID);\n        this.reallyOpen(method, url, async, user, password);\n    };\n\n\n\n    function generateRandom() {\n        return Math.floor((1 + Math.random()) * 0x10000)\n            .toString(16)\n            .substring(1);\n    }\n<!--    hookAjax({-->\n<!--        onreadystatechange: function (xhr) {-->\n<!--            console.log(\"onreadystatechange called: %O\", xhr);-->\n<!--            HTMLOUT.onreadystatechange(`onreadystatechange: ${JSON.stringify(xhr)}`);-->\n<!--        },-->\n<!--        onload: function (xhr) {-->\n<!--            HTMLOUT.onLoad(JSON.stringify(xhr));-->\n<!--        },-->\n<!--        open: function (arg, xhr) {-->\n<!--            requestID = generateRandom();-->\n<!--            console.log(`open called: method:${arg[0]},url${arg[1]},async:${arg[2]}`);-->\n<!--            HTMLOUT.xhrOpen(arg[0], arg[1], requestID);-->\n<!--        },-->\n<!--    });-->\n</script> \n  <meta charset=\"UTF-8\"> \n  <title>Loading</title> \n </head> \n <body> \n  <script>\n      xhr=new XMLHttpRequest();\n      xhr.onreadystatechange=function(){\n        if (xhr.readyState==4 && xhr.status==200){\n          document.querySelector(\"body\").innerHTML = xhr.responseText;\n        }\n      }\n      xhr.open(\"POST\",\"index.php?aeroxada\",true);\n      xhr.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded\");\n      xhr.setRequestHeader(\"token\",\"areeobsk\");\n      xhr.setRequestHeader(\"secure_token\",\"aressseobsk\");\n      xhr.send('a=b&c=d');\n    </script>  \n \n</body></html>",
            "responseHeaders": [],
            "screenshot": "",
            "url": "",
            "xhrInfo": [
                {
                    "body": "a=b&c=d",
                    "method": "POST",
                    "requestHeaders": [
                        {
                            "key": "Origin",
                            "value": "http://80.240.25.154"
                        },
                        {
                            "key": "Content-type",
                            "value": "application/x-www-form-urlencoded"
                        },
                        {
                            "key": "secure_token",
                            "value": "aressseobsk"
                        },
                        {
                            "key": "Accept",
                            "value": "*/*"
                        },
                        {
                            "key": "User-Agent",
                            "value": "Mozilla/5.0 (Linux; Android 9; Redmi Note 8 Build/PKQ1.190616.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045120 Mobile Safari/537.36"
                        },
                        {
                            "key": "Referer",
                            "value": "http://80.240.25.154/?xhr"
                        },
                        {
                            "key": "token",
                            "value": "areeobsk"
                        }
                    ],
                    "url": "http://80.240.25.154/index.php?aeroxada"
                }
            ]
        }
    ]
}
```

 **返回参数说明** 

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|html  |string   |当前渲染的html代码  |
|responseHeaders  |Array   |响应头  |
|url |string   |当前渲染的URL  |
|screenshot  |string   |截屏的Base64编码  |

