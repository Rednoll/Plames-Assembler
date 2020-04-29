import React from 'react';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from "@material-ui/core/styles";

import $ from 'jquery';

const styles = {

	root: {

		minWidth: "max-content",
		width: "max-content",
		maxWidth: "max-content",
		background: "#BAE1FF",
		padding: "5px 8px 5px 8px",
		cursor: "pointer"
	}
};


class ProductKeyViewer extends React.Component {

	constructor(props) {
		super(props);

		this.state = {

			productKey: null,
			elevation: 2
		}

		this.onMouseEnter = this.onMouseEnter.bind(this);
		this.onMouseLeave = this.onMouseLeave.bind(this);
		this.onClick = this.onClick.bind(this);
	}

	componentDidMount() {

		$.ajax({

			url: "../authorized/user/product_key",
			method: "GET",
			success: (productKey)=> {

				this.setState({productKey: productKey});
			},
			statusCode: {

				401: ()=> {

					this.setState({productKey: "None authorized error!"});
				}
			}
		});
	}

	onMouseEnter(e) {

		this.setState({elevation: 4});
	}

	onMouseLeave(e) {

		this.setState({elevation: 2});
	}

	onClick(e) {

		this.setState({elevation: 2});

		setTimeout(()=> this.setState({elevation: 4}), 200);
	}

	render() {

		let { classes } = this.props;

		return (

			<div>

				{this.props.withLabel &&
					
					<div>
						<Typography variant="subtitle1" color="textPrimary">Product key</Typography>
						<div style={{height: "0.2em"}}></div>
					</div>
				}

				<Tooltip title="Copy" enterDelay={200}>
				<Paper onClick={this.onClick} className={classes.root} elevation={this.state.elevation} onClick={this.onClick} onMouseEnter={this.onMouseEnter} onMouseLeave={this.onMouseLeave}>

					<div class="btn" data-clipboard-text={this.state.productKey}>

						<Typography variant="h6" color="textPrimary">{this.state.productKey}</Typography>

					</div>

				</Paper>
				</Tooltip>

			</div>
		);
	}
}

export default withStyles(styles)(ProductKeyViewer);