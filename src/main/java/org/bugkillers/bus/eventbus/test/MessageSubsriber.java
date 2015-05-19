package org.bugkillers.bus.eventbus.test;

import com.google.common.eventbus.Subscribe;
import org.bugkillers.bus.eventbus.Subscribe_;

/**
 * Created by liuxinyu on 15/5/19.
 */
public class MessageSubsriber {

    //@Subscribe
    @Subscribe_
    public void subsribe(MessageEvent event){
        System.out.println("接收到订阅的消息："+event.getMessage());
    }
}
