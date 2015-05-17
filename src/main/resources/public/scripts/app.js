/**
 * Created by shekhargulati on 10/06/14.
 */

var app = angular.module('worxapp', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute'
]);

//todo add authentication logic
app.config(function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/list.html',
        controller: 'ListCtrl'
    }).when('/createBucket', {
        templateUrl: 'views/createBucket.html',
        controller: 'CreateBucketCtrl'
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
        $http.post('/api/v1/projects', $scope.project).success(function (data) {
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }
});