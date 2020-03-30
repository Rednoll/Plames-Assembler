/// <reference path="./plames_part.ts" />

async function init() {

    let PlamesPart = await import("./plames_part.js");

    let bootloadersList = $("#bootloaders-list");

    let bootloadersArea = new PlamesPart.LabelsArea(bootloadersList, true);

    $.get("../rest/parts/bootloaders", (data)=> {

    	bootloadersArea.loadFromJson(data);
    })

    let coresList = $("#cores-list");

    let coresArea = new PlamesPart.LabelsArea(coresList, true);

    $.get("../rest/parts/cores", (data)=> {

    	coresArea.loadFromJson(data);
    })

    let modulesList = $("#modules-list");

    let modulesArea = new PlamesPart.LabelsArea(modulesList, true);
        modulesArea.setTextOnEmpty("Please add modules from repository!");

    $("#modules-search").load("../resources/wizard/htmls/plames_modules_search.html");

    let PlamesModulesSearch = await import("./plames_modules_search.js"); 

    let modulesSearch = new PlamesModulesSearch.ModulesSearch($("#modules-search"))

    $.get("../rest/parts/modules", (data)=> {

        modulesSearch.loadFromJsonArray(data);
    })
}