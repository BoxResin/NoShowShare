var express = require('express');
var connection = require('../libs/dbConnect.js').dbConnect();
var AWS = require('aws-sdk');
var awsKey = require('../libs/awsKey.js');
var multer = require('multer');
var multerS3 = require('multer-s3');
var router = express.Router();

AWS.config.update({
    accessKeyId: awsKey.accessKeyId(),
    secretAccessKey: awsKey.secretAccessKey(),
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

/* GET home page. */
router.get('/', function(req, res, next) {
  console.log('hello');
  res.render('login', { title: 'noShow' });
});

router.post('/login', function(req, res, next) {
  var name = req.body.name;
  var pw = req.body.password;

  var query = 'SELECT * FROM owner WHERE owner_name=?';
  connection.query(query, name, function (error, result) {
      if(error) {
          console.log("error : " + error);
      }else{

        var password = result[0].owner_pw;
        var id = result[0].owner_id;

        if(pw == password){
            var query = 'SELECT * FROM store WHERE owner_id=?';
            connection.query(query, id, function (error, info) {
              if(error) {
                  console.log("error : " + error);
              } else {
                console.log('info : ' + info);
                res.render('register', {store : JSON.stringify(info)});
              }
            });
        }else{
          console.log('check password');
        }
      }
  });

});
/*
router.post('/register', upload.array('food_img', 10), function (req, res, next) {
    var img_url = [];
    for(var i=0; i<req.files.length; i++) {
        img_url.push(req.files[i].location);
    }
    //phone 데이터는 어디서 가져올지
    var params = ["01098031992", req.body.food_name, req.body.food_num, req.body.price, req.body.discount_price, getCurrentTime()];
    infoUpload(res, params, img_url);
});
*/
router.post('/register', upload.array('food_img', 10), function (req, res, next){

  var store_id = req.body.store_id;
  var store_name = req.body.store_name;
  var phone = req.body.phone;

  var food_name = req.body.food_name;
  var food_num = req.body.food_num;
  var price = req.body.price;
  var discount_price = req.body.discount_price;
  //var food_img = req.body.food_img;

  var update_time = getCurrentTime()

  var img_url = [];
  for(var i=0; i<req.files.length; i++) {
      img_url.push(req.files[i].location);
  }

  var params = [phone, food_name, food_num, price, discount_price, update_time];
  infoUpload(res, params, img_url);
/*
  var query = 'INSERT INTO food(phone, food_name, food_num, price, discount_price, update_time) values(?, ?, ?, ?, ?, ?)';
  var params = [phone, food_name, food_num, price, discount_price, update_time];
  connection.query(query, params, function (error, result) {
      if(error) {
          console.log("error : " + error);
      }else{
        var enroll_id = result.insertId;
        var query = 'INSERT INTO food_img(enroll_id, food_img_url) values(?, ?)';
        var params = [enroll_id, food_img];
        connection.query(query, params, function(err, result){
          if(error){
            console.log("error : " + err);
          }else{
            res.redirect('/')
          }
        });
      }
  });
  */
});


/*
router.get('/register', function (req, res, next) {
    res.render('register');
});
*/

var infoUpload = function (res, params, img_url) {
    connection.beginTransaction(function (err) {
        if (err) {
            console.log(err);
        } else {
            var infoUploadSQL = 'INSERT INTO food(phone, food_name, food_num, price, discount_price, update_time) VALUES(?, ?, ?, ?, ?, ?)';
            connection.query(infoUploadSQL, params, function (error, info) {
                if(error) {
                    connection.rollback(function () {
                        res.status(500).render('register', { result: false });
                    });
                } else{
                    //imgUpload(res, info.insertId, img_url);
                    res.redirect('/');
                }
            });
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
    connection.query(imgUploadSQL, params, function (error, info) {
        if(error) {
            connection.rollback(function () {
                res.status(500).render('register', { result: false });
            });
        }
        connection.commit(function (err) {
            if(err) {
                connection.rollback(function () {
                    res.status(500).render('register', { result: false });
                });
            } else {
                res.status(200).render('register', { result: true });
            }
        });
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
