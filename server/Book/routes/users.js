var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function (req, res, next) {
  console.log(req.method);
  res.send('<html><body><a class="a1">aaa</a></body></html>');
});

router.post('/post', function (req, res, next) {
  console.log(req.body.params);
  res.send('Post test success');
});

module.exports = router;