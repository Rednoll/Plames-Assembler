import React from 'react';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Typography from '@material-ui/core/Typography';
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';
import MenuList from '@material-ui/core/MenuList';

import $ from 'jquery';

import "./PartsArea.css";

import StopIcon from "../images/stop.svg";

function PartLabel(props){

	const initialState = {
		
		mouseX: null,
		mouseY: null
	};

	const [state, setState] = React.useState(initialState);

	const part = props.part;
	const selectPart = props.selectPart;
	const removePart = props.removePart;

	const openContextMenu = (e)=> {

		if($(e.target).closest(".plames-part-label").prop("class") != undefined) {

			e.preventDefault();

			setState({mouseX: e.clientX - 4, mouseY: e.clientY - 8})
		}
		else {

			handleClose();
		}
	};

	const handleClose = (e)=> {
		
		setState(initialState);
	};

	const removePartHandler = ()=> {

		removePart(part);
		handleClose();
	};

	return (

		<div onContextMenu={openContextMenu} onClick={(e)=> {selectPart(part)}} className={"plames-part-label " + (props.selected ? "selected" : "")}>

			<img className="icon" src={part.icon}></img>
			<p className="name">{part.name}</p>

			<Menu keepMounted open={state.mouseY !== null} MenuListProps={{onMouseLeave: handleClose}} onClose={handleClose} anchorReference="anchorPosition" anchorPosition={state.mouseX !== null && state.mouseY !== null ? {top: state.mouseY, left: state.mouseX} : undefined}>

				<MenuItem onClick={removePartHandler}>

					<img class="part-context-menu-img" src="../resources/assembler/common/images/stop.svg"></img>

	      			<Typography color="textPrimary">remove</Typography>

				</MenuItem>

			</Menu>

		</div>
	);
}

export default class PartsArea extends React.Component {

	constructor(props) {
		super(props);

		this.state = {

			parts: [],
			selectedPart: {},
			selectable: false
		};
	}

	hasPart(part) {

		for(let index in this.state.parts) {

			let suspect = this.state.parts[index];

			if(suspect.id == part.id) {

				return true;
			}
		}

		return false;
	}

	selectPart(part) {

		if(!this.state.selectable) return;

		this.setState({selectedPart: part});
	}

	addPart(part) {

		this.state.parts.push(part);

		this.setState({

			parts: this.state.parts
		});

		if(this.props.partsSearch != undefined && this.props.partsSearch.current != null) {

			this.props.partsSearch.current.setState({});
		}
	}

	removePart(part) {

		this.state.parts.splice(this.state.parts.indexOf(part), 1);
		
		this.setState({

			parts: this.state.parts
		});	

		if(this.props.partsSearch != undefined && this.props.partsSearch.current != null) {

			this.props.partsSearch.current.setState({});
		}
	}

	render() {

		const parts = this.state.parts;

		return (

			<div className="plames-parts-area">
				
				{parts.map((part)=> <PartLabel id={part.id} selected={this.state.selectedPart.id == part.id} part={part} selectPart={(part)=> this.selectPart(part)} removePart={(part)=> this.removePart(part)}/>)}

				{parts.length == 0 &&

					<div className="empty-container">

						<p className="empty-text">Empty</p>

					</div>
				}
			
			</div>
		);
	}
}