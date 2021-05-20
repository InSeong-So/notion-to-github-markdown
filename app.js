const dotenv = require('dotenv');
const path = require('path');
const { executeJava } = require('./src/service');

// 1. dotenv set
dotenv.config({ path: path.resolve(process.cwd(), ".env") });

// 2. parameter set
const jarPath = process.env.JAR_FILE;
const filePath = process.argv[2];

// 3. excute
executeJava(jarPath, filePath);