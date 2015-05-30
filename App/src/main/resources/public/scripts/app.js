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
    }).when('/uploadFile', {
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
                $location.path('/projects/' + data.replace('"','').replace('"',''));
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
    }).error(function (data, status) {
        console.log('Error ' + data)
    })
});

app.controller('UploadFileCtrl', function ($scope, $http, $routeParams, $location, FileUploader) {
    $scope.fileId = $routeParams.fileId;

    $scope.uploadFile = function () {
        $scope.files = document.getElementById('uploadFile').files;
        FileUploader.post(
            '/api/v1/uploadFile/' + $scope.fileId,
            $scope.files
        ).then(function (response, status) {
                console.log("Dobar je upload");
                alert(data)
            }, function () {
                console.log("Los je upload !");
            }, function () {
                console.log("Notify");
            }); //todo implement callbacks
    };
});
