/// <reference path="./plames_part.ts" />

let bootloadersArea = null;
let coresArea = null;
let modulesArea = null;

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

    $("#modules-search").load("../resources/wizard/htmls/plames_modules_search.html");

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

    settingsContainer.animate({"opacity": "0"}, 500, "swing", ()=> {

        settingsContainer.css("display", "none");
        generationContainer.removeClass("hidden");

        $.ajax({

            url: "../ajax/request/create",
            method: "POST",
            dataType: "JSON",
            headers: {

                "Content-Type": "application/json"
            },
            data: JSON.stringify({

                partBootloader: bootloadersArea.selectedPart,
                partCore: coresArea.selectedPart,
                modules: modulesArea.parts
            })

        }).done(()=> {

            $.ajax({

                url: "../ajax/request/build",
                method: "GET"

            }).done((placeInQueue)=> {

                $("#compile-log").html("Please wait, place in queue: "+placeInQueue);

                
            });
        });
    });
}