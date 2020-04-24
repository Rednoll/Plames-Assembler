import React from 'react';

import $ from 'jquery';

import "./PartsSearch.css";

const restPartsAddress = "../rest/parts"

function PartSearchLabel(props) {

	return (

		<div className="suspect-module">

			<div className="content" style={{border: "none", marginLeft: "12px", marginRight: "12px", marginBottom: "10px"}}>

				<p className="h-sub name" style={{marginBottom: "8px"}}>{props.name}</p>

				<p className="standard-text description">{props.description}</p>

			</div>

			<div style={{borderBottom: "1px solid lightgrey", marginLeft: "12px", marginRight: "12px"}}></div>

		</div>
	);
}

export default class PartsSearch extends React.Component {

	constructor(props) {
		super(props);

		this.state = {

			parts: [],
			searchName: ""
		};

		this.refreshPartsList();
	}

	refreshPartsList() {

		let searchQuery = this.getSearchQuery();

		let modulesSearch = this;

		$.get(restPartsAddress+"/modules"+searchQuery, (jsonModules)=> {

			modulesSearch.setState({parts: jsonModules});
		});

		console.log("try search: "+this.state.searchName);
	}

	getSearchQuery() {

		return "?name="+this.state.searchName;
	}

	render() {

		const parts = this.state.parts;

		return (

			<div class="search-container">

				<div className="search-data-container">

					<input type="text" placeholder="Market" className="search-name-field" onInput={(e)=> {this.setState({searchName: e.target.value}, this.refreshPartsList);}}/>

				</div>

				<div className="suspects-container">

					{parts.map((part)=> <PartSearchLabel name={part.name} description={part.description}/>)}

				</div>

			</div>
		);
	}
}