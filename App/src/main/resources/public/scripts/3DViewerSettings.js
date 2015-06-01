/**
 * Created by vlazar on 6/1/15.
 */

function intialize3D(urn, aToken) {
    token = aToken;

    console.log("URN: " + 'urn:' + btoa(urn))
    console.log("URN: " + urn);
    var options = {
        'document': 'urn:' + btoa(urn),
        'env': 'AutodeskProduction',
        'getAccessToken': getToken,
        'refreshToken': getToken,
    };

    var viewerElement = document.getElementById('viewer');
    var viewer = new Autodesk.Viewing.Viewer3D(viewerElement, {});

    Autodesk.Viewing.Initializer(options, function () {
        viewer.initialize();
        loadDocument(viewer, options.document);
    });

    function getToken() {
        console.log("TOKENN: " + token.replace('"', '').replace('"', ''));
        return token.replace('"', '').replace('"', '');
    }
}

function loadDocument(viewer, documentId) {
    // Find the first 3d geometry and load that.
    Autodesk.Viewing.Document.load(documentId, function (doc) {// onLoadCallback
        var geometryItems = [];
        geometryItems = Autodesk.Viewing.Document.getSubItemsWithProperties(doc.getRootItem(), {
            'type': 'geometry',
            'role': '3d'
        }, true);

        if (geometryItems.length > 0) {
            viewer.load(doc.getViewablePath(geometryItems[0]));
        }
    }, function (errorMsg) {// onErrorCallback
        alert("Load Error: " + errorMsg);
    });
}

function createViewerToolbarCanvas(htmlDivContainer) {

    _canvasToolbar = new Autodesk.Viewing.UI.ToolBar("lmvdbg_canvas_toolbar");

    // we need to add a class to this container so we can reposition where we want (see CSS class above)
    _canvasToolbar.addClass("lmvdbg_canvas_tb_positioner");
    //_canvasToolbar.addClass("notouch");   // TBD: make sure this bug if fixed

    var controlGroup = new Autodesk.Viewing.UI.ControlGroup("lmvdbg_ctrlgroup_bgcolors");
    var buttonSolid = new Autodesk.Viewing.UI.Button("lmvdbg_canvas_tb_button_solid");          // id passed in will trigger CSS above to style button (or you can add a CSS class)
    buttonSolid.icon.style.backgroundImage = "url(./res/icons/lmvdbgSolid.png)";
    buttonSolid.setToolTip("Change Viewer Background - Solid");
    buttonSolid.onClick = function (e) {
        _viewer.setBackgroundColor(250, 235, 215, 250, 235, 215);
    };
    controlGroup.addControl(buttonSolid);

    var buttonGradient = new Autodesk.Viewing.UI.Button("lmvdbg_canvas_tb_button_gradient");    // id passed in will trigger CSS above to style button
    buttonGradient.icon.style.backgroundImage = "url(./res/icons/lmvdbgGradient.png)";
    buttonGradient.setToolTip("Change Viewer Background - Gradient");
    buttonGradient.onClick = function (e) {
        _viewer.setBackgroundColor(0, 0, 255, 55, 248, 220);
    };
    controlGroup.addControl(buttonGradient);
    _canvasToolbar.addControl(controlGroup);

    htmlDivContainer.appendChild(_canvasToolbar.container);

}