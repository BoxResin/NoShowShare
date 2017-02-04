var express = require('express');
var AWS = require('aws-sdk');
var connection = require('../libs/dbConnect.js').dbConnect();
var multer = require('multer');
var multerS3 = require('multer-s3');
AWS.config.update({
    accessKeyId: "AKIAJ2HECAE6J7NYT62A",
    secretAccessKey: "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",
    region: "ap-northeast-2",
    signatureVersion: 'v4'
});
var s3 = new AWS.S3();
var router = express.Router();

var upload = multer({
    storage: multerS3({
        s3: s3,
        bucket: 'unithon-noshow',
        acl: 'public-read',
        key: function (req, file, cb) {
            cb(null, file.originalname);
        }
    })
});

router.get('/register', function (req, res, next) {
    res.render('register');
});

router.post('/register', upload.array('food_img', 10), function (req, res, next) {
    var img_url = [];
    for(var i=0; i<req.files.length; i++) {
        img_url.push(req.files[i].location);
    }
    var params = ["01098031992", req.body.food_name, req.body.food_num, req.body.price, req.body.discount_price, getCurrentTime()];
    infoUpload(res, params, img_url);
});

var infoUpload = function (res, params, img_url) {
    var infoUploadSQL = 'INSERT INTO food(phone, food_name, food_num, price, discount_price, update_time) VALUES(?, ?, ?, ?, ?, ?)';
    connection.query(infoUploadSQL, params, function (error, info) {
        if(error) {
            console.log('1 : '+error);
            res.status(500).json({ result: false });
        } else{
            imgUpload(res, info.insertId, img_url);
        }
    });
};

var imgUpload = function (res, enroll_id, img_url) {
    var imgUploadSQL = 'INSERT INTO food_img(enroll_id, food_img_url) VALUES';
    var params = [];
    for(var i=0; i<img_url.length; i++) {
        imgUploadSQL += '(?, ?)';
        if(i != img_url.length-1) imgUploadSQL += ',';
        params.push(enroll_id, img_url[i]);
    }
    console.log(imgUploadSQL);
    console.log(params);
    connection.query(imgUploadSQL, params, function (error, info) {
        if(error) {
            console.log('2 : '+error);
            res.status(500).json({ result: false });
        } else {
            res.status(200).json({ result: true });
        }
    })
};

var getCurrentTime = function() {
    var date = new Date();
    var time =
        twoDigits(date.getFullYear(), 4)+'-'+
        twoDigits(date.getMonth()+1, 2)+'-'+
        twoDigits(date.getDate(), 2)+' '+

        twoDigits(date.getHours(), 2)+':'+
        twoDigits(date.getMinutes(), 2)+':'+
        twoDigits(date.getSeconds(), 2);

    return time;
};

var twoDigits = function(str, digits) {
    var zero = '';
    str = str.toString();
    if (str.length < digits) {
        for (var i = 0; i < digits - str.length; i++)
            zero += '0';
    }
    return zero + str;
};

module.exports = router;
