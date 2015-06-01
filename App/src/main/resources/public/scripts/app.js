/**
 * Created by vlazar on 26/05/15.
 */

var app = angular.module('worxapp', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute',
    'file-uploader'
]);

//todo add authentication logic
app.config(function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/list.html',
        controller: 'ListCtrl'
    }).when('/createBucket', {
        templateUrl: 'views/createBucket.html',
        controller: 'CreateBucketCtrl'
    }).when('/projects/:id', {
        templateUrl: 'views/viewBucket.html',
        controller: 'ViewBucketCtrl'
    }).when('/uploadFile/:id', {
        templateUrl: 'views/uploadFile.html',
        controller: 'UploadFileCtrl'
    }).otherwise({
        redirectTo: '/'
    })
});

app.controller('ListCtrl', function ($scope, $http) {
    $http.get('/api/v1/projects').success(function (data) {
        $scope.projects = data;
    }).error(function (data, status) {
        console.log('Error ' + data)
    })

    $scope.projectStatusChanged = function (project) {
        console.log(project);
        $http.put('/api/v1/projects/' + project.id, project).success(function (data) {
            console.log('status changed');
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }
});

app.controller('CreateBucketCtrl', function ($scope, $http, $location) {
    $scope.createBucket = function () {
        console.log($scope.project);
        /*$http({
         url: '/api/v1/projects',
         method: 'POST',
         headers: { 'Content-Type': 'application/json' },
         data: {
         title: $scope.project.title,
         owner: $scope.project.owner,
         bucketKey: '666999666'
         }
         })*/
        $http.post('/api/v1/projects', $scope.project).success(function (data, status, headers) {
        }).success(function (data, status, headers) {
            console.log('DATA:' + data.toString());
            $http.get('/api/v1/lastProject').success(function (data) {
                $location.path('/projects/' + data.replace('"', '').replace('"', ''));
            })
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }
});

app.controller('ViewBucketCtrl', function ($scope, $http, $routeParams) {
    $scope.projectId = $routeParams.id;

    $http.get('/api/v1/projects/' + $scope.projectId).success(function (data) {
        $scope.project = data;
        console.log('PROJECT: ' + data);
        $http.get('/api/v1/models/child/' + $scope.project.id).success(function (data) {
            console.log('MODELS: ' + data);
            $scope.models = data;
            $http.get('/api/v1/models/assemblies/' + $scope.project.id).success(function (data) {
                console.log('ASSEMBLIES: ' + data);
                $scope.assemblies = data;
            }).error(function (data, status) {
                console.log('Error ' + data)
            })
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }).error(function (data, status) {
        console.log('Error ' + data)
    })
});

app.controller('UploadFileCtrl', function ($scope, $route, $http, $routeParams, $location, FileUploader) {
    //variable for differing upload to local server or autodesk server
    $scope.source = true;
    //parameter is bucket ID
    $scope.fileId = $routeParams.id;

    $scope.refresh = document.getElementById("refreshPage");
    $scope.refresh.style.display = "none";

    //success/fail alerts
    var successs = document.getElementById("alertSuccess");
    var fail = document.getElementById("alertFail");
    //log the bucket ID
    console.log("Project id: " + $scope.fileId);

    //set route
    $scope.route = "uploadFile/";


    console.log("Route: " + $scope.route);

    //upload file method
    $scope.uploadFile = function () {

        if ($scope.source) {
            //internal server
            $scope.route = "uploadFile/";
            //first time is preview
            $scope.source = false;
            document.getElementById("uploadButton").value = "Upload";
            $scope.refresh.style.display = "block";
        } else {
            //external autodesk server
            $scope.route = "uploadFileExternal/";
            //second time is upload to autodesk server
            $scope.source = true;
            document.getElementById("uploadButton").value = "Preview";
            $scope.refresh.style.display = "none";
        }

        $scope.files = document.getElementById('uploadFile').files;
        FileUploader.post(
            '/api/v1/' + $scope.route + $scope.fileId,
            $scope.files
        ).then(function (data, status, headers, config) {
                //success
                console.log("Dobar je upload");
                console.log(data);

                fail.style.display = "none";
                successs.style.display = "block";

                var img = document.getElementById("uploadPreview");
                img.style.visibility = "visible";

                var imageName = data.files[0].name;

                if (imageName.indexOf(".SLDPRT") != -1) {
                    imageName = imageName.replace(".SLDPRT", "_PreviewPNG.png");
                } else if (imageName.indexOf(".sldprt") != -1) {
                    imageName = imageName.replace(".sldprt", "_PreviewPNG.png");
                } else if (imageName.indexOf(".SLDASM") != -1) {
                    imageName = imageName.replace(".SLDASM", "_PreviewPNG.png");
                } else if (imageName.indexOf(".sldasm") != -1) {
                    imageName = imageName.replace(".sldasm", "_PreviewPNG.png");
                }


                console.log("/img/generated/" + imageName);

                img.style.display = "block";
                img.src = "/img/generated/" + imageName;
                /*if($scope.route === "uploadFileExternal/"){
                 console.log("ID: " +$scope.projectId);
                 $location.path("/projects/"+$scope.projectId);
                 }*/
            }, function () {
                console.log("Los je upload !");
                successs.style.display = "none";
                fail.style.display = "block";

                //if there was an error stay on preview or stay on upload
                if ($scope.source) {
                    $scope.source = false;
                } else {
                    $scope.source = true;
                }
            }, function () {
                console.log("Notify");
            }); //todo implement callbacks

        $scope.setSource = function (source) {
            console.log("setSource: " + source);
            $scope.source = source;
        }
        $scope.refreshLocation = function () {

            $location.path("/#/projects/" + $scope.fileId);

            //$scope.refresh.href = "/#/uploadFile/"+$scope.fileId;
            console.log("/fileUpload/" + $scope.fileId);
            //$scope.$route.reload();


        }

    };
});
