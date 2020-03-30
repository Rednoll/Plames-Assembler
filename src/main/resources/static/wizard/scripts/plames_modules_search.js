export class ModulesSearch {

	//jquery
	//suspectsContainer
	//searchDataContainer
	//suspectModules

	constructor(jquery) {
			
		this.jquery = jquery;
		this.suspectModules = new Array();

		let jquerySearchDataContainer = jquery.find(".search-data-container").first();
		this.searchDataContainer = new SearchDataContainer(jquerySearchDataContainer, this);

		let jquerySuspectsContainer = jquery.find(".suspects-container").first();
		this.suspectsContainer = new SuspectsContainer(jquerySuspectsContainer);
	}

	loadFromJsonArray(jsonModules) {

		this.clear();

		for(let index in jsonModules) {

			let jsonModule = jsonModules[index];
		
			let suspectJqueryContainer = this.suspectsContainer.getSuspectJqueryContainer();

			let suspectModule = new SuspectModule(suspectJqueryContainer, jsonModule);
		
			this.suspectModules.push(suspectModule);
		}
	}

	clear() {

		this.suspectModules = new Array();
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

	constructor(jquery) {

		this.jquery = jquery;
	}

	getSuspectJqueryContainer() {

		let result = this.jquery.find(".prototype").first().clone().removeClass("prototype");
		
		result.appendTo(this.jquery)

		return result;
	}
}

export class SuspectModule {

	constructor(jquery, suspectModule) {

		this.jquery = jquery;
		this.suspectModule = suspectModule;

		jquery.find(".name").html(suspectModule.name);
		jquery.find(".description").html(suspectModule.description);
	}
}