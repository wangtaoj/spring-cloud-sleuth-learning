package com.wangtao.brave;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggageFields;
import brave.propagation.TraceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wangtao
 * Created at 2026-01-20
 */
@SpringBootTest
public class BraveApiTest {

    @Autowired
    public Tracer tracer;

    @Test
    public void testTraceApi() {
        // 创建根span
        Span rootSpan = tracer.nextSpan().name("root").start();
        // 开启作用域, 将rootSpan中的traceContext对象放到threadLocal中
        // SpanInScope的close会关闭作用域，还原上一次threadLocal中的traceContext值
        try (Tracer.SpanInScope ignored = tracer.withSpanInScope(rootSpan)) {
            // 从ThreadLocal中获取TraceContext
            Assertions.assertNotNull(Tracing.current().currentTraceContext().get());
            // 根据当前绑定的traceContext恢复span，这里会创建一个新的，因此不能使用==判断
            Span currentSpan = tracer.currentSpan();
            Assertions.assertEquals(rootSpan, currentSpan);

            // 创建子span
            Span childSpan = tracer.nextSpan().name("child").start();
            Assertions.assertEquals(rootSpan.context().traceIdString(), childSpan.context().traceIdString());
            Assertions.assertEquals(rootSpan.context().spanIdString(), childSpan.context().parentIdString());
            childSpan.finish();
        } finally {
            rootSpan.finish();
        }
        // 作用域外, 无法获取到绑定到ThreadLocal中的TraceContext对象
        Assertions.assertNull(Tracing.current().currentTraceContext().get());
        Span currentSpan = tracer.currentSpan();
        Assertions.assertNull(currentSpan);
    }

    @Test
    public void testMDCAction() {
        String oldTraceId = "123";
        MDC.put(BaggageFields.TRACE_ID.name(), oldTraceId);
        // 创建span并开启作用域, 绑定TraceContext
        ScopedSpan root = tracer.startScopedSpan("root");
        TraceContext context = root.context();
        Assertions.assertEquals(MDC.get(BaggageFields.TRACE_ID.name()), context.traceIdString());
        Assertions.assertEquals(MDC.get(BaggageFields.SPAN_ID.name()), context.spanIdString());
        Assertions.assertEquals(MDC.get(BaggageFields.PARENT_ID.name()), context.parentIdString());
        root.finish();
        // 作用域结束后MDC中的值会还原
        Assertions.assertEquals(oldTraceId, MDC.get(BaggageFields.TRACE_ID.name()));
    }

    @Test
    public void testBaggageField() {
        ScopedSpan root = tracer.startScopedSpan("root");
        // 作用域内可直接通过静态方法获取, 本质上从traceContext的extra中获取
        BaggageField userIdField = BaggageField.getByName("userId");
        // 初始值为null
        Assertions.assertNull(userIdField.getValue());
        // 更新值, 如果当前没有traceContext, 不会有任何动作, 返回false
        Assertions.assertTrue(userIdField.updateValue("0000000"));

        // 创建子span时，也会复制parent traceContext的extra值，修改不会影响父的值
        ScopedSpan child = tracer.startScopedSpan("child");
        BaggageField userIdFieldChild = BaggageField.getByName("userId");
        Assertions.assertEquals("0000000", userIdFieldChild.getValue());
        // 修改
        Assertions.assertTrue(userIdField.updateValue("0000001"));
        // 结束child
        child.finish();
        userIdField = BaggageField.getByName("userId");
        // 依然还是原来的值
        Assertions.assertEquals("0000000", userIdField.getValue());
        // 结束root
        root.finish();

        // 当前无TraceContext, 更新无效果, 获取无值
        BaggageField userName = BaggageField.create("userName");
        Assertions.assertFalse(userName.updateValue("zhangsan"));
        Assertions.assertNull(userName.getValue());
    }
}
