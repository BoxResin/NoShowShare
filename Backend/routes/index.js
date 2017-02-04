var express = require('express');
var connection = require('../libs/dbConnect.js').dbConnect();
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  console.log('hello');
  res.render('login', { title: 'noShow' });
});

router.post('/login', function(req, res, next) {
  var name = req.body.name;
  var pw = req.body.password;

  //var imgSQL = 'SELECT food_img_url, main FROM food_img WHERE enroll_id=?';

  //var query = "SELECT * FROM owner WHERE owner_name='" + name + "'";
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
                //res.render('register', {store : JSON.stringify(info)});
                res.render('register', {store : JSON.stringify(info)});
              }
            });
        }else{
          console.log('check password');
        }
      }
  });

});

router.post('/register', function (req, res, next){
  var store_id = req.body.store_id;
  var store_name = req.body.store_name;
  var phone = req.body.phone;

  var food_name = req.body.food_name;
  var food_num = req.body.food_num;
  var price = req.body.price;
  var discount_price = req.body.discount_price;
  var food_img = req.body.food_img;

  var date = new Date();
  var update_time =
      twoDigits(date.getFullYear(), 4)+'-'+
      twoDigits(date.getMonth()+1, 2)+'-'+
      twoDigits(date.getDate(), 2)+' '+

      twoDigits(date.getHours(), 2)+':'+
      twoDigits(date.getMinutes(), 2)+':'+
      twoDigits(date.getSeconds(), 2);


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
});

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
