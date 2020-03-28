/// <reference path="./plames_part.ts" />

async function init() {

    let PlamesPart = await import("./plames_part.js");

    let bootloadersList = $("#bootloaders-list");

    let testArea = new PlamesPart.LabelsArea(bootloadersList);

    testArea.createLabel(new PlamesPart.Part("Test"));
}