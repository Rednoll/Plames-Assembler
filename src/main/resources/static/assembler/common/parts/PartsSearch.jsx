import React from 'react';

import $ from 'jquery';

import "./PartsSearch.css";

function PartSearchLabel(props) {

	return (

		<div onClick={props.onClick} className="suspect-module">

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

		$.get(this.props.restPartsAddress+searchQuery, (jsonModules)=> {

			modulesSearch.setState({parts: jsonModules});
		});
	}

	getSearchQuery() {

		return "?name="+this.state.searchName;
	}

	onClickPartLabel(part) {

		let partsArea = this.props.partsArea;

		if(partsArea != undefined && partsArea.current != null) {
			
			partsArea = partsArea.current;

			partsArea.addPart(part);
		}

		this.setState({});
	}

	render() {

		const parts = this.state.parts;

		let partsArea = this.props.partsArea;

		if(partsArea != undefined) {

			partsArea = partsArea.current;
		}

		return (

			<div className="search-container">

				<div className="search-data-container">

					<input type="text" placeholder="Market" className="search-name-field" onInput={(e)=> {this.setState({searchName: e.target.value}, this.refreshPartsList);}}/>

				</div>

				<div className="suspects-container">

					{parts.map((part)=> {

						if(partsArea == undefined || partsArea == null || !partsArea.hasPart(part)) {

							return (<PartSearchLabel onClick={(e)=> {this.onClickPartLabel(part)}} name={part.name} description={part.description}/>);
						}
					})}

				</div>

			</div>
		);
	}
}