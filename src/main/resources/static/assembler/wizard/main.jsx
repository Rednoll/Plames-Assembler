import React from 'react';
import ReactDOM from 'react-dom';
import $ from "jquery";

import TextField from "@material-ui/core/TextField";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
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
import Paper from '@material-ui/core/Paper';

import PartsSearch from "../common/parts/PartsSearch";
import PartsArea from "../common/parts/PartsArea";
import ProductKeyViewer from "../common/user/ProductKeyViewer";

import styles from "./jss_styles.js";
import mainTheme from "../common/jss_styles.jsx";

import ClipboardJS from "clipboard";

let loadBuildLogInterval = null;

function waitQueue() {

    $.ajax({

        url: "../ajax/request/place_in_queue",
        method: "GET",
        async: true
    })
    .done((data)=> {

        if(data.placeInQueue > 0) {

            $("#build-log").html("Please wait, place in queue: "+data.placeInQueue+"</br>(estimated waiting time: "+data.estimatedWaitingTime.toFixed(2)+"s)");
            
            setTimeout(waitQueue, 2000);
        }
        else {

            startLoadBuildLog();
        }
    });
}

function startLoadBuildLog(){

    loadBuildLogInterval = setInterval(loadBuildLog, 750, 5);

    $.ajax({

        url: "../ajax/request/wait_build_end",
        method: "GET",
        timeout: 3600000,
        async: true
    })
    .done(()=> {

        clearInterval(loadBuildLogInterval);

        loadBuildLog(-1);
    });
}

function loadBuildLog(linesCount) {

    $.ajax({

        url: "../ajax/request/build_log_news?linesCount="+linesCount,
        method: "GET",
        async: false
    })
    .done((news)=> {

        let jsLogContainer = document.getElementById("build-log-container");
        let log = $("#build-log");

        if(log.hasClass("wait")) {

            log.removeClass("wait");
            log.html("");
        }

        for(let index in news) {

            let line = news[index];

            log.html(log.html()+line+"</br>");

        }
        
        $(jsLogContainer).stop().animate({scrollTop: jsLogContainer.scrollHeight}, 250);
    });
}

class Wizard extends React.Component {

    constructor(props) {
        super(props);
        
        this.bootloadersArea = React.createRef();
        this.coresArea = React.createRef();
        this.modulesArea = React.createRef();
        this.partsSearch = React.createRef();
        this.buildProgressBar = React.createRef();

        this.beginGeneration = this.beginGeneration.bind(this);
    }

    componentDidMount() {

        $.get("../rest/parts/bootloaders", (data)=> {

            this.bootloadersArea.current.setState({parts: data});

            if(data.length == 1) {
            
                this.bootloadersArea.current.selectPart(data[0]);
            }
        });

        $.get("../rest/parts/cores", (data)=> {

            this.coresArea.current.setState({parts: data});

            if(data.length == 1) {
                
                this.coresArea.current.selectPart(data[0]);
            }
        });
    }

    beginGeneration() {
        
        let mainDataContainer = $("#main-data-container");
        let generationContainer = $("#generation-content-container");

        let buildProgressBar = this.buildProgressBar;

        let bootloadersArea = this.bootloadersArea;
        let coresArea = this.coresArea;
        let modulesArea = this.modulesArea;

        $.ajax({

            url: "../ajax/request/create",
            method: "POST",
            headers: {

                "Content-Type": "application/json"
            },
            data: JSON.stringify({

                bootloader: bootloadersArea.current.state.selectedPart,
                core: coresArea.current.state.selectedPart,
                modules: modulesArea.current.state.parts
            })
        })
        .done((data)=> {

            buildProgressBar.current.loadSteps();
            buildProgressBar.current.startWaitStepChanges();

            mainDataContainer.animate({"opacity": "0"}, 500, "swing", ()=> {

                mainDataContainer.css("display", "none");
                generationContainer.removeClass("hidden");

                $.ajax({

                    url: "../ajax/request/build",
                    method: "GET"
                })
                .done((placeInQueue)=> {

                    $("#build-log").html("Please wait, place in queue: "+placeInQueue);
                    
                    if(placeInQueue <= 0) {
                        
                        startLoadBuildLog();  
                    }
                    else {

                        waitQueue();
                    }
                });

            });

            $.ajax({

                url: "../ajax/request/wait_build_end",
                method: "GET",
                timeout: 3600000
            })
            .done((success)=> {

                let finalContentContainer = $("#final-content-container");

                generationContainer.animate({"opacity": "0"}, 500, "swing", ()=> {

                    generationContainer.css("display", "none");
                    finalContentContainer.removeClass("hidden");
                });
            });
        })
        .fail((jqXHR)=> {

            if(jqXHR.status == 409) {

                alert("Compile process already running!");
            }
        });
    }

    render() {

        const { classes } = this.props;

        return (

            <ThemeProvider theme={mainTheme}>
            <div class="site-module-container vertical" style={{width: "80vw", height: "70vh", minHeight: "300px", maxHeight: "70vh", padding: "0px"}}>

                <div id="main-data-container" class="wizard-content-container hidden" style={{display: "flex", flexDirection: "column", width: "100%", height: "100%"}}>

                    <div id="settings-content-container" style={{display: "flex", flexDirection: "row", flexGrow: "3.5"}}>

                        <div id="modules-steps-area">

                            <div class="sub-module-container" style={{marginLeft: "3px", marginRight: "3px"}}>

                                <p class="h-main" style={{marginBottom: "10px"}}><span style={{color: "lightgrey"}}>Step 1. Select </span>Bootloader:</p>

                                <PartsArea selectable id="bootloaders-list" ref={this.bootloadersArea} />

                            </div>

                            <div style={{borderBottom: "1px solid lightgrey", marginTop: "12px", marginBottom: "10px"}}></div>

                            <div class="sub-module-container" style={{marginLeft: "3px", marginRight: "3px"}}>

                                <p class="h-main" style={{marginBottom: "10px"}}><span style={{color: "lightgrey"}}>Step 2. Select </span>Core:</p>

                                <PartsArea selectable id="cores-list" ref={this.coresArea} />

                            </div>

                            <div style={{borderBottom: "1px solid lightgrey", marginTop: "12px", marginBottom: "10px"}}></div>

                            <div class="sub-module-container" style={{marginLeft: "3px", marginRight: "3px"}}>

                                <p class="h-main" style={{marginBottom: "10px"}}><span style={{color: "lightgrey"}}>Step 3. Add </span>Modules:</p>

                                <PartsArea id="modules-list" ref={this.modulesArea} partsSearch={this.partsSearch} />

                            </div>

                        </div>

                        <div id="modules-steps-area-delimeter" style={{borderLeft: "1px solid lightgrey"}}></div>

                        <div style={{flexGrow: "4", paddingTop: "15px", flexBasis: "60vw"}}>

                            <p class="h-main" style={{marginLeft: "15px"}}>Modules repository</p>

                            <div style={{borderBottom: "1px solid lightgrey", marginTop: "10px"}}></div>

                            <PartsSearch id="modules-search" ref={this.partsSearch} partsArea={this.modulesArea} restPartsAddress="../rest/parts/modules" class="sub-module-container plames-modules-search" style={{position: "relative", boxSizing: "border-box"}} />

                        </div>

                    </div>

                    <div style={{borderBottom: "1px solid lightgrey"}}></div>

                    <div class="sub-module-container" style={{padding: "15px", marginLeft: "3px", marginRight: "3px", flexGrow: "0"}}>

                        <p class="h-main" style={{marginBottom: "10px"}}><span style={{color: "lightgrey"}}>Step 4. Finally</span> Build & Get!</p>

                        <div style={{display: "flex", flexDirection: "row"}}>

                            <button class="generate-button accent-button" onClick={this.beginGeneration}>GENERATE</button>

                        </div>

                    </div>
                
                </div>

                <div id="generation-content-container" class="wizard-content-container hidden">

                    <p class="h-main" style={{marginLeft: "15px", marginTop: "15px"}}>Build log</p>

                    <div style={{borderBottom: "1px solid lightgrey", marginTop: "10px"}}></div>

                    <div id="build-log-container" class="build-log-container">

                        <p id="build-log" class="build-log wait"></p>

                    </div>

                    <div style={{borderBottom: "1px solid lightgrey", marginTop: "10px"}}></div>

                    <BuildProgressBar ref={this.buildProgressBar} style={{position: "relative", height: "40", flexBasis: "80px", flexShrink: "0.0000001"}}/>

                </div>

                <div id="final-content-container" class="wizard-content-container">

                    <div style={{ width: "35%" }}>
                        
                        <ProductKeyViewer withLabel />

                        <button class="accent-button">DOWNLOAD</button>
                    
                    </div>

                </div>

            </div>
            </ThemeProvider>
        );
    }
}

class BuildProgressBar extends React.Component {

    constructor(props) {
        super(props);

        this.state = {

            steps: [],
            activeStep: null
        };
    }

    loadSteps() {

        $.ajax({

            url: "../ajax/request/steps",
            method: "GET",
            async: false,
            success: (steps)=> {

                steps = steps.filter(step => step.visible);
                steps.map((step, index) => step.index = index);

                this.setState({steps: steps, activeStep: 0});
            }
        });
    }

    startWaitStepChanges() {

        let me = this;

        this.waitStepChange(me);

        $.ajax({

            url: "../ajax/request/wait_build_end",
            method: "GET",
            timeout: 3600000,
            async: true
        })
        .done((success)=> {

            let lastStep = me.state.steps[me.state.activeStep];

            if(success) {
                
                lastStep.completed = true;
            }
            else {

                lastStep.error = true;
            }

            me.setState({});
        });
    }

    waitStepChange(me) {

        $.get("../ajax/request/wait_step_change", (step)=> {

            if(step.visible) {
                
                step = me.getStep(step.name);

                me.setState({activeStep: step.index});
            
                if(step.index+1 != me.state.steps.length) {

                    me.waitStepChange(me);
                }
            }
            else {

                me.waitStepChange(me);
            }
        });
    }

    getStep(name) {

        for(let index in this.state.steps) {

            let step = this.state.steps[index];

            if(step.name == name) {
                
                return step;
            }
        }

        return null;
    }

    render() {

        return (

            <Stepper alternativeLabel nonLinear activeStep={this.state.activeStep}>

                {this.state.steps.map((step)=> {

                    if(step.visible) {

                        return (

                            <Step key={step.name}>
                                <StepLabel error={step.error} completed={this.state.activeStep > step.index || step.completed}>{step.name}</StepLabel>
                            </Step>
                        );
                    }
                    else {

                        return null;
                    }
                })}

            </Stepper>
        );
    }
}

const StyledWizard = withStyles(styles)(Wizard);

ReactDOM.render(<StyledWizard />, document.querySelector("#react-body"));

new ClipboardJS(".btn");