export class ModulesSearch {

	//jquery
	//suspectsContainer
	//searchDataContainer
	//suspectModules
	//ignoreList
	//onClick

	constructor(jquery) {
			
		this.jquery = jquery;
		this.moduleLabels = new Array();
		this.ignoreList = new Array();

		let jquerySearchDataContainer = jquery.find(".search-data-container").first();
		this.searchDataContainer = new SearchDataContainer(jquerySearchDataContainer, this);

		let jquerySuspectsContainer = jquery.find(".suspects-container").first();
		this.suspectsContainer = new SuspectsContainer(jquerySuspectsContainer, this);
	}

	loadFromJsonArray(jsonModules) {

		this.clear();

		for(let index in jsonModules) {

			let jsonModule = jsonModules[index];
		
			if(this.ignoreList.includes(jsonModule.id)) continue;

			let suspectJqueryContainer = this.suspectsContainer.getSuspectJqueryContainer();

			let suspectModule = new ModuleLabel(suspectJqueryContainer, jsonModule, this);

			this.moduleLabels.push(suspectModule);
		}
	}

	setOnClick(onClick) {

		this.onClick = onClick;
	}

	getLabel(targetModule) {

		for(let moduleLabel in this.moduleLabels) {

			if(moduleLabel.suspectModule.id == targetModule.id) {

				return moduleLabel;
			}
		}

		return null;
	}

	removeModule(targetModule) {

		if(!(targetModule instanceof ModuleLabel)) {

			targetModule = this.getLabel(targetModule);
		}

		this.moduleLabels.splice(this.moduleLabels.indexOf(targetModule), 1);
        
		targetModule.jquery.remove();
	}

	clear() {

		this.moduleLabels = new Array();
		this.suspectsContainer.jquery.find(".suspect-module").not(".prototype").remove();
	}
}

export class SearchDataContainer {

	//jquery
	//modulesSearch

	constructor(jquery, modulesSearch) {

		this.jquery = jquery;
		this.modulesSearch = modulesSearch;
		
		let self = this;

		this.jquery.find(".name-field").on("input", ()=> {
		
			let searchQuery = self.getSearchQuery();

			$.get("../rest/parts/modules"+searchQuery, (jsonModules)=> {

				modulesSearch.loadFromJsonArray(jsonModules);
			});

		});
	}

	getSearchQuery() {

		let name = this.jquery.find(".name-field").first().val();
		
		return "?name="+name;
	}
}

export class SuspectsContainer {

	// jquery
	// modulesSearch

	constructor(jquery, modulesSearch) {

		this.jquery = jquery;
		this.modulesSearch = modulesSearch;
	}

	getSuspectJqueryContainer() {

		let result = this.jquery.find(".prototype").first().clone().removeClass("prototype");
		
		result.appendTo(this.jquery)

		return result;
	}
}

export class ModuleLabel {

	constructor(jquery, suspectModule, modulesSearch) {

		this.jquery = jquery;
		this.suspectModule = suspectModule;
		this.modulesSearch = modulesSearch;

		jquery.find(".name").html(suspectModule.name);
		jquery.find(".description").html(suspectModule.description);

		jquery.click((event)=> this.modulesSearch.onClick(this));
	}
}