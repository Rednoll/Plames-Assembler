export class LabelsArea {

    //jquery: JQuery;

    //partLabels: PartLabel[] = new Array();

    constructor(jquery) {

        this.jquery = jquery;
        this.partLabels = new Array();
        
        $.ajax({

            url: "../resources/wizard/htmls/plames_parts_area.html",
            type: "GET",
            dataType: "HTML",
            async: false
        })
        .done((data)=> {
            
            jquery[0].innerHTML = data
        });
    }

    createLabel(part) {
        
        let jqueryLabel = this.jquery.find(".prototype").first().clone()
            jqueryLabel.removeClass("prototype")
            
        let partLabel = new PartLabel(part, jqueryLabel);

        jqueryLabel.appendTo(this.jquery);

        this.partLabels.push(partLabel);

        return partLabel;
    }

    getLabel(part) {
        
        let result;

        this.partLabels.forEach((label)=> {
            
            if(label.part == part) {
                
                result = label;
            }
        })

        return result;
    }

    removeLabel(partLabel) {
        
        if(partLabel instanceof Part) {

            partLabel = this.getLabel(partLabel);
        }

        this.partLabels.filter((value) => value != partLabel)
        
        partLabel.dispose();
    }
}

export class PartLabel {

    //jquery: JQuery;

    //part: Part;

    constructor(part, jqueryLabel) {
        
        this.part = part;
        this.jquery = jqueryLabel;

        jqueryLabel.find(".name").val(this.part.name);
    }

    dispose() {

        this.jquery.remove();
    }
}

export class Part {

    //name: string;

    constructor(name) { 

        this.name = name;
    }
}
