import React from 'react';
import ReactDOM from 'react-dom';
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import { withStyles } from "@material-ui/core/styles";
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';

import styles from "./jss_styles.js";
import mainTheme from "../common/jss_styles.jsx";

let addPartForm = null;

class AddPartForm extends React.Component {

	constructor(props) {
		super(props);

		this.state = {

			nameValid: true,
			nameHelperText: ""
		};
		
		addPartForm = this;
	}

	render() {

		const { classes } = this.props;

		return (

			<form id="addpart-form" name="f" action="#" method="POST">

				<div>

					<ThemeProvider theme={mainTheme}>
						
						<TextField fullWidth id="name-field" name="name" InputProps={{ classes: { input: classes.addPartFormFieldInput } }} className={classes.addPartFormField} error={!this.state.nameValid} helperText={this.state.nameHelperText} label="Name" />

					</ThemeProvider>

				</div>

				<div id="embodimen-container">
				
					<div id="embodiment-choose-container">
						
						<ThemeProvider theme={mainTheme}>
							
							<Button variant="contained" color="primary">Gradle Project</Button>

							<div class="air" style={{ width: "10px"}}></div>

							<Button variant="outlined" color="primary">Artifact</Button>

						</ThemeProvider>

					</div>

				</div>

			</form>
		);
	}
}

const StyledAddPartForm = withStyles(styles)(AddPartForm);

ReactDOM.render(<StyledAddPartForm />, document.querySelector("#addpart-form-container"));