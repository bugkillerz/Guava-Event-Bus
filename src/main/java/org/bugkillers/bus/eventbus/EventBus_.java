/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bugkillers.bus.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.util.concurrent.MoreExecutors;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 向各个监听者分发事件和提供监听者注册事件的渠道。
 * <p>EventBus允许使用 发布-订阅 的方式来达到两个组件相互沟通，同时他们相互并不相互依赖，从而达到解耦的目的。
 * 这是专为使用显式取代传统的Java中的事件分布
 * 登记。这< em >不是< / em >一个通用的发布订阅系统，
 * 也不是用于进程间通信
 * <p/>
 * <h2>Receiving Events</h2>
 * <p>To receive events, an object should:
 * <ol>
 * <li>Expose a public method, known as the <i>event subscriber</i>, which accepts
 * a single argument of the type of event desired;</li>
 * <li>Mark it with a {@link Subscribe_} annotation;</li>
 * <li>Pass itself to an EventBus instance's {@link #register(Object)} method.
 * </li>
 * </ol>
 * <p/>
 * <h2>Posting Events</h2>
 * <p>To post an event, simply provide the event object to the
 * {@link #post(Object)} method.  The EventBus instance will determine the type
 * of event and route it to all registered listeners.
 * <p/>
 * <p>Events are routed based on their type &mdash; an event will be delivered
 * to any subscriber for any type to which the event is <em>assignable.</em>  This
 * includes implemented interfaces, all superclasses, and all interfaces
 * implemented by superclasses.
 * <p/>
 * <p>When {@code post} is called, all registered subscribers for an event are run
 * in sequence, so subscribers should be reasonably quick.  If an event may trigger
 * an extended process (such as a database load), spawn a thread or queue it for
 * later.  (For a convenient way to do this, use an {@link AsyncEventBus_}.)
 * <p/>
 * <h2>Subscriber Methods</h2>
 * <p>Event subscriber methods must accept only one argument: the event.
 * <p/>
 * <p>Subscribers should not, in general, throw.  If they do, the EventBus will
 * catch and log the exception.  This is rarely the right solution for error
 * handling and should not be relied upon; it is intended solely to help find
 * problems during development.
 * <p/>
 * <p>The EventBus guarantees that it will not call a subscriber method from
 * multiple threads simultaneously, unless the method explicitly allows it by
 * bearing the {@link AllowConcurrentEvents_} annotation.  If this annotation is
 * not present, subscriber methods need not worry about being reentrant, unless
 * also called from outside the EventBus.
 * <p/>
 * <h2>Dead Events</h2>
 * <p>If an event is posted, but no registered subscribers can accept it, it is
 * considered "dead."  To give the system a second chance to handle dead events,
 * they are wrapped in an instance of {@link DeadEvent_} and reposted.
 * <p/>
 * <p>If a subscriber for a supertype of all events (such as Object) is registered,
 * no event will ever be considered dead, and no DeadEvents will be generated.
 * Accordingly, while DeadEvent extends {@link Object}, a subscriber registered to
 * receive any Object will never receive a DeadEvent.
 * <p/>
 * <p>This class is safe for concurrent use.
 * <p/>
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/EventBusExplained">
 * {@code EventBus}</a>.
 *
 * @author Cliff Biffle
 * @since 10.0
 */
@Beta
public class EventBus_ {

    /**
     * 日志logger
     */
    private static final Logger logger = Logger.getLogger(EventBus_.class.getName());

    /**
     * 每个消息总线的唯一身份
     */
    private final String identifier;
    /**
     * jdk并发框架
     */
    private final Executor executor;
    /**
     * 异常Handler
     */
    private final SubscriberExceptionHandler_ exceptionHandler;

    /**
     * 订阅者
     */
    private final SubscriberRegistry_ subscribers = new SubscriberRegistry_(this);
    /**
     * 分发器
     */
    private final Dispatcher_ dispatcher;

    /**
     * 使用默认的名称创建一个EventBus "default".
     */
    public EventBus_() {
        this("default");
    }

    /**
     * 创建一个EventBus使用 {@code identifier}.
     *
     * @param identifier a brief name for this bus, for logging purposes.  Should
     *                   be a valid Java identifier.
     */
    public EventBus_(String identifier) {
        this(identifier, MoreExecutors.directExecutor(),
                Dispatcher_.perThreadDispatchQueue(), LoggingHandler_.INSTANCE);
    }

    /**
     * Creates a new EventBus with the given {@link SubscriberExceptionHandler}.
     *
     * @param exceptionHandler Handler for subscriber exceptions.
     * @since 16.0
     */
    public EventBus_(SubscriberExceptionHandler_ exceptionHandler) {
        this("default",
                MoreExecutors.directExecutor(), Dispatcher_.perThreadDispatchQueue(), exceptionHandler);
    }

    EventBus_(String identifier, Executor executor, Dispatcher_ dispatcher,
              SubscriberExceptionHandler_ exceptionHandler) {
        this.identifier = checkNotNull(identifier);
        this.executor = checkNotNull(executor);
        this.dispatcher = checkNotNull(dispatcher);
        this.exceptionHandler = checkNotNull(exceptionHandler);
    }

    /**
     * Returns the identifier for this event bus.
     */
    public final String identifier() {
        return identifier;
    }

    /**
     * Returns the default executor this event bus uses for dispatching events to subscribers.
     */
    final Executor executor() {
        return executor;
    }

    /**
     * Handles the given exception thrown by a subscriber with the given context.
     */
    void handleSubscriberException(Throwable e, SubscriberExceptionContext_ context) {
        checkNotNull(e);
        checkNotNull(context);
        try {
            exceptionHandler.handleException(e, context);
        } catch (Throwable e2) {
            // if the handler threw an exception... well, just log it
            logger.log(Level.SEVERE,
                    String.format(Locale.ROOT, "Exception %s thrown while handling exception: %s", e2, e),
                    e2);
        }
    }

    /**
     * 注册订阅者
     * Registers all subscriber methods on {@code object} to receive events.
     *
     * @param object object whose subscriber methods should be registered.
     */
    public void register(Object object) {
        subscribers.register(object);
    }

    /**
     * 取消注册
     * Unregisters all subscriber methods on a registered {@code object}.
     *
     * @param object object whose subscriber methods should be unregistered.
     * @throws IllegalArgumentException if the object was not previously registered.
     */
    public void unregister(Object object) {
        subscribers.unregister(object);
    }

    /**
     * Posts an event to all registered subscribers.  This method will return
     * successfully after the event has been posted to all subscribers, and
     * regardless of any exceptions thrown by subscribers.
     * <p/>
     * <p>If no subscribers have been subscribed for {@code event}'s class, and
     * {@code event} is not already a {@link DeadEvent_}, it will be wrapped in a
     * DeadEvent and reposted.
     *
     * @param event event to post.
     */
    public void post(Object event) {
        Iterator<Subscriber_> eventSubscribers = subscribers.getSubscribers(event);
        if (eventSubscribers.hasNext()) {
            dispatcher.dispatch(event, eventSubscribers);
        } else if (!(event instanceof DeadEvent_)) {
            // the event had no subscribers and was not itself a DeadEvent
            post(new DeadEvent_(this, event));
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(identifier)
                .toString();
    }

    /**
     * Simple logging handler for subscriber exceptions.
     * 异常日志记录处理Handler
     */
    static final class LoggingHandler_ implements SubscriberExceptionHandler_ {
        static final LoggingHandler_ INSTANCE = new LoggingHandler_();

        //@Override
        public void handleException(Throwable exception, SubscriberExceptionContext_ context) {
            Logger logger = logger(context);
            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, message(context), exception);
            }
        }

        private static Logger logger(SubscriberExceptionContext_ context) {
            return Logger.getLogger(com.google.common.eventbus.EventBus.class.getName() + "." + context.getEventBus().identifier());
        }

        private static String message(SubscriberExceptionContext_ context) {
            Method method = context.getSubscriberMethod();
            return "Exception thrown by subscriber method "
                    + method.getName() + '(' + method.getParameterTypes()[0].getName() + ')'
                    + " on subscriber " + context.getSubscriber()
                    + " when dispatching event: " + context.getEvent();
        }
    }
}
