/**
 * Created by mathabaws on 6/1/15.
 */

// LoadLinkView.js
//
// Load a specific view from a model that has a link specified for it.  This is similar to LoadModel.js but without the
// pre-canned set of models.  This one will be passed a model URN and view GUID.
//
// Jim Awe
// Autodesk, Inc.

var _viewer = null;     // the viewer

// setup for STAGING
/*var _viewerEnv = "AutodeskStaging";
 var _myAuthToken = new MyAuthToken("STG");*/

// setup for PRODUCTION
var _viewerEnv = "AutodeskProduction";
var _myAuthToken = null;
var modelUrn = null;

function getCurrentPagePathForLink() {
    var pathStr = window.location.pathname;
    pathStr = pathStr.slice(0, pathStr.lastIndexOf("/"));
    var linkStr = window.location.protocol + "//" + window.location.host + pathStr;
    return linkStr;
}

// we add a custom toolbar to the upper right so that we brand the viewer in this shared context
// and so we can provide a link back to "home"
function addTrackbackToolbar(parent) {
    var div = window.document.createElement("div");

    parent.appendChild(div);

    div.style.top = '90px';
    div.style.left = "10px";
    div.style.zIndex = "2";
    div.style.position = "absolute";

    var toolbar = new Autodesk.Viewing.UI.ToolBar(div);

    var toolbar = new Autodesk.Viewing.UI.ToolBar('quickshare-toolbar');
    var controlGroup = new Autodesk.Viewing.UI.ControlGroup('quickshare-control-group');

    var button = new Autodesk.Viewing.UI.Button('quickshare-linkback');
    button.icon.style.backgroundImage = "url(./images/adsk.24x24.png)";
    button.setToolTip("Back to QuickShare page");
    button.onClick = function (e) {
        window.open(getCurrentPagePathForLink(), '_blank');    // The link back button goes to the main web site page where they do the original sharing
    };

   // controlGroup.addControl(button);
    //toolbar.addControl(controlGroup);

    //div.appendChild(toolbar.container);
}


// initialize the viewer into the HTML placeholder
function initializeViewer() {

    console.assert(_viewer === null);    // we always start from a fresh page

    var viewerElement = document.getElementById("viewer");  // placeholder in HTML to stick the viewer

    _viewer = new Autodesk.Viewing.Private.GuiViewer3D(viewerElement, {});

    var retCode = _viewer.initialize();
    if (retCode !== 0) {
        alert("ERROR: Couldn't initialize viewer!");
        console.log("ERROR Code: " + retCode);      // TBD: do real error handling here
    }
}


// load a specific document into the intialized viewer
function loadDocument(urnStrModel, viewGUID, addEmbedToolbar) {

    if (!urnStrModel || (0 === urnStrModel.length)) {
        alert("You must specify a URN!");
        return;
    }
    var fullUrnStr = "urn:" + urnStrModel;

    Autodesk.Viewing.Document.load(fullUrnStr, function (document) {

        initializeViewer();

        if (viewGUID === null) {   // if they didn't pass in a specific view, try to load a default view
            // get all the 3D and 2D views (but keep in separate arrays so we can differentiate in the UX)
            var views3D = Autodesk.Viewing.Document.getSubItemsWithProperties(document.getRootItem(), {
                'type': 'geometry',
                'role': '3d'
            }, true);
            var views2D = Autodesk.Viewing.Document.getSubItemsWithProperties(document.getRootItem(), {
                'type': 'geometry',
                'role': '2d'
            }, true);

            // load up first 3D view by default
            if (views3D.length > 0) {
                loadView(document, views3D[0]);
            }
            else if (views2D.length > 0) {
                loadView(document, views2D[0]);
            }
            else {
                assert("ERROR: Can't find any default Views in the current model!");
            }
        }
        else {
            // retrieve the specific view that they are referencing
            var initialView = Autodesk.Viewing.Document.getSubItemsWithProperties(document.getRootItem(), {'guid': viewGUID}, true);
            console.assert(initialView.length == 1);

            if (initialView.length == 1) {
                loadView(document, initialView[0]);
            }
            else {
                alert("Can't find a View with that GUID!");
            }
        }

        // if we are being used in a context where we want a Trackback link and branding, add it now
        if (addEmbedToolbar) {
            var parent = window.document.getElementById(_viewer.clientContainer.id);
            addTrackbackToolbar(parent);
        }

    }, function (errorCode, errorMsg) {
        alert('Load Error: ' + errorMsg);
    });
}

// for now, just simple diagnostic functions to make sure we know what is happing
function loadViewSuccessFunc() {
    console.log("Loaded viewer successfully with given asset...");
}

function loadViewErrorFunc() {
    console.log("ERROR: could not load asset into viewer...");
}

// load a particular viewable into the viewer
function loadView(document, viewObj) {
    var path = document.getViewablePath(viewObj);
    console.log("Loading view URN: " + path);
    _viewer.load(path, document.getPropertyDbPath(), loadViewSuccessFunc, loadViewErrorFunc);
}

// wrap this in a simple function so we can pass it into the Initializer options object
function getAccessToken() {
    return _myAuthToken;
}

function setAccessToken(authtoken) {
    _myAuthToken = authtoken;
}


// get the arguments passed as part of the URL
function getUrlVars() {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
        vars[key] = value;
    });
    return vars;
}

// called when HTML page is finished loading, trigger loading of default model into viewer
function loadLinkedModel(urn, auth, guid) {
    var urlVars = getUrlVars();

    var modelID = btoa(urn);
    //set it globaly
    modelUrn = modelID;
    var viewGUID = null;
    var token = auth.replace('"', '').replace('"', '');

    setAccessToken(token);

    var options = {};
    options.env = _viewerEnv;                // AutodeskProduction, AutodeskStaging, or AutodeskDevelopment (set in global var in this project)
    options.getAccessToken = getToken;
    options.refreshToken = getToken;
    var addEmbedToolbar = true;
    Autodesk.Viewing.Initializer(options, function () {
        loadDocument(modelID, viewGUID, addEmbedToolbar);
    });

    function getToken() {
        console.log("TOKENN: " + token.replace('"', '').replace('"', ''));
        return token.replace('"', '').replace('"', '');
    }
}

//Export to img file

function exportToImg() {
    var img = _viewer.getScreenShot();
    console.log(img);
    $('#imageDownloader').attr('href',img);
    //window.location.replace(img);
}