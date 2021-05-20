// const iconv = require("iconv-lite");
const { exec } = require('child_process');

const executeJava = (jarPath, filePath) => {
    return new Promise((resolve, reject) => {
        if (!filePath) { reject("Please enter the file path to convert."); return; }
        const child = exec(`java -Dfile.encoding=UTF-8 -jar ${jarPath} ${filePath}`, { encoding: 'binary' }, (error, stdout, stderr) => {
            // const result = iconvDecode(stdout);
            console.log('<<< convert success >>>');
            console.log(stdout);
            resolve(stdout);
            if (error !== null) {
                console.log('exec error: ' + error);
                reject(error);
            }
        });
    });
}

// const encoding = 'cp936';
// const binaryEncoding = 'binary';

// function iconvDecode(str = '') {
//     return iconv.decode(Buffer.from(str, binaryEncoding), encoding);
// }
module.exports = { executeJava };