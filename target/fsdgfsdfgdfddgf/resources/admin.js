S360 = {
	getContext: function() {
		return 'http://localhost:8080';
	}
};

function sayHello() {
	alert('Hello');
}
function nothing() {

}
function func(name) {
	var self = this,
		args = [].slice.call(arguments, 0);

	return function(item) {
		return item[name]();
	};
}
Ext.Msg.info = function(title, msg, fn) {
	return Ext.Msg.show({
		title: title || '',
		msg: msg || '',
		buttons: Ext.Msg.OK,
		fn: fn || nothing,
		icon: Ext.MessageBox.QUESTION
	});
};

Ext.Msg.error = function(title, msg, fn) {
	return Ext.Msg.show({
		title: title || '',
		msg: msg || '',
		buttons: Ext.Msg.OK,
		fn: fn || nothing,
		icon: Ext.MessageBox.ERROR
	});
};

S360.UploadedVideoGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(config) {
		Ext.applyIf(this, config);
		this.init();
		S360.UploadedVideoGrid.superclass.constructor.apply(this, arguments);
	},
	init: function() {
		this.store = this.createStore();
		this.colModel = this.createColModel();
		this.sm = this.createSelectionModel();
		// this.tbar = this.createToolBar();
		this._buttons = this.createButtons();
		this.bbar = this.createBottomBar(this.store, this._buttons);

		Ext.applyIf(this, {
			title: 'Videos',
			iconCls: 'icon-grid'
		});
	},
	createStore: function() {
		var proxy = new Ext.data.HttpProxy({
			url: S360.getContext(),
			api: {				
		        read    : S360.getContext() + '/video',
		        create  : S360.getContext() + '/video',
		        update  :  S360.getContext() + '/video',
		        destroy :  S360.getContext() + '/video'		       
		    }
		});

		var reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			successProperty: 'success',
			idProperty: 'id',
			root: 'data',
			record: 'item',
			messageProperty: 'message',  // <-- New "messageProperty" meta-data
			fields: [
				'id',
				'description',
				{name: 'createdAt', type: 'date', dateFormat: 'n/j h:ia'},
				{name: 'lastChange', type: 'date', dateFormat: 'n/j h:ia'}
			]});

		var writer = new Ext.data.JsonWriter({
		    encode: false   // <--- false causes data to be printed to jsonData config-property of Ext.Ajax#reqeust
		});

 		var store = new Ext.data.Store({
	        id: 'item',
	        restful: true,     // <-- This Store is RESTful
	        proxy: proxy,
	        reader: reader,
	        writer: writer,
	        autoLoad: true,
	        autoSave: false,
	        listeners: {
	        	exception: function(scope, type, action, options, response, arg) {	        		
	        		if (action == 'update') {
	        			if (!response.success) {
	        				arg.reject();	
	        			}	        			
	        		}

	        		if (!response.success) {
	        			Ext.Msg.error('Error', response.message);
	        		}
	        	}
	        }
	    });		
		
		
		return store;
	},
	createColModel: function() {
		return new Ext.grid.ColumnModel({
			defaults: {
				width: 120,
				sortable: true
			},
			columns: [
				{id: 'id', header: 'ID', width: 200, sortable: true, dataIndex: 'id'},
				{header: 'Description', dataIndex: 'description'},
				// instead of specifying renderer: Ext.util.Format.dateRenderer('m/d/Y') use xtype
				{
					header: 'Created At', width: 135, dataIndex: 'createdAt',
					xtype: 'datecolumn', format: 'M d, Y'
				},
				{
					header: 'Last Updated', width: 135, dataIndex: 'lastChange',
					xtype: 'datecolumn', format: 'M d, Y'
				}, {header: 'Video URL', dataIndex: 'videoURL' }
			]});
	},
	createSelectionModel: function() {
		return  new Ext.grid.RowSelectionModel({singleSelect:true});
	},
	createBottomBar: function(store, buttons) {
		return new Ext.PagingToolbar({
			store: store,       // grid and PagingToolbar using same store
			displayInfo: true,
			pageSize: 50,
			prependButtons: true,
			items: buttons.concat(['->'])
		});
	},
	createButtons: function() {
		var self = this;
		return [{
			xtype: 'button',
			text: 'Add',
			handler: this.onAdd.bind(this)
		},{
			xtype: 'button',
			text: 'Edit',
			handler: this._onEdit.bind(this)
		},{
			xtype: 'button',
			text: 'Delete',
			handler: this._onDelete.bind(this)
		}
		];
	},
	checkSelection: function(next) {
		var selection = this.getSelectionModel().getSelections();

		if (!selection.length) {
			Ext.Msg.info('Info', 'Please, select a row in the grid at first');
			return;
		}
		next.bind(this)(selection[0]);
	},
	onAdd: function(self) {

		this.addWnd.setTitle('Add New');
		this.addWnd.clear();
		this.addWnd.show();
	},
	_onEdit: function() {
		this.checkSelection(this.onEdit);
	},
	onEdit: function(selection) {
		this.addWnd.setTitle('Edit');
		this.editWnd.inject(selection);
		this.editWnd.show();
	},
	_onDelete: function() {
		this.checkSelection(this.onDelete);
	},
	onDelete: function(selection) {
		var self = this;
		Ext.Msg.confirm('Delete', 'Are you sure want to delete this record?', function (btn) {
			if (btn == 'yes') {
				//TODO: delete	
				self.store.remove(selection);
				self.store.save();				
			}
		});
	}
});

S360.AddVideoWnd = Ext.extend(Ext.Window, {
	title: 'Add',	
	waitMsgTarget: true,
	hidden: true,
	closeAction: 'hide',
	modal: true,
	width: 500,
	resizable: false,
    addForm: false,
	constructor: function(config) {
		Ext.applyIf(this, config);
		
		arguments.callee.superclass.constructor.apply(this, arguments);
		this.init();
	},
	init: function() {
		this.addButtons();

		this.fields = this.createFields();
		this.fieldForm = this.createForm(this.fields);
		this.add(this.fieldForm);
	},
	addButtons: function() {
		this.addButton('Cancel', this.hide, this);
		this.addButton('Clear',	sayHello, this);
		this.addButton('Submit', this.onSubmit, this);
	},
	createForm: function(items) {
		return new Ext.FormPanel({
			fileUpload: true,
			frame: true,
			height: 'auto',
			bodyStyle: 'padding: 20px 10px 15px 10px',
			defaults: {
				selectOnFocus: true,
				anchor: '97%',
				msgTarget: 'side'
			},
			items:items
		});
	},
	createFields: function() {
		return [{
			xtype: 'textfield',
			name: 'id',
			id: 'id',
			fieldLabel: 'id'
		}, {
			xtype: 'textarea',
			name: 'description',
			id: 'description',
			fieldLabel: 'Description'
		}, {
			xtype: 'datefield',
			name: 'createdAt',
			id: 'createdAt',
			fieldLabel: 'Created At'
		}, {
				xtype: 'fileuploadfield',
				name: 'video',
				id: 'video',
				emptyText: 'Select a video',
				fieldLabel: 'Video',
				buttonText: '',
				buttonCfg: {
					iconCls: 'upload-icon'
				}
			}
		];
	},
	clear: function() {
		this.fieldForm.items.each(func('enable'));
		this.fieldForm.items.each(function(item) {
			item.setValue('');
		});
	},
	inject: function(record) {
		this.record = record;
		//ID
		var id = this.fieldForm.items.map.id;
		id.setValue(record.get('id'));
		id.disable();

		//Description
		var description = this.fieldForm.items.map.description;
		description.setValue(record.get('description'));
		// Created At
		
		var createdAt = this.fieldForm.items.map.createdAt;
		createdAt.setValue(record.get('createdAt'));
		// Video
		
		var video = this.fieldForm.items.map.video;
		video.setValue(record.get('videoURL'));
		video.disable();

	},
	onSubmit: function() {
		var self = this;
		if (this.addForm) {
			this.fieldForm.getForm().submit({
	            url:S360.getContext() +'/video',
	            waitMsg:'Submitting Data...',
	            submitEmptyText: false,
	            success : self.onSuccessOrFail.bind(this),
	            failure : self.onSuccessOrFail.bind(this)
	        });
		} else {

			this.fieldForm.items.items.forEach(function(field) {
				this.record.set(field.name, field.getValue());
			}.bind(this));			
			this.record.store.save();//commit();
			this.hide();
		}
	},
	onSuccessOrFail: function(sender, action) {
		var result = action.result;
		
	    
	    if(result.success) {
	        	        
	        this.hide();
	        this.fireEvent('submited', this);
	        
	    } else { // put code here to handle form validation failure.
	    	Ext.Msg.error('Error', result.message);
	    }
	}
});
// S360.EditVideoWnd = 
// 

Ext.onReady(function(){	
	var addWnd = new S360.AddVideoWnd({
            addForm: true,
			listeners: {
				submited: function() {
					grid.store.reload({});
				}
			}
		});

    var editWnd = new S360.AddVideoWnd({
        listeners: {
            submited: function() {
                grid.store.reload({});
            }
        }
    });

	var grid = new S360.UploadedVideoGrid({
		region: 'center',
		layout: 'fit',
		url: URL,
		title: 'Test',
		addWnd: addWnd,
		editWnd: editWnd
	});

	var viewPort = new Ext.Viewport({
		layout: 'border',
		items: [grid]
	});
});