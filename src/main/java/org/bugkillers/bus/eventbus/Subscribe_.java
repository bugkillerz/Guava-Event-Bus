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
import com.google.common.eventbus.EventBus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 订阅注解
 * 标示一个方法作为一个事件的订阅者
 * <p/>
 * <p>事件类型将由方法的第一个（也是唯一的）参数表明。如果这个注解标注的方法没有参数或者超过一个参数，
 * 则包含的方法则不能注册成为事件的订阅者对于 {@link EventBus_}。
 * <p/>
 * <p>除非是使用注解另外表明 @{@link AllowConcurrentEvents_}, 事件订阅方法将连续通过他们注册的每个事件总线调用.
 *
 * @author Cliff Biffle
 * @since 10.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Beta
public @interface Subscribe_ {
}
