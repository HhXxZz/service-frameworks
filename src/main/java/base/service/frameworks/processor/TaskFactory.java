package base.service.frameworks.processor;

import base.service.frameworks.misc.Parameters;
import base.service.frameworks.processor.annotation.API;
import base.service.frameworks.utils.ClassScanner;
import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by someone on 2020/12/2 10:26.
 */
@SuppressWarnings("unused")
public enum TaskFactory {
    // ===========================================================
    // Enums
    // ===========================================================
    INSTANCE;

    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(TaskFactory.class);

    // ===========================================================
    // Fields
    // ===========================================================
    private final AtomicBoolean                            mInitialed = new AtomicBoolean(false);
    private       ImmutableMap<String, ConstructorWrapper> mTasks;

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public void init(String... pPackage) {
        if (mInitialed.compareAndSet(false, true)) {
            scanTasks(pPackage);
        }
    }

    public void release() {
        if (mInitialed.compareAndSet(true, false)) {
            mTasks = null;
        }
    }

    private void scanTasks(String... pPackage) {
        if (pPackage != null && pPackage.length > 0) {
            Map<String, ConstructorWrapper> apiMap = new HashMap<>();
            Arrays.stream(pPackage).forEach(packageName -> {
                List<Class<? extends BaseTask>> apis = ClassScanner.in(packageName).withAnnotation(API.class).scanInherited(BaseTask.class);
                apis.forEach(clazz -> {
                    API api = clazz.getAnnotation(API.class);
                    @SuppressWarnings("unchecked")
                    Constructor<BaseTask>[] constructors = (Constructor<BaseTask>[]) clazz.getConstructors();
                    for (Constructor<BaseTask> constructor : constructors) {
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        if (parameterTypes.length == 3
                                && HttpRequest.class.isAssignableFrom(parameterTypes[0])
                                && ChannelHandlerContext.class.isAssignableFrom(parameterTypes[1])
                                && Parameters.class.isAssignableFrom(parameterTypes[2])) {
                            apiMap.put(api.uri(), new ConstructorWrapper(
                                    constructor, api.enableToken(), api.enableMember(), api.enableAuthentication(), api.enableEncryption()
                            ));
                            break;
                        }
                    }
                    if (apiMap.containsKey(api.uri())) {
//                        LOG.dd("API: %-30s[%s]%s%s%s%s active", api.uri(), api.name(),
//                                api.enableToken() ? " T" : "", api.enableMember() ? " M" : "",
//                                api.enableAuthentication() ? " A" : "", api.enableEncryption() ? " E": "");
                    } else {
                        //LOG.ww("API: %-30s[%s] no constructor", api.uri(), api.name());
                    }
                });
            });
            mTasks = ImmutableMap.copyOf(apiMap);
        } else {
            mTasks = ImmutableMap.of();
        }
    }

    public BaseTask obtainTask(String pURI, FullHttpRequest pRequest, ChannelHandlerContext pContext, Parameters pParams) {
        if (mInitialed.get()) {
            if (mTasks.containsKey(pURI)) {
                try {
                    ConstructorWrapper wrapper = mTasks.get(pURI);
                    BaseTask task = wrapper.constructor.newInstance(pRequest, pContext, pParams);
                    task.enableToken(wrapper.enableToken);
                    task.enableMember(wrapper.enableMember);
                    task.enableAuthentication(wrapper.enableAuthentication);
                    task.enableEncryption(wrapper.enableEncryption);
                    return task;
                } catch (Exception e) {
                    LOG.error("obtain task failed.", e);
                }
            }
        }
        return null;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    private static final class ConstructorWrapper {
        Constructor<BaseTask> constructor;
        boolean               enableToken;
        boolean               enableMember;
        boolean               enableAuthentication;
        boolean               enableEncryption;

        ConstructorWrapper(Constructor<BaseTask> pConstructor,
                           boolean pEnableToken,
                           boolean pEnableMember,
                           boolean pEnableAuthentication,
                           boolean pEnableEncryption) {
            this.constructor = pConstructor;
            this.enableToken = pEnableToken;
            this.enableMember = pEnableMember;
            this.enableAuthentication = pEnableAuthentication;
            this.enableEncryption = pEnableEncryption;
        }
    }
}