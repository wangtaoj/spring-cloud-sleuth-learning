package com.wangtao.brave;

import brave.Tracer;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggageFields;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.baggage.CorrelationScopeConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalCurrentTraceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangtao
 * Created at 2026-01-19
 */
@Configuration(proxyBeanMethods = false)
public class BraveConfig {

    @Bean
    public Tracing tracing() {
        // MDCScopeDecorator默认添加了BaggageFields.TRACE_ID、BaggageFields.SPAN_ID
        // 即会把traceId、spanId到MDC中
        // 可通过add方法添加别的数据, 比如parentId, 或者自定义的数据
        CurrentTraceContext.ScopeDecorator scopeDecorator = MDCScopeDecorator.newBuilder()
            .add(CorrelationScopeConfig.SingleCorrelationField.create(BaggageFields.PARENT_ID))
            .build();
        CurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.newBuilder()
            .addScopeDecorator(scopeDecorator)
            .build();
        BaggageField userIdField = BaggageField.create("userId");
        return Tracing.newBuilder()
            .localServiceName("brave-api-demo")
            .currentTraceContext(currentTraceContext)
            .propagationFactory(
                BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
                    .add(BaggagePropagationConfig.SingleBaggageField.remote(userIdField))
                    .build()
            )
            .build();
    }

    @Bean
    public Tracer tracer(Tracing tracing) {
        return tracing.tracer();
    }
}
