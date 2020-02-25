// config.js

let path = require('path');

// 日志根目录
let baseLogPath = path.resolve(__dirname, '../logs');
// 请求日志目录
let reqPath = '/request';
// 请求日志文件名
let reqFileName = 'request';
// 请求日志输出完整路径
let reqLogPath = baseLogPath + reqPath + '/' + reqFileName;


// 响应日志目录
let resPath = '/response';
// 响应日志文件名
let resFileName = 'response';
// 响应日志输出完整路径
let resLogPath = baseLogPath + resPath + '/' + resFileName;

// 错误日志目录
let errPath = '/error';
// 错误日志文件名
let errFileName = 'error';
// 错误日志输出完整路径
let errLogPath = baseLogPath + errPath + '/' + errFileName;


// 串口日志目录
let comPath = '/com';
// 串口日志文件名
let comFileName = 'com';
// 串口日志输出完整路径
let comLogPath = baseLogPath + comPath + '/' + comFileName;


module.exports = {
    appenders: {
        // 所有的日志
        'console': {
            type: 'console'
        },
        // 请求日志
        'reqLogger': {
            type: 'dateFile', // 日志类型
            filename: reqLogPath, // 输出文件名
            pattern: '-yyyy-MM-dd-hh.log', // 后缀
            alwaysIncludePattern: true, // 上面两个参数是否合并
            encoding: 'utf-8', // 编码格式
            maxLogSize: Number.MAX_VALUE, // 最大存储内容
        },
        // 响应日志
        'resLogger': {
            type: 'dateFile',
            filename: resLogPath,
            pattern: '-yyyy-MM-dd-hh.log',
            alwaysIncludePattern: true,
            encoding: 'utf-8',
            maxLogSize: Number.MAX_VALUE,
        },
        // 错误日志
        'errLogger': {
            type: 'dateFile',
            filename: errLogPath,
            pattern: '-yyyy-MM-dd-hh.log',
            alwaysIncludePattern: true,
            encoding: 'utf-8',
            maxLogSize: Number.MAX_VALUE,
        },
        'comLogger': {
            type: 'dateFile',
            filename: comLogPath,
            pattern: '-yyyy-MM-dd-hh.log',
            alwaysIncludePattern: true,
            encoding: 'utf-8',
            maxLogSize: Number.MAX_VALUE,
        }
    },
    // 分类以及日志等级
    categories: {
        default: {
            appenders: ['console'],
            level: 'all'
        },
        reqLogger: {
            appenders: ['reqLogger'],
            level: 'info'
        },
        resLogger: {
            appenders: ['resLogger'],
            level: 'info'
        },
        errLogger: {
            appenders: ['errLogger'],
            level: 'error'
        },
        comLogger: {
            appenders: ['comLogger'],
            level: 'info'
        }
    },
}