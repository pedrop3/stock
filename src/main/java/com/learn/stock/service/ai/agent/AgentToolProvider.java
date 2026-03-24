package com.learn.stock.service.ai.agent;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.*;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class AgentToolProvider implements ToolProvider {

    private final Map<ToolSpecification, ToolExecutor> toolMap = new LinkedHashMap<>();

    public AgentToolProvider(Object... toolObjects) {
        for (Object obj : toolObjects) {
            for (Method method : obj.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    ToolSpecification spec = ToolSpecifications.toolSpecificationFrom(method);
                    ToolExecutor executor = new DefaultToolExecutor(obj, method);
                    toolMap.put(spec, executor);
                }
            }
        }
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder builder = ToolProviderResult.builder();
        toolMap.forEach(builder::add);
        return builder.build();
    }
}
