var packageJSON = require('./package.json');
var path = require('path');
var webpack = require('webpack');
module.exports = {
	entry: {
		
		add_part: "./assembler/add_part/main.jsx",
		wizard: "./assembler/add_part/wizard.jsx"
	},
	output: {
		path: path.join(__dirname, 'assembler/generated'),
		filename: '[name].bundle.js'
	},
	resolve: {
		extensions: ['.js', '.jsx', '.css', '.svg']
	},
	module: {
		rules: [
			{
                test: /\.jsx?$/,
                loader: 'babel-loader',
                exclude: /node_modules/,
				query: {
					presets: ["@babel/preset-env", "@babel/preset-react"]
				}
			},
			{
				test: /\.css$/i,
				use: ['style-loader', 'css-loader'],
			},
			{
				test: /\.svg$/,
				use: ['@svgr/webpack']
			}
        ]
    },
};