var express = require('express');
var connection = require('../libs/dbConnect.js').dbConnect();
var router = express.Router();

router.get('/:goo/:dong', function(req, res, next) {
    var params = [req.params.goo, req.params.dong];
    noShowList(res, params);
});

var noShowList = function(res, params) {
    var foodListSQL = 'SELECT s.store_name, s.phone, s.lat, s.lng, s.etc, f.enroll_id, f.food_name, f.food_num, f.price, ' +
        'f.discount_price, f.update_time, fi.food_img_url FROM store s LEFT JOIN food f ON s.phone=f.phone ' +
        'LEFT JOIN food_img fi ON f.enroll_id=fi.enroll_id WHERE s.goo=? AND s.dong=? ' +
        'AND fi.main=1 AND f.enroll_id NOT IN(SELECT enroll_id FROM reserve_list)';
    connection.query(foodListSQL, params, function (error, list) {
        if(error) {
            res.status(500).json({
                result: 'false',
                list: list
            });
        } else {
            if(!list[0]) {
                res.status(404).json({
                    result: 'false',
                    list: list
                });
            } else {
                res.status(200).json({
                    result: 'true',
                    list: list
                });
            }
        }
    });
};

router.get('/:enroll_id', function(req, res, next) {
    getDetailInfo(res, [req.params.enroll_id]);
});

var getDetailInfo = function(res, params) {
    var detailSQL = 'SELECT * FROM store s RIGHT JOIN food f ON s.phone=f.phone WHERE enroll_id=?';
    connection.query(detailSQL, params, function (error, info) {
        if(error) {
            res.status(500).json({
                result: 'false',
                info: info[0],
                img: null
            });
        } else {
            getFoodImg(res, params, info);
        }
    });
};

var getFoodImg = function(res, params, info) {
    var imgSQL = 'SELECT food_img_url, main FROM food_img WHERE enroll_id=?';
    connection.query(imgSQL, params, function (error, img) {
        if(error) {
            res.status(500).json({
                result: 'false',
                info: info[0],
                img: null
            });
        } else {
            res.status(200).json({
                result: 'false',
                info: info[0],
                img: img
            });
        }
    });
};

router.post('/reservation', function (req, res, next) {
    timeCheck(res, req.body.phone, req.body.arrival_time, function(check, time) {
        if(!check) {
            res.status(200).json({
                result: 'false',
                msg: 'sold out'
            });
        } else {
            var reserveSQL = 'INSERT INTO reserve_list(enroll_id, user_phone, arrival_time, end_time) values(?, ?, ?, ?)';
            var params = [req.body.enroll_id, req.body.phone, req.body.arrival_time, time];
            connection.query(reserveSQL, params, function (error, info) {
                if(error) {
                    res.status(500).json({
                        result: 'false',
                        msg: 'sold out'
                    });
                } else {
                    var list_params = [req.body.goo, req.body.dong];
                    noShowList(res, list_params);
                }
            });
        }
    });
});

//가장 마지막 서비스 이용 시간을 체크(2시간이 지나야 서비스 이용 가능 - 추가 노쇼 방지)
var timeCheck = function(res, phone, arrival_time, callback) {
    var timeCheckSQL = 'SELECT enroll_id, end_time FROM reserve_list WHERE user_phone=? ORDER BY arrival_time DESC LIMIT 1';
    var params = [phone];
    connection.query(timeCheckSQL, params, function (error, result) {
        if(error) {
            return callback(false);
        } else {
            var check = (result[0] == null);
            var time = getArrivalTime(arrival_time);
            if(check) { //null인 경우 = 예약이 없는 경우 바로 예약 가능
                return callback(check, time);
            } else {    //이전 예약이 있는 경우 시간 체크 후 예약 가능
                check = (getCurrentTime() > time);
                if(check) {
                    return callback(check, time);
                } else {
                    res.status(200).json({
                        result: 'false',
                        msg: 'time not yet'
                    });
                }
            }
        }
    });
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

var getArrivalTime = function(arrival_time) {
    var date = new Date();
    var hours = date.getHours();
    var minutes = Number(date.getMinutes())+Number(arrival_time)+120;   //현재 시간 + arrival_time + 2시간 = end_time
    if(minutes >= 120 && minutes < 180) {
        hours += 2;
        minutes -= 120;
    } else if(minutes >= 180 && minutes < 240) {
        hours += 3;
        minutes -= 180;
    } else if(minutes >= 240) {
        hours += 4;
        minutes -= 240;
    }
    var time =
        twoDigits(date.getFullYear(), 4)+'-'+
        twoDigits(date.getMonth()+1, 2)+'-'+
        twoDigits(date.getDate(), 2)+' '+
        twoDigits(hours, 2)+':'+
        twoDigits(minutes, 2)+':'+
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
