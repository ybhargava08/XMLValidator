var xmlValidatorApp = angular.module("XmlValidateApp", ['ngMaterial']);

xmlValidatorApp.controller('XmlValidateController',['$scope','$timeout','$window','XmlValidateService',function($scope,$timeout,$window,XmlValidateService){
	
	$scope.xmlLinkShow=true;
	$scope.schemaLinkShow=true;
	$scope.isloading = false;
	var animationTimer,timedMsgTimer;
    $scope.validationmethod = 'Validate With Files';
    $scope.isDepSchemaloading = false;
    $scope.isValidationInProgress=false;
	
	$scope.validateWithFiles = function($event) {
		console.log("this is executed -> validateWithFiles");
		if($scope.schemafilelist && $scope.schemafilelist.length>0 ) {
			if($scope.xmlfilelist && $scope.xmlfilelist.length>0){
				 $scope.isValidationInProgress=true;
				clearMsgResult();
			$scope.isloading=true;
			XmlValidateService.uploadfiles($scope.schemafilelist,$scope.xmlfilelist).then(function(data){
				var returndata = JSON.parse(data);
				showMiscMessages(returndata);
				 $scope.isValidationInProgress=false;
			});
			
			}else{
				animateAddRemoveClass('#dragdropxml','animateEmpty',1040);
			}	
		}else{
			animateAddRemoveClass('#dragdropschema','animateEmpty',1040);
		}
		
	};
	
	$scope.validateWithUrl = function($event) {
		$scope.incorrectinputschemamsg='';
		if($scope.schemaUrl && $scope.schemaUrl.toLowerCase().startsWith('http://') && 
				($scope.schemaUrl.toLowerCase().endsWith('.xsd') || $scope.schemaUrl.toLowerCase().endsWith('?wsdl'))) {
			if($scope.xmlfilelist && $scope.xmlfilelist.length>0){
				var url = $scope.schemaUrl.replace(/[\s]/g, '');
				 $scope.isValidationInProgress=true;
				clearMsgResult();
				$scope.isloading=true;
				XmlValidateService.validateWithUrl(url,$scope.xmlfilelist).then(function(data){
					var returndata = JSON.parse(data);
					showMiscMessages(returndata);
					 $scope.isValidationInProgress=false;
				});
				
			}else{
				animateAddRemoveClass('#dragdropxml','animateEmpty',1040);
			}
		}else{
			animateAddRemoveClass('#schemaUrlInput','animateEmpty',1040);
		}
	}
	
	$scope.checkValidSchemaUrl = function() {
		if(!($scope.schemaUrl.toLowerCase().startsWith('http://') && 
				($scope.schemaUrl.toLowerCase().endsWith('.xsd') || $scope.schemaUrl.toLowerCase().endsWith('?wsdl')))) {
			var data = {
				msg: 'incorrectinputschemamsg',	
			};
			updateMissingSchemaList(data);
		}
	}
	
	$scope.removefile = function($index,filetype){
		if('schema' === filetype && $scope.schemafilelist) {
			 $scope.missingschemamsg = '';
			 $scope.schemafilelist.splice($index,1);
			 console.log("schemalist after splice: "+$scope.schemafilelist+" "+$scope.schemafilelist.length);
		}else if('xml' === filetype && $scope.xmlfilelist) {
			 $scope.xmlfilelist.splice($index,1);
			 console.log("xmlist after splice: "+$scope.xmlfilelist+" "+$scope.xmlfilelist.length);
		}
		
	}
	
	/*$scope.flipValidationMethod = function() {
		$scope.isValidateWithUrl=!$scope.isValidateWithUrl;
		
		if($scope.isValidateWithUrl && $scope.schemafilelist) {
			$scope.schemafilelist.length =0;
		}else if(!$scope.isValidateWithUrl && $scope.schemaUrl) {
			$scope.schemaUrl = '';
		}
	}*/
	
	$scope.remove = function(type) {
		if(type && 'missingschemas'==type) {
		   $scope.missingschemamsglist.length=0;	
		   $scope.missingschemamsg ='';
		}
	}
	
	$scope.triggerupload = function(type) {
		if('schema'== type) {
			angular.element('#schemafileuploadhidden').trigger('click');
		}else if('xml'== type) {
			angular.element('#xmlfileuploadhidden').trigger('click');
		}
	}
	
	$scope.validateSchemas = function() {
	    if($scope.schemafilelist) {
	    	$scope.isDepSchemaloading = true;
	    	XmlValidateService.validateschemas($scope.schemafilelist).then(function(data){
				var returndata = JSON.parse(data);
				updateMissingSchemaList(returndata);
				$scope.isDepSchemaloading = false;
			});
	    	
	    }	
	}
	
	$scope.$on('schemaLink', function(event,data) {
		 $scope.$apply(function(){
			 $scope.schemaLinkShow = data;
		 });
	});
	
	$scope.$on('xmlLink', function(event,data) {
		 $scope.$apply(function(){
			 $scope.xmlLinkShow = data;
		 });
	});
	
	$scope.$on('errormsg', function(event,data) {
		 $scope.$apply(function(){
			 updateMissingSchemaList(data);
		 });
	});
	
	function updateMissingSchemaList(data) {
		
		console.log('got data: '+data.msg);
		if(data.msg==='Files Missing') {
			$scope.missingschemamsg='Error: Following Schemas are missing. Please add them';
			$scope.missingschemamsglist = data.data;
			scrollToElement('.missingschemas');
		}else if(data.msg==='Error') {
			$scope.missingschemamsg='Error Occured: '+data.data;
			timedMsg(data,6000);
			scrollToElement('.missingschemas');
		}else if(data.msg==='Valid') {
			$scope.missingschemamsg=data.data;
			timedMsg(data,6000);
			scrollToElement('.missingschemas');
		}else if(data.msg == 'incorrectextnschema') {
			$scope.missingschemamsg='Error: Only file extn xsd / wsdl are allowed. Some files were not uploaded';
			timedMsg(data,6000);
			scrollToElement('.missingschemas');
		}else if(data.msg == 'incorrectextnxml') {
			$scope.errormsgxml='Error: Only file extn xml is allowed. Some files were not uploaded';
			timedMsg(data,6000);
			scrollToElement('.errormsgxml');
		}else if(data.msg=='incorrectinputschemamsg') {
			$scope.incorrectinputschemamsg='Error: Url is not in desired format-> http://abc.xsd or http://abc?wsdl';
			timedMsg(data,6000);
			scrollToElement('.incorrectinputschemamsg');
		}
	}
	
	function timedMsg(data,time) {
		$timeout.cancel(timedMsgTimer);
		timedMsgTimer = $timeout(function(){
			if(data.msg==='Error' || data.msg==='Valid' || data.msg == 'incorrectextnschema') {
				$scope.missingschemamsg='';
			}else if(data.msg == 'incorrectextnxml') {
				$scope.errormsgxml='';
			}else if(data.msg=='incorrectinputschemamsg') {
				$scope.incorrectinputschemamsg='';
			}
		},time);
	}
	
	function scrollToElement(ele) {
		$window.scrollTo(0, angular.element(ele).offsetTop); 
	}
	
	function showMiscMessages(data) {
		if(data.msg==='Files Missing') {
			updateMissingSchemaList(data);
		}else if(data.msg==='Error') {
			$scope.msginfo='Error Occured: '+data.data;
			animateAddRemoveClass('.messages','show',10000);			
		}else if(data.msg==='Valid') {
			$scope.msginfo=data.data;
			animateAddRemoveClass('.messages','show',5000);
			//$scope.xmlfilelist.length=0;
			//$scope.schemafilelist.length=0;
		}
		$scope.isloading=false;
	}

	function animateAddRemoveClass(elem,classname,time) {
		   $timeout.cancel(animationTimer);
			angular.element(elem).addClass(classname);
			animationTimer =  $timeout(function(){
			   angular.element(elem).removeClass(classname);
		   },time);
	}
	
	function clearMsgResult() {
		$scope.msginfo='';
		if(animationTimer!==undefined) {
			$timeout.cancel(animationTimer);
		}
		angular.element('.messages').removeClass('show');
	}
	
}]);
