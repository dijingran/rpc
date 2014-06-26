package org.dxx.rpc.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类似于spring 的Autowire注解，可以对远程服务的接口进行注入（注入的是 Rpc生成的代理类）。
 * @author   dixingxing
 * @Date	 2014-6-26
 * @see RpcApplicationListener
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcBean {
}
