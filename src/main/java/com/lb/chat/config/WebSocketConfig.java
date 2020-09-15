package com.lb.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
/**
 * 通过注解开启使用STOMP协议来传输基于代理（message broker）的消息，这时控制器支持使用@MessageMapping
 * 就像使用@RequestMapping一样。
 */
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * 讲一下什么是 webSocket
     * WebSocket用于在Web浏览器和服务器之间进行任意的双向数据传输的一种技术。
     * WebSocket协议基于TCP协议实现，包含初始的握手过程，以及后续的多次数据帧双向传输过程。
     * 其目的是在WebSocket应用和WebSocket服务器进行频繁双向通信时，
     * 可以使服务器避免打开多个HTTP连接进行工作来节约资源，提高了工作效率和资源利用率。
     * <p>
     * 说人话：正常的http协议一个request 对应 一个response，很难实现一次request然后多次response(循坏除外)
     * 而对于这种场景经常用域弹幕系统、web聊天室，其他用户发送的信息，要让所有当前用户看到
     * 也就是服务端对所有登录用户发送一次response，而这种问题使用webSocket协议就非常容易解决
     */

    @Override
    /**
     * 配置消息代理（Message Broker)
     */
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /**
         * 比如说：/LuBan/chat
         *  设置接受的请求地址以 “/LuBan”开头
         *  如果拦截到了，继续去Controller找剩下的地址 "/chat"
         */
        registry.setApplicationDestinationPrefixes("/LuBan");
        /**
         * 设置对返回的地址进行放行
         * user必须放行，因为这是单对单发送信息的地址的前缀
         */
        registry.enableSimpleBroker("/LuBan", "/user");
    }

    @Override
    /**
     * 注册（endpoint），并映射指定的URL。
     */
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         *  注册一个STOMP的endpoint，并指定使用SockJS协议。
         *
         * WebSocket虽好，但一些浏览器中缺少对WebSocket的支持，因此，回退选项是必要的。
         * SockJS和socket.io都是对WebSocket的模拟。
         *
         *  SockJS：SockJS是一个浏览器的JavaScript库，它提供了一个类似于网络的对象，SockJS提供了一个连贯的，跨浏览器的JavaScriptAPI，
         *  它在浏览器和Web服务器之间创建了一个低延迟、全双工、跨域通信通道
         *
         *   STOMP：在webSocket的基础上进行了扩展，webSocket没有高层协议,
         *   就需要我们定义应用间发送消息的语义,还需要确保连接的两端都能遵循这些语义
         *   可以理解为消息信息的载体，在管道中传输信息
         */
        registry.addEndpoint("/LuBans").withSockJS();
    }

}
