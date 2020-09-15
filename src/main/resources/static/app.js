var stompClient = null;
var name = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
        $("#words").hide();
    }
    $("#greetings").html("");
}

function connect() {
    name = $("#name").val();
    //创建一个协议，如果不是同一端口，则要写完整的地址 https://localhost:8080/LuBans
    var socket = new SockJS('/LuBans');
    //和自己创建的STOMP协议的节点建立连接，
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        //设置接收地址，接收返回的数据
        stompClient.subscribe('/LuBan/RChat', function (data) {
            showGreeting(data.body);
        });
        stompClient.subscribe('/user/' + name + '/OChat', function (data) {
            showGreeting(data.body);
        });
        stompClient.subscribe('/LuBan/RCount', function (data) {
            document.getElementById("count").innerHTML = data.body;
        });
    });
    $.get({
        url: "http://localhost:8080/user?opt=add",
    });
    alert("连接成功");
    setInterval(showCount, 500);
}

function disconnect() {
    if (stompClient !== null) {
        $.get({
            url: "http://localhost:8080/user?opt=cut",
        });
        stompClient.disconnect();
    }
    setConnected(false);
    name = '';
    alert("退出连接成功");
}

function chatAll() {
    stompClient.send("/LuBan/chat", {}, JSON.stringify({'群发消息': $("#name").val() + ' : ' + $("#chatAll").val()}));
}

function showCount() {
    $.get({
        url: "http://localhost:8080/all",
    });
}

function chatOne() {
    $.get({
        url: "http://localhost:8080/one",
        data: {name: $("#chatName").val(), myName: $("#name").val(), message: $("#chatOne").val()},
    });
}

function showGreeting(message) {
    var str = "";
    var content = message.toString();
    if (content.charAt(2) === '群') {
        if (content.substring(9, name.length + 9) === name) {
            str = '<div class="btalk"><span>' + content + '</span></div>';
        } else {
            str = '<div class="atalk"><span>' + content + '</span></div>';
        }
        document.getElementById("words").innerHTML = document.getElementById("words").innerHTML + str;
    } else {
        if (content.substring(9, name.length + 9) === name) {
            str = '<div class="btalk"><span>' + content + '</span></div>';
        } else {
            /*  $("#greetings").append("<tr><td><font color=\"#FF0000\">" + message + "</font></td></tr>");*/
            str = '<div class="atalk"><span>' + content + '</span></div>';
        }
        document.getElementById("words2").innerHTML = document.getElementById("words2").innerHTML + str;
    }
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        chatAll();
    });
    $("#sendOne").click(function () {
        chatOne();
    });
    $("#showCount").click(function () {
        showCount();
    });
});

