import React from 'react';
import ReactDOM from 'react-dom';

import $ from "jquery";

import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import { withStyles } from "@material-ui/core/styles";
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import FormControl from '@material-ui/core/FormControl';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Typography from '@material-ui/core/Typography';

import PartsSearch from "../common/parts/PartsSearch";
import PartsArea from "../common/parts/PartsArea";

import styles from "./jss_styles.js";
import mainTheme from "../common/jss_styles.jsx";

let addPartForm = null;

function changeContainer(fromContainer, toContainer) {

	fromContainer.animate({opacity: "0"}, 500, ()=> {

		fromContainer.css("display", "none");

		toContainer.css("display", "flex");
		toContainer.animate({opacity: "1"}, 500);
	});
}

const showGradleContainer = (e)=> {

	let addPartFormJq = $("#addpart-form");

	addPartFormJq.append('<input type="hidden" name="embodiment-type" value="gradle"/>');

	let chooseContainer = $("#embodiment-choose-container");
	let gradleContainer = $("#embodiment-gradle-container");

	changeContainer(chooseContainer, gradleContainer);

	$("#embodiment-container-h").html("Embodiment Data<span style='color: lightgrey'> (Gradle Project)</span>")
};

const showGitContainer = (e)=> {

	let addPartFormJq = $("#addpart-form");

	addPartFormJq.append('<input type="hidden" name="prvider-type" value="git_repository"/>');

	let chooseContainer = $("#provider-choose-container");
	let gitContainer = $("#provider-git-container");

	changeContainer(chooseContainer, gitContainer);

	$("#provider-container-h").html("Provider Data<span style='color: lightgrey'> (Git Repository)</span>")
};

function GitCredentialSelect(props) {

	const { classes } = props;

	let credentials = null;

	$.ajax({

		url: "../authorized/user/credential_labels",
		method: "GET",
		async: false,
		success: (data)=> {

			credentials = data;
		}
	});

	return (

		<FormControl className={classes.formControlGitCredential}>
											
			<InputLabel id="git-credential-select-label" color="primary">Credential</InputLabel>

			<Select id="git-credential-select" defaultValue="default" labelId="git-credential-select-label">

				<MenuItem value="default">default</MenuItem>

				{credentials.map((credential) => <MenuItem value={credential.id}>{credential.displayId}</MenuItem>)}

			</Select>

		</FormControl>
	);
}

GitCredentialSelect = withStyles(styles)(GitCredentialSelect);

class AddPartForm extends React.Component {

	constructor(props) {
		super(props);

		this.state = {

			nameValid: true,
			nameHelperText: "",
			renderGitCredential: false
		};
		
		addPartForm = this;
	}

	render() {

		const { classes } = this.props;

		return (

			<form id="addpart-form" name="f" action="#" method="POST">

				<div id="form-left-block">

					<div id="main-data-container">

						<p class="h-main" style={{margin: "10px"}}>Main Data</p>

						<div id="main-data-fields-container">

							<ThemeProvider theme={mainTheme}>
								
								<FormControl className={classes.formControlName}>
									
									<TextField id="name-field" name="name" error={!this.state.nameValid} helperText={this.state.nameHelperText} label="Name" />

								</FormControl>

								<div class="air" style={{width: "10px"}}></div>

								<FormControl className={classes.formControl}>
								
									<InputLabel id="type-select-label" color="primary">Type</InputLabel>

									<Select id="type-select" labelId="type-select-label">

										<MenuItem value="API">API</MenuItem>
										<MenuItem value="Module">Module</MenuItem>
										
									</Select>
								
								</FormControl>

							</ThemeProvider>

						</div>

					</div>

					<div id="embodimen-container">

						<p id="embodiment-container-h" class="h-main" style={{margin: "10px", opactiy: "0"}}>Embodiment Data</p>

						<div id="embodiment-choose-container">
							
							<ThemeProvider theme={mainTheme}>
								
								<Button variant="contained" color="primary" onClick={showGradleContainer}>Gradle Project</Button>

								<div class="air" style={{ width: "10px"}}></div>

								<Button variant="outlined" color="primary">Artifact</Button>

							</ThemeProvider>

						</div>

						<div id="embodiment-gradle-container" style={{display: "none"}}>

							<ThemeProvider theme={mainTheme}>
								
								<TextField id="gradle-name-field" name="embodiment-name" label="Name" />

							</ThemeProvider>

						</div>

					</div>

					<div id="provider-container">

						<p id="provider-container-h" class="h-main" style={{margin: "10px", opactiy: "0"}}>Provider Data</p>

						<div id="provider-choose-container">
							
							<ThemeProvider theme={mainTheme}>
								
								<Button variant="contained" color="primary" onClick={showGitContainer}>Git Repository</Button>

							</ThemeProvider>

						</div>

						<div id="provider-git-container" style={{display: "none"}}>

							<ThemeProvider theme={mainTheme}>
								
								<div style={{ display: "flex", flexDirection: "row", justifyContent: "flex-start", alignItems: "center"}}>
									
									<FormControl className={classes.formControlGitAddress}>
										
										<TextField id="git-address-field" name="git-address" label="Address" />

									</FormControl>

									<div class="air" style={{ width: "10px"}}></div>

									<FormControlLabel color="primary" style={{ marginBottom: "0px" }} label={<Typography color="textPrimary">Private repository</Typography>} control={ <Checkbox id="git-private-checkbox" onChange={(e)=> this.setState({renderGitCredential: e.target.checked})} color="primary" name="git-private" /> } />

								</div>

								{ this.state.renderGitCredential && <GitCredentialSelect /> }

							</ThemeProvider>

						</div>

					</div>

					<div id="dependencies-container">

						<p id="dependencies-container-h" class="h-main" style={{margin: "10px", opactiy: "0"}}>Dependencies</p>

						<div style={{padding: "10px"}}>
							
							<PartsArea />

						</div>

					</div>

				</div>

				<div style={{borderLeft: "1px solid lightgrey", height: "100%"}}></div>

				<div id="form-right-block">
					
					<div id="modules-repository-container">

						<p id="modules-repository-container-h" class="h-main" style={{padding: "10px", opactiy: "0", borderBottom: "1px solid lightgrey"}}>Modules Repository</p>

						<PartsSearch theme={mainTheme} />

					</div>
				
				</div>

			</form>
		);
	}
}

const StyledAddPartForm = withStyles(styles)(AddPartForm);

ReactDOM.render(<StyledAddPartForm />, document.querySelector("#addpart-form-container"));