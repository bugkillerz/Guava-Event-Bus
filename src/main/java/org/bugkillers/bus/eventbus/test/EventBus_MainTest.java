package org.bugkillers.bus.eventbus.test;

import com.google.common.eventbus.EventBus;
import org.bugkillers.bus.eventbus.EventBus_;

/**
 * Created by liuxinyu on 15/5/19.
 */
public class EventBus_MainTest {
    public static void main(String[] args) {
        EventBus_ eventBus = new EventBus_("my");
        MessageSubsriber messageSubsriber = new MessageSubsriber();
        eventBus.register(messageSubsriber);
        eventBus.post(new MessageEvent("哈哈"));
        eventBus.post(new MessageEvent("嘿嘿"));
        eventBus.post(new MessageEvent("呵呵"));

//        EventBus eventBus2 = new EventBus("my");
//        MessageSubsriber messageSubsriber2 = new MessageSubsriber();
//        eventBus.register(messageSubsriber2);
//        eventBus.post(new MessageEvent("哈哈"));
//        eventBus.post(new MessageEvent("嘿嘿"));
//        eventBus.post(new MessageEvent("呵呵"));
    }

}
