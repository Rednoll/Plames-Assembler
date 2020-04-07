export let context_menu_part = null;
export let context_menu_area = null;

export class LabelsArea {

    //jquery: JQuery;

    //partLabels: PartLabel[] = new Array();

    constructor(jquery, selectable) {

        this.jquery = jquery;
        this.partLabels = new Array();
        this.selectable = selectable

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

    loadFromJson(jsonArray) {

        for(let index in jsonArray) {

            let jsonPart = jsonArray[index]; 

            let part = Part.createFromJson(jsonPart);

            this.createLabel(part);
        }

        if(this.partLabels.length == 1) {

            this.selectPart(this.partLabels[0])
        }
    }

    selectPart(partLabel) {

        if(!this.selectable) return;

        if(!(partLabel instanceof PartLabel)) {

            partLabel = this.getLabel(partLabel);
        }

        this.jquery.find(".selected").removeClass("selected");

        partLabel.jquery.addClass("selected");
    
        this.selectedPart = partLabel.part
    }

    createLabel(part) {

        this.hideEmpty();
        
        let jqueryLabel = this.jquery.find(".prototype").first().clone()
            jqueryLabel.removeClass("prototype")
            
        let partLabel = new PartLabel(this, part, jqueryLabel);

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
        
        if(!(partLabel instanceof PartLabel)) {

            partLabel = this.getLabel(partLabel);
        }

        this.partLabels.splice(this.partLabels.indexOf(partLabel), 1);
        
        partLabel.dispose();

        if(this.partLabels.length == 0) {

            this.showEmpty();
        }
    }

    setTextOnEmpty(text) {

        this.jquery.find(".empty-container").find(".empty-text").html(text);
    }

    hideEmpty() {

        this.jquery.find(".empty-container").css({"display": "none"})
    }

    showEmpty() {
    
        this.jquery.find(".empty-container").css({"display": "inline-block"})
    }
}

export class PartLabel {

    //jquery: JQuery;

    //part: Part;

    constructor(area, part, jqueryLabel) {
        
        this.area = area;
        this.part = part;
        this.jquery = jqueryLabel;

        jqueryLabel.find(".name").html(part.name);
        jqueryLabel.find(".icon").attr("src", part.icon);
    
        jqueryLabel.click((event)=> {
            
            area.selectPart(part);
        });

        jqueryLabel.contextmenu((event)=> {

            event.preventDefault();

            context_menu_part = part;
            context_menu_area = area;

            $(".part-context-menu").finish().toggle(100).
            css({
                top: event.pageY + "px",
                left: event.pageX + "px"
            });
        });
    }

    dispose() {

        this.jquery.remove();
    }
}

export class Part {

    //name: string;

    constructor(id, name, icon) { 

        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    static createFromJson(json) {

        return new Part(json.id, json.name, json.icon)
    }
}
