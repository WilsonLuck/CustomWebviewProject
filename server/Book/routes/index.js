var express = require('express');
var router = express.Router();
let SocketHandler = require('../routes/socket/socket_handler');
/* GET home page. */
router.get('/', function (req, res, next) {
  console.log(req.headers);
  console.log(req.query);
  res.render('index', {
    title: 'Express'
  });
});

router.get('/gettest', (req, res, next) => {
  let name = req.query.username;
  res.send("get success" + name);
});

router.post('/posttest', function (req, res, next) {
  console.log(req.body);
  // res.sendStatus(307);
  res.render('index', {
    title: 'Express',

  });
});

router.get("/getjson", (req, res, next) => {
  res.json({
    "name": "hc",
    "age": 12
  })
});

router.post("/webHook", (req, res, next) => {
  //需要打印的信息
  var loggerInfo = `\n Method:${req.method}`;
  //请求头
  loggerInfo += `\n headers: ${JSON.stringify(req.headers)} \n`;

  if (req.method == 'GET') {
    loggerInfo += ` data: ${JSON.stringify(req.query)} \n`;
  } else {
    loggerInfo += ` data: ${JSON.stringify(req.body)} \n`;
  }
  global.reqLogger.info(loggerInfo);
  return new SocketHandler(req, res, next).handler();
});
module.exports = router;