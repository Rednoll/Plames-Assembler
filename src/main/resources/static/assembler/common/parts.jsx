import React from 'react';

import $ from 'jquery';

const restPartsAddress = "../rest/parts"

export class PartsSearch extends React.Component {

	constructor(props) {
		super(props);

		this.state = {

			parts: [],
			searchName: ""
		};
	}

	refreshPartsList() {

		let searchQuery = this.getSearchQuery();

		let modulesSearch = this;

		$.get(restPartsAddress+"/modules"+searchQuery, (jsonModules)=> {

			modulesSearch.state.parts = jsonModules;
		});
	}

	getSearchQuery() {

		return "?name="+this.state.searchName;
	}

	render() {

		return (

			<div>

				<div class="search-data-container">
					<input type="text" placeholder="Market" class="name-field" onInput={(e)=> { this.setState({searchName: e.target.value}); this.refreshPartsList();}} />
				</div>

				<div class="suspects-container">

					{this.state.parts.map((part) => {

						<div class="suspect-module">

							<div class="content" style={{border: "none", marginLeft: "12px", marginRight: "12px", marginBottom: "10px"}}>

								<p class="h-sub name" style={{marginBottom: "8px"}}>{part.name}</p>

								<p class="standard-text description">{part.description}</p>

							</div>

							<div style={{borderBottom: "1px solid lightgrey", marginLeft: "12px", marginRight: "12px"}}></div>

						</div>
					})}

				</div>

			</div>
		);
	}
}