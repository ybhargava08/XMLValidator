xmlValidatorApp.service('XmlValidateService', ['$http',function($http){
	
	this.uploadfiles = function(schemafilelist,xmlfilelist) {
		var fd = new FormData();
		//console.log("filelist size: "+schemafilelist.length+" "+xmlfilelist.length);
		angular.forEach(schemafilelist, function(file) {
			fd.append('schemafiles',file.origfile);
		});
		
		angular.forEach(xmlfilelist, function(file) {
			fd.append('xmlfile',file.origfile);
		});
		
		var config = {
				transformRequest: angular.identity,
				transformResponse: angular.identity,
		        headers: {
		        	'Content-Type':undefined,
		        },
		};
		
		var url = "/uploadfiles";
		
		return $http.post(url,fd,config).then(function(response) {
			  return response.data;
		}).catch(function(reason) {
			var data = {
					msg: 'Error',
					data: reason,
				};
				return data;
			});
	}
	
	this.validateschemas = function(schemafilelist) {
		var fd = new FormData();
		angular.forEach(schemafilelist, function(file) {
			fd.append('schemafiles',file.origfile);
		});
		var config = {
				transformRequest: angular.identity,
				transformResponse: angular.identity,
		        headers: {
		        	'Content-Type':undefined,
		        },
		};
		var url  = "/validateschemas";
		return $http.post(url,fd,config).then(function(response) {
			  return response.data;
		}).catch(function(reason) {
			var data = {
					msg: 'Error',
					data: reason,
				};
				return data;
			});
	}
	
	this.validateWithUrl = function(schemaUrl,xmlfilelist) {
		var fd = new FormData();
		angular.forEach(xmlfilelist, function(file) {
			fd.append('xmlfile',file.origfile);
		});
		fd.append('schemaUrl',schemaUrl);
		var config = {
				transformRequest: angular.identity,
				transformResponse: angular.identity,
		        headers: {
		        	'Content-Type':undefined,
		        },
		};
		var url  = "/validateWithUrl";
		return $http.post(url,fd,config).then(function(response) {
			  return response.data;
		}).catch(function(reason) {
			var data = {
				msg: 'Error',
				data: reason,
			};
			return data;
		});
	}
	
}]);
