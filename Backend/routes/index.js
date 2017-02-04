var express = require('express');
var formidable = require('formidable');
var AWS = require('aws-sdk');
var multer = require('multer');
var multerS3 = require('multer-s3');
var connection = require('../libs/dbConnect.js').dbConnect();
var router = express.Router();

var memorystorage = multer.memoryStorage();
var upload = multer({ storage: memorystorage });

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/register', function (req, res, next) {
    res.render('register');
});

router.post('/register', upload.array('/libs'), function (req, res, next) {
    imgFileSave(req);
    // fileUpload(req);
});

var imgFileSave = function (req) {
    var form = new formidable.IncomingForm();

    form.parse(req);
    form.on('fileBegin', function (name, file){
        file.path = __dirname + '/uploads/' + file.name;
    });
    form.on('file', function (name, file){
        console.log('Uploaded ' + file.name);
    });
};

var fileUpload = function (req) {
    req.files.forEach(function (fileObj, index) {
        aws.config.region = 'ap-northeast-2'; //Seoul
        aws.config.update({
            accessKeyId: "AKIAILPQ4NIT6MHQKCLQ",
            secretAccessKey: ""
        });

        var s3_params = {
            Bucket: 'unithon-noshow',
            Key: fileObj.originalname,
            ACL: 'public-read',
            ContentType: fileObj.mimetype
        };

        var s3obj = new aws.S3({ params: s3_params });
        s3obj.upload({ Body: el.buffer })
            .on('httpUploadProgress', function (evt) { console.log(evt); })
            .send(function (err, data) {
                var url = data.Location;
                console.log(url);
        });
    });
};


module.exports = router;
