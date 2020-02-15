let request = require('request');

let loginUrl = "http://211.151.212.88/api4/login_new/";

function login(username, password) {
    let flag = "flag=false";
    let requestParams = "username=" + username + "&" + "password=" + (new Buffer(password + "tadu")).toString("base64") + "&" + flag;
    console.log("requeset params is :" + requestParams);
    request({
        url: loginUrl,
        method: "POST",
        headers: {
            "X-Client": " sdk=9;screenSize=1080*2261;type=ONEPLUSA6010;imei=869386045937995;imsi=460017175390072;android_id=99001249798100;version=6.6.50.865;mac=98:09:cf:07:02:26;rootPath=%2Fstorage%2Femulated%2F0;rn=0604940228;tdcn=2885577E740264020BC1178F4D7BC852248B40DD4FDBB3487639C46096AB28DAB906836AEEFA6CED;hotfix=1;ip=;android_id_new=80abe7ae20ad6f01;localTime=1581140265904;shuZiId=DuVWnCXSG5u1Y4nksHyf5e75sICrJ9RCEhLau7j/4QCcJA9eNIs8hO2GuwMaYuOkn0K00nosXnVGRFuATLC83wiQ;tdUUID=3ba6b7e1-5f89-49d7-8639-c164821a69bd;oaid=;package_name=wenxue;k=%C2%96%C2%98%C2%94%C2%95%C2%9C%C2%9A%C3%88%C2%97%C3%89%C2%98%C2%99%C3%8A%C3%89%C2%96%C2%9C%C3%87%C2%95%C2%97%C2%9C%C3%86%C2%9B%C2%9A%C2%9B%C2%97%C2%98%C2%97%C2%98%C2%9D%C2%9A%C2%99%C3%89%C2%96%C3%8A%C2%9A%C2%9E%C2%99%C2%9C;",
            "Content-Length": requestParams.length,
            "Content-Type": "application/x-www-form-urlencoded",
            "Host": "211.151.212.88",
            "Connection": "keep-alive"
        },
        form: {
            username: username,
            password: (new Buffer(password + "tadu")).toString("base64"),
            flag: true
        }
    }, function (error, response, body) {
        if (error) {
            console.log(error);
        } else {
            console.log(response.body);
        }
    });
}
login("td57814249", "pfvbxq");