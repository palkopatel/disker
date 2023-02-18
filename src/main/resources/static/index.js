var token;

window.addEventListener('load', function() {
	$.ajax({
		url: 'token',
		type: 'get',
		dataType: 'text',
		contentType:'plain/text; charset=utf-8',
		success: function(resp) {
			token = resp;
			// if ('serviceWorker' in navigator) {
			// 	navigator.serviceWorker.register('file-explorer/file-explorer.js', { scope: './'});
			// }
		},
		error: function (_jqXHR, exception) {
			console.log('Failed to get token: ' + exception);
		}
	});
});

// self.addEventListener('fetch', function(event) {
// 	const newRequest = new Request(event.request, {
// 			method: 'GET',
// 			mode: 'cors',
// 			headers: { 'Authorization': 'OAuth ' + token }
// 	});
// 	return fetch(newRequest);
// });


//document.addEventListener('DOMContentLoaded',
window.addEventListener('load', function() {
	var elem = document.getElementById('filemanager');

	var options = {
		initpath: [
			[ '', 'Projects (/)', { canmodify: false } ]
		],
		tools: {
			item_checkboxes: true
		},

		onrefresh: function(folder, required) {
			// Optional:  Ignore non-required refresh requests.  By default, folders are refreshed every 5 minutes so the widget has up-to-date information.
//			if (!required)  return;

			// Maybe notify a connected WebSocket here to watch the folder on the server for changes.
			if (folder === this.GetCurrentFolder())
			{
			}

			var $this = this;
			var pathIds = folder.GetPathIDs();
			var pathId = pathIds[pathIds.length - 1]
			if (pathId === '') {
				pathId = '/';
			}
			var xhr = new this.PrepareXHR({
				url: 'disk/path',
				body: pathId,
				onsuccess: function(e) {
					var data = JSON.parse(e.target.response);
					console.log(data);
					if (data._embedded) {
						folder.SetEntries(data._embedded.items);
					} else if (required) {
						$this.SetNamedStatusBarText('folder', $this.EscapeHTML('Failed to load folder.  ' + data.error));
					}
				},
				onerror: function(e) {
					// Maybe output a nice message if the request fails for some reason.
//					if (required)  $this.SetNamedStatusBarText('folder', 'Failed to load folder.  Server error.');
					console.log(e);
				}
			});

			xhr.Send();
		},
		onopenfile: function(_folder, entry) {
			console.log('onopenfile');
			console.log(entry);
		},
		onrename: function(_renamed, _folder, entry, newname) {
			console.log('onrename');
			console.log(entry);
			console.log(newname);
		},
		onnewfolder: function(created, folder) {
			console.log('onnewfolder');
		},
		onnewfile: function(created, folder) {
			console.log('onnewfile');
		},
		ondelete: function(deleted, folder, ids, entries, recycle) {
			console.log('ondelete');
			console.log(folder);
			console.log(ids);
			console.log(entries);
			console.log(recycle);
		},
		oninitdownload: function(startdownload, folder, ids, entries) {
			console.log('oninitdownload');
			console.log(ids);
			console.log(entries);
			// Simulate network delay.
			setTimeout(function() {
				// Set a URL and params to send with the request to the server.
				var options = {};

				// Optional:  HTTP method to use.
//				options.method = 'POST';

				options.url = 'filemanager/';

				options.params = {
					action: 'download',
					path: JSON.stringify(folder.GetPathIDs()),
					ids: JSON.stringify(ids),
					xsrftoken: 'asdfasdf'
				};

				// Optional:  Control the download via an in-page iframe (default) vs. form only (new tab).
//				options.iframe = false;

				startdownload(options);
			}, 250);
		},			
	};

	var fe = new window.FileExplorer(elem, options);
});

