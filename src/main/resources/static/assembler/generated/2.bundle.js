(window.webpackJsonp=window.webpackJsonp||[]).push([[2],{108:function(e,n,t){"use strict";function r(e,n){if(!(e instanceof n))throw new TypeError("Cannot call a class as a function")}function i(e,n){for(var t=0;t<n.length;t++){var r=n[t];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}function s(e,n,t){return n&&i(e.prototype,n),t&&i(e,t),e}t.r(n),t.d(n,"ModulesSearch",(function(){return u})),t.d(n,"SearchDataContainer",(function(){return o})),t.d(n,"SuspectsContainer",(function(){return a})),t.d(n,"ModuleLabel",(function(){return c}));var u=function(){function e(n){r(this,e),this.jquery=n,this.moduleLabels=new Array,this.ignoreList=new Array;var t=n.find(".search-data-container").first();this.searchDataContainer=new o(t,this);var i=n.find(".suspects-container").first();this.suspectsContainer=new a(i,this)}return s(e,[{key:"loadFromJsonArray",value:function(e){for(var n in this.clear(),e){var t=e[n];if(!this.ignoreList.includes(t.id)){var r=this.suspectsContainer.getSuspectJqueryContainer(),i=new c(r,t,this);this.moduleLabels.push(i)}}}},{key:"setOnClick",value:function(e){this.onClick=e}},{key:"getLabel",value:function(e){for(var n in this.moduleLabels)if(n.suspectModule.id==e.id)return n;return null}},{key:"removeModule",value:function(e){e instanceof c||(e=this.getLabel(e)),this.moduleLabels.splice(this.moduleLabels.indexOf(e),1),e.jquery.remove()}},{key:"clear",value:function(){this.moduleLabels=new Array,this.suspectsContainer.jquery.find(".suspect-module").not(".prototype").remove()}}]),e}(),o=function(){function e(n,t){r(this,e),this.jquery=n,this.modulesSearch=t;var i=this;this.jquery.find(".name-field").on("input",(function(){var e=i.getSearchQuery();$.get("../rest/parts/modules"+e,(function(e){t.loadFromJsonArray(e)}))}))}return s(e,[{key:"getSearchQuery",value:function(){return"?name="+this.jquery.find(".name-field").first().val()}}]),e}(),a=function(){function e(n,t){r(this,e),this.jquery=n,this.modulesSearch=t}return s(e,[{key:"getSuspectJqueryContainer",value:function(){var e=this.jquery.find(".prototype").first().clone().removeClass("prototype");return e.appendTo(this.jquery),e}}]),e}(),c=function e(n,t,i){var s=this;r(this,e),this.jquery=n,this.suspectModule=t,this.modulesSearch=i,n.find(".name").html(t.name),n.find(".description").html(t.description),n.click((function(e){return s.modulesSearch.onClick(s)}))}}}]);