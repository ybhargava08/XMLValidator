xmlValidatorApp.directive('fileDragDrop',['$compile','$rootScope',function($compile,$rootScope){
	
	function link(scope,elem,attrs) {
		
		angular.element('body').on('dragover dragenter dragleave drop',function(e){
			e.stopPropagation();
			e.preventDefault();
		});
		
		elem.on('dragover',function(e){
				e.originalEvent.dataTransfer.effectAllowed = 'copy';
		});
		
		elem.on('dragenter',function(e){
			e.originalEvent.dataTransfer.dropEffect = 'copy';
			e.originalEvent.dataTransfer.effectAllowed = 'copy';
			showHideAnchorLink(attrs,false);
				$(e.target).removeClass("fileUpload").addClass("fileUploadActive");
			
		});
		
		elem.on("dragleave",function(e){
			showHideAnchorLink(attrs,true);
				$(e.target).addClass("fileUpload").removeClass("fileUploadActive");

		});
		
		elem.on('drop',function(e){
			showHideAnchorLink(attrs,true);
			if($rootScope.messagelist){
				$rootScope.messagelist.length=0;
				$rootScope.$apply();
			}
			 
				$(e.target).addClass("fileUpload").removeClass("fileUploadActive");
				var files = e.originalEvent.dataTransfer.files;
				fileProcessing(files);
			
			
		});
		
		//for direct file upload
		
		elem.off('change').on('change',function(e){
			var files = e.target.files;
			if($rootScope.messagelist){
				$rootScope.messagelist.length=0;
				$rootScope.$apply();
			}
			fileProcessing(files);
		});
		
		
		function fileProcessing(files){
			
			var fileObjArray =[];
			if(attrs.filefor && 'schema'===attrs.filefor && scope.uploadedschemafiles){
				fileObjArray = scope.uploadedschemafiles;
			}else if(attrs.filefor && 'xml'===attrs.filefor && scope.uploadedxmlfiles){
				fileObjArray = scope.uploadedxmlfiles;
			} 
			//console.log(JSON.stringify(fileObjArray)+" file list size: "+fileObjArray.length);
			//console.log("file obj array length: "+fileObjArray.length+" "+attrs.allowedextn+" "+attrs.filefor);
			angular.forEach(files, function(file) {
				
			 if(!checkFileAlreadyUploaded(fileObjArray,file.name) && checkFilExtn(attrs,file.name) && checkNoofFilesUploaded(attrs,fileObjArray)){
			 //  console.log("before reader");
				var reader = new FileReader();
				reader.onload = function(event) {
					scope.$apply(function(){
							var newFile = {
									   origfile: file,
									   name: file.name,
				                      type: file.type,
				                      size: file.size 
				                      };
							fileObjArray.push(newFile);	
					});
					
				}
				reader.readAsDataURL(file);
			  }	
			});
			if(attrs.filefor && 'schema'===attrs.filefor){	
				//console.log("adding schemas to schemalist");
			    scope.uploadedschemafiles = fileObjArray;
			}else if(attrs.filefor && 'xml'===attrs.filefor){	
			    scope.uploadedxmlfiles = fileObjArray;
			}
		}
		
		function checkFileAlreadyUploaded(fileArray,filename) {
			for(i=0;i<fileArray.length;i++) {
				var file = fileArray[i];
				//console.log("while comparing: "+file.name.toLowerCase()+" to-> "+filename.toLowerCase());
				if(file.name.toLowerCase() == filename.toLowerCase()) {
					return true;
				}
			}
			return false;
		}
		
		function showHideAnchorLink(attrs,isHidden) {
			if(attrs.filefor && 'xml'===attrs.filefor) {
				$rootScope.$broadcast('xmlLink',isHidden);
			}else if(attrs.filefor && 'schema'===attrs.filefor) {
				$rootScope.$broadcast('schemaLink',isHidden);
			}
		}
		
		function checkFilExtn(attrs,filename) {
			if(attrs.allowedextn.indexOf(filename.substring(filename.length-3))>=0 || 
					attrs.allowedextn.indexOf(filename.substring(filename.length-4))>=0){
				return true;
			}
			  console.log("extn not uploaded: "+filename);
			    var data = {
			    		msg : 'incorrectextn'+attrs.filefor,		
			    };
				$rootScope.$broadcast('errormsg',data);
			
			return false;
		}
		
		function checkNoofFilesUploaded(attrs,filearray) {
			if(attrs.filefor==='xml' && filearray && filearray.length>1){
				return false;
			}
			return true;
		}
	}
	
	return {
		restrict: 'A',
		scope : {
			uploadedschemafiles:'=',
			uploadedxmlfiles:'=',
			allowedextn:'=',
			filefor:'=',
		},
		link:link,
	};
}]);
