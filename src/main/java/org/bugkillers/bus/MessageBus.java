package org.bugkillers.bus;

import java.util.Set;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Created by liuxinyu on 15/5/19.
 */
public class MessageBus {
    private static final LoadingCache<Class<?>, Set<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader() {
        public Set<Class<?>> load(Class<?> concreteClass) {
            return TypeToken.of(concreteClass).getTypes().rawTypes();
        }
    });
}
