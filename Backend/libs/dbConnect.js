/**
 * Created by choi on 2017-02-04.
 */
var mysql = require('mysql');

exports.dbConnect = function() {
    return connection = mysql.createConnection({
      host: 'aws-rds-bk.cdinvyqu4zcg.ap-northeast-2.rds.amazonaws.com',
              user: 'user',
              password: 'dho1921!036',
              database: 'no_show'
    });
};
