package com.lb.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
public class WebSocketController {

    private int num = 0;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 一个request对应一次response
     * 给所有人发
     */
    @MessageMapping("/chat")
    @SendTo("/LuBan/RChat")
    public String chat(Message message) {
        byte[] bytes = (byte[]) message.getPayload();
        String content = new String(bytes);
        return content;
    }

    @GetMapping("/all")
    @ResponseBody
    public void chatAll() {
        /**
         * 实现对所有建立连接的用户进行发送信息，不需要request直接进行response
         */
        simpMessagingTemplate.convertAndSend("/LuBan/RCount", num);
    }

    @GetMapping("/user")
    @ResponseBody
    public void addUser(HttpServletRequest request) {
        if (request.getParameter("opt").equals("add")) {
            num++;
        } else {
            num--;
        }
    }

    /**
     * 实现单对单发送信息
     */
    @GetMapping("/one")
    @ResponseBody
    public void chatOne(HttpServletRequest request) {
        /**
         * 底层依然使用的是 convertAndSend 但是他用了封装
         * 也就是说这句话发送信息的接收端地址，必须是  /user/zilu/OChat  /user是写死了
         * 所以说每次新的接收端地址必须含有一个 /user/xxx/xxx  代表的只能自己接受信息的一个地址
         *
         */
        simpMessagingTemplate.convertAndSendToUser(request.getParameter("name"), "/OChat", "{\"私聊消息\":\"" + request.getParameter("myName") + "：" + request.getParameter("message") + "\"}");
        simpMessagingTemplate.convertAndSendToUser(request.getParameter("myName"), "/OChat", "{\"私聊消息\":\"" + request.getParameter("myName") + "TO" + request.getParameter("Name") + "：" + request.getParameter("message") + "\"}");

    }
}
