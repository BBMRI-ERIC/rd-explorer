<#include "molgenis-header.ftl">
<#include "molgenis-footer.ftl">

<#assign css=['select2.css', 'bootstrap-datetimepicker.min.css', 'molgenis-form.css']>
<#assign js=['handlebars.min.js', 'ace/src-min-noconflict/ace.js', 'jquery.validate.min.js', 'bootstrap-datetimepicker.min.js', 'select2.min.js', 'scripts.js']>

<@header css js/>

<div class="container-fluid">
	<div class="row">
		<h3>Scripts <@hasPermission entityName='script' permission="WRITE"><a id="create-script-btn" href="#" style="margin:30px 10px"><img src="/img/new.png"></a></@hasPermission></h3>
		<table class="table table-condensed table-bordered">
			<thead>
				<tr>
					<@hasPermission entityName='script' permission="WRITE">
						<th class="edit-icon-holder"></th>
					</@hasPermission>
					<@hasPermission entityName='script' permission="WRITE">
						<th class="edit-icon-holder"></th>
					</@hasPermission>
					<th>Name</th>
					<th>Type</th>
					<th>Result file extension</th>
					<th>Parameters</th>
					<th>Execute</th>
				</tr>
			</thead>
			<tbody>
			<#if scripts?has_content>
				<#list scripts as script>
				<tr>
					<@hasPermission entityName='script' permission="WRITE">
						<td><a href="#" class="edit-script-btn"><img src="/img/editview.gif"></a></td>
					</@hasPermission>
					<@hasPermission entityName='script' permission="WRITE">
						<td><a href="#" class="delete-script-btn"><img src="/img/delete.png"></a></td>
					</@hasPermission>
					<td class="name">${script.name!}</td>
					<td>${script.type!}</td>
					<td>${script.resultFileExtension!}</td>
					<td class="parameters">
						<#if script.parameters?has_content>
							<#list script.parameters as parameter>
								${parameter}<#if parameter_has_next>,</#if>
							</#list>
						</#if>
					</td>
					<td><a href="#" data-hasAttributes="${script.parameters?has_content?string("true","false")}" class="icon-large icon-refresh execute"></a></td>
				</tr>
				</#list>
			</#if>
			</tbody>
		</table>
	</div>
	<div class="row">
		<h3>Parameters <@hasPermission entityName='scriptparameter' permission="WRITE"><a id="create-scriptparameter-btn" href="#" style="margin:30px 10px"><img src="/img/new.png"></a></@hasPermission></h3>
		<table class="table table-condensed table-bordered" style="width: 25%">
			<thead>
				<tr>
					<th>Name</th>
				<tr>
			</thead>
			<tbody>
			<#if parameters?has_content>
				<#list parameters as parameter>
				<tr>
					<td class="name">${parameter.name!}</td>
				</tr>
				</#list>
			</#if>
			</tbody>	
		</table>	
	</div>
</div>

<div class="modal fade medium" id="parametersModal" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">				
	      	<div class="modal-header">
	        	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        	<h4 class="modal-title">Parameters</h4>
	     	</div>
	      	<div class="modal-body">
				<form id="parametersForm" class="form-horizontal"></form>
	      	</div>
	      	<div class="modal-footer">
	        	<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        	<button id="runWithParametersButton" class="btn btn-primary">Run</button>
	      	</div>
	    </div>
	</div>
</div>

<div class="modal fade medium" id="formModal" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">				
	      	<div class="modal-header">
	        	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        	<h4 class="modal-title" id="formModalTitle"></h4>
	     	</div>
	      	<div class="modal-body">
				<div id="controlGroups"></div>
	      	</div>
	      	<div class="modal-footer">
	        	<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        	<button id="submitFormButton" class="btn btn-primary">Save</button>
	      	</div>
	    </div>
	</div>
</div>

<script id="parameters-template" type="text/x-handlebars-template">
	{{#each parameters}}
		<div class="form-group">
			<label class="col-md-3 control-label">{{name}}</label>
			<div class="col-md-9">
    			<input type="text" name="{{name}}" value="" class="required">
    		</div>
		</div>	
	{{/each}}
</script>
<@footer/>