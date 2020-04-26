/// <reference path="./plames_part.ts" />
import React from 'react';
import ReactDOM from 'react-dom';
import $ from "jquery";

import { makeStyles } from '@material-ui/core/styles';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepButton from '@material-ui/core/StepButton';
import Typography from '@material-ui/core/Typography';

let bootloadersArea = null;
let coresArea = null;
let modulesArea = null;

let loadBuildLogInterval = null;

async function init() {

    let PlamesPart = await import("./plames_part.js");

    let bootloadersList = $("#bootloaders-list");

    bootloadersArea = new PlamesPart.LabelsArea(bootloadersList, true);

    $.get("../rest/parts/bootloaders", (data)=> {

    	bootloadersArea.loadFromJson(data);
    })

    let coresList = $("#cores-list");

    coresArea = new PlamesPart.LabelsArea(coresList, true);

    $.get("../rest/parts/cores", (data)=> {

    	coresArea.loadFromJson(data);
    })

    let modulesList = $("#modules-list");

    modulesArea = new PlamesPart.LabelsArea(modulesList, false);
        modulesArea.setTextOnEmpty("Please add modules from repository!");

    $("#modules-search").load("../resources/assembler/wizard/htmls/plames_modules_search.html");

    let PlamesModulesSearch = await import("./plames_modules_search.js"); 

    let modulesSearch = new PlamesModulesSearch.ModulesSearch($("#modules-search"))

    modulesSearch.setOnClick((moduleLabel)=> {

        let suspectModule = moduleLabel.suspectModule;

        modulesSearch.removeModule(moduleLabel);
        modulesSearch.ignoreList.push(suspectModule.id);

        modulesArea.createLabel(suspectModule);
    });

    $.get("../rest/parts/modules", (data)=> {

        modulesSearch.loadFromJsonArray(data);
    })

    $(document).bind("mousedown", (event)=> {
        
        let menu = $(".part-context-menu");

        if($(event.target).closest(".menu-item").prop("class") != undefined) {

            let menuItem = $(event.target).closest(".menu-item").first();
            
            if(menuItem.attr("data-action") == "remove") {

                PlamesPart.context_menu_area.removeLabel(PlamesPart.context_menu_part);
                modulesSearch.ignoreList.splice(modulesSearch.ignoreList.indexOf(PlamesPart.context_menu_part.id), 1);
                
                $.get("../rest/parts/modules", (data)=> {

                    modulesSearch.loadFromJsonArray(data);
                })
            }
        }

        menu.hide(100);
    });
}

async function beginGeneration() {
    
    let settingsContainer = $("#settings-content-container");
    let generationContainer = $("#generation-content-container");

    $.ajax({

        url: "../ajax/request/create",
        method: "POST",
        headers: {

            "Content-Type": "application/json"
        },
        data: JSON.stringify({

            bootloader: bootloadersArea.selectedPart,
            core: coresArea.selectedPart,
            modules: modulesArea.parts
        })
    })
    .done((data)=> {

        settingsContainer.animate({"opacity": "0"}, 500, "swing", ()=> {

            settingsContainer.css("display", "none");
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
    })
    .fail((jqXHR)=> {

        if(jqXHR.status == 409) {

            alert("Compile process already running!");
        }
    });
}

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

class BuildProgressBar extends React.Component {

    constructor(props) {
        super(props);

        this.state = {

        };
    }

    render() {

        return (

            <Stepper alternativeLabel nonLinear activeStep={}>

            </Stepper>
        );
    }
}