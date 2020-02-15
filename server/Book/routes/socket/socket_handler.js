module.exports = class SocketHandler {

    constructor(req, res, next) {
        this.req = req;
        this.res = res;
        this.next = next;
        this.requestParams = this.req.body;
        console.log(this.requestParams);
        this.devicesID = this.requestParams.devicesID;
    };

    handler() {
        if (this.devicesID) {
            let client = global.sockets
                .get(this.devicesID)
                .emit('hook_params', JSON.stringify({
                    name: 'hc',
                    age: '12'
                }))
                .once('data_callback', (datas) => {
                    clearTimeout(timer);
                    console.log('receive data' + datas);
                    this.res.status(200).json({
                        msg: "success",
                        code: 200,
                        data: datas
                    });
                });

            let timer = setTimeout(() => {
                this.res.status(408).json({
                    code: 408,
                    msg: 'Request TimeOut',
                    data: ''
                });
                client
                    .removeAllListeners('data_callback', () => {
                        console.log('remove success')
                    });
                console.log('time delay 3000ms');
            }, 3000);


        }
    }
}