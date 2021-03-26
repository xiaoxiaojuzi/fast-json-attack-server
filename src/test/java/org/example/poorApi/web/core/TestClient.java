package org.example.poorApi.web.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Component
@Lazy
public class TestClient {
    private final MockMvc mockMvc;
    private static final ObjectMapper mapper = initializeMapper();

    public TestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private static ObjectMapper initializeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    String serialize(Object value) throws Exception {
        return mapper.writeValueAsString(value);
    }

    public ResultActions post(String uri, Object request) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(uri)
            .content(serialize(request))
            .contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    public ResultActions get(String uri) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(uri));
    }

    public static String getLocation(ResultActions resultActions) {
        return resultActions.andReturn()
            .getResponse().getHeader("location");
    }
}
