| 错误码  | 错误解释  |
| ------------ | ------------ |
|  408 |  接口超时|
|  500|  服务器内部错误|
|  40001 | 缺失参数  |

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
	"data":
				{
  				 "deviceBrand":"OnePlus",
  	 			"devicesID":"ucHuyQ6fAQ7je59UAAAA",
  				 "ipAddress":"192.168.0.108",
 				  "systemModel":"ONEPLUS A6010"
				  }
  }
```

 **返回参数说明** 

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|devicesID |string   |设备ID，用于向具体设备发送指令  |
|ipAddress |string   |设备IP  |
|systemModel |string   |设备型号  |

# 发送指令
**请求URL：** 
- ` http://ip:3000/webHook `
  
**请求方式：**
- POST 

**请求参数参数：** 

|参数名|类型|是否必须|说明|
|:-----  |:-----|:----------                           |
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



 **返回示例**

```
  {
    "code": 200,
	"msg":"success"
	"data":[
		{
        "html": "<html><head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">get successundefined</pre></body></html>",
        "responseHeaders": [
            {
                "key": "connection",
                "value": "keep-alive"
            },
            {
                "key": "content-length",
                "value": "20"
            },
            {
                "key": "content-type",
                "value": "text/html; charset=utf-8"
            },
            {
                "key": "date",
                "value": "Sat, 22 Feb 2020 07:20:12 GMT"
            },
            {
                "key": "etag",
                "value": "W/\"14-egV51QIW9Fh6WBssjF7NgB25Ckg\""
            },
            {
                "key": "x-powered-by",
                "value": "Express"
            }
        ],
        "screenshot": "iVBORw0KGgoAAAANSUhEUgAABDgAAAgCCAIAAAABM7VnAAAAA3NCSVQICAjb4U/gAAAgAElEQVR4nOzdd1wUd+L/8aFKU0BUFARREQVEUFDBFo0xGhvGWFM0xngmeqYnxtzl7kxMvUtPjDGJOTW2qLEjltiVYFcUpapIEREQ6f33x/5uvuvM7oKwCx/09Xz4xzo789nPzs4u8575FDPpg/kSAAAAAIjEvLErAAAAAABKBBUAAAAAwiGoAAAAABAOQQUAAACAcAgqAAAAAIRDUAEAAAAgHIIKAAAAAOEQVAAAAAAIh6ACAAAAQDgEFQAAAADCIagAAAAAEA5BBQAAAIBwCCoAAAAAhENQAQAAACAcggoAAAAA4RBUAAAAAAiHoAIAAABAOAQ,
		"url": "http://192.168.0.104:3000/gettest"
		},
		{
        "html": "<html><head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">get successundefined</pre></body></html>",
        "responseHeaders": [
            {
                "key": "connection",
                "value": "keep-alive"
            },
            {
                "key": "content-length",
                "value": "20"
            },
            {
                "key": "content-type",
                "value": "text/html; charset=utf-8"
            },
            {
                "key": "date",
                "value": "Sat, 22 Feb 2020 07:20:12 GMT"
            },
            {
                "key": "etag",
                "value": "W/\"14-egV51QIW9Fh6WBssjF7NgB25Ckg\""
            },
            {
                "key": "x-powered-by",
                "value": "Express"
            }
        ],
        "screenshot": "iVBORw0KGgoAAAANSUhEUgAABDgAAAgCCAIAAAABM7VnAAAAA3NCSVQICAjb4U/gAAAgAElEQVR4nOzdd1wUd+L/8aFKU0BUFARREQVEUFDBFo0xGhvGWFM0xngmeqYnxtzl7kxMvUtPjDGJOTW2qLEjltiVYFcUpapIEREQ6f33x/5uvuvM7oKwCx/09Xz4xzo789nPzs4u8575FDPpg/kSAAAAAIjEvLErAAAAAABKBBUAAAAAwiGoAAAAABAOQQUAAACAcAgqAAAAAIRDUAEAAAAgHIIKAAAAAOEQVAAAAAAIh6ACAAAAQDgEFQAAAADCIagAAAAAEA5BBQAAAIBwCCoAAAAAhENQAQAAACAcggoAAAAA4RBUAAAAAAiHoAIAAABAOAQ,
		"url": "http://192.168.0.104:3000/gettest"		},
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

