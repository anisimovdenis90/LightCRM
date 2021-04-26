angular.module('app')
    .directive('scroll', function($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element, attr) {
                scope.$watchCollection(attr.scroll, function(newVal) {
                    $timeout(function() {
                        element[0].scrollTop = element[0].scrollHeight;
                    });
                });
            }
        }
    })
    .directive('ngEnter', function() {
        return function(scope, element, attrs) {
            element.bind("keydown", function(e) {
                if(e.which === 13 && e.ctrlKey) {
                    var text = document.getElementById("chatInput");
                    text.value += '\n';
                } else if(e.which === 13) {
                    scope.$apply(function(){
                        scope.$eval(attrs.ngEnter, {'e': e});
                    });
                    e.preventDefault();
                }
            });
        };
    })
    .controller('chatController',
    function ($scope, $rootScope, $http, $location, $localStorage, $filter, $routeParams, chatService, pageService) {

        const contextPath = 'http://localhost:8180/app';
        $scope.senderId = $localStorage.currentUser.profileId;
        $scope.messages = [];
        $scope.newMessage = {};
        $scope.messageText = "";

        $scope.subscribe = function() {
            chatService.ready.then(function () {
                chatService.unsubscribe();
                chatService.subscribe("/user/" + $scope.senderId + "/queue/messages", function (payload) {
                    $scope.getNotification(payload);
                });
            });
        };

        $scope.sendMessage = function() {
            if ($scope.messageText !== "" && $scope.messageText.trim() !== "") {
                $scope.newMessage = {
                    senderId: $scope.senderId,
                    recipientId: $scope.currentChat.recipientId,
                    senderName: $rootScope.currentProfile.firstname + " " + $rootScope.currentProfile.lastname,
                    recipientName: $scope.currentChat.recipientName,
                    content: $scope.messageText,
                    timestamp: $filter('date')(new Date(),"yyyy-MM-dd'T'HH:mm:ss.sssZ")
                };
                chatService.send('/app/chat', $scope.newMessage);
                $scope.messageText = "";
                $scope.messages.push($scope.newMessage);
            }
        };

        $scope.getMessages = function(senderId, recipientId) {
            $http({
                url: contextPath + '/chats/messages/' + senderId + '/' + recipientId,
                method: 'GET'
            })
                .then(function (response) {
                    $scope.messages = response.data;
                });
        };

        $scope.getMessageById = function(id) {
            $http({
                url: contextPath + '/chats/messages/' + id,
                method: 'GET'
            })
                .then(function (response) {
                    $scope.messages.push(response.data);
                });
        };

        $scope.getNotification = function(payload) {
            if ($scope.activeChatWindow) {
                if (payload.chatId === $scope.currentChat.chatId) {
                    $scope.getMessageById(payload.messageId);
                } else {
                    angular.forEach($scope.chats, function (value) {
                        if (value.chatId === payload.chatId) {
                            $scope.playAudio();
                            value.unreadMessageCount += 1;
                            $scope.pageNotificationCount += 1;
                            pageService.setNotificationCount($scope.pageNotificationCount);
                        }
                    });
                }
            }
        };

        $scope.getChatById = function(chatId, recipientId) {
            angular.forEach($scope.chats, function (value) {
                if (value.chatId === chatId) {
                    $scope.pageNotificationCount -= value.unreadMessageCount;
                }
            });
            pageService.setNotificationCount($scope.pageNotificationCount);
            $http({
                url: contextPath + '/chats/room/' + chatId + '/' + recipientId,
                method: 'GET'
            })
                .then(function (response) {
                    $scope.currentChat = response.data;
                    $scope.currentChat.unreadMessageCount = 0;
                    $scope.getMessages($scope.currentChat.senderId, $scope.currentChat.recipientId);
                    $http.get(contextPath + '/api/v1/files/photo/preview/' + $scope.currentChat.recipientId,
                        {responseType: "arraybuffer"}
                    )
                        .then(function (response) {
                            let contentType = response.headers("content-type");
                            let blob = new Blob([response.data], {type: contentType});
                            $scope.currentChat.recipientAvatar = (window.URL || window.webkitURL).createObjectURL(blob);
                        });
                    angular.forEach($scope.chats, function (value, key) {
                        if (value.chatId === $scope.currentChat.chatId) {
                            $scope.chats[key] = $scope.currentChat;
                        }
                    });
                });
        };

        $scope.setActive = function(chatId, recipientId) {
            $scope.getChatById(chatId, recipientId);
        };

        $scope.subscribe();
        $scope.getMessages($scope.currentChat.senderId, $scope.currentChat.recipientId);
});