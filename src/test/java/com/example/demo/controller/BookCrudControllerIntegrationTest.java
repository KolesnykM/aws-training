package com.example.demo.controller;

import com.example.demo.DemoAwsApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, properties = "spring.main.allow-bean-definition-overriding=true")
public class BookCrudControllerIntegrationTest {

    @Autowired
    BookCrudController bookCrudController;
    @MockBean
    private QueueMessagingTemplate queueMessagingTemplate;
    MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = standaloneSetup(bookCrudController).build();
        try {
            createBook();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldCreateBook() throws Exception {
        String expected = "{" +
                "\"isbn\":\"123-457\"," +
                "\"title\":\"title\"," +
                "\"description\":\"description\"" +
                "}";
        MockHttpServletRequestBuilder request = post("/books")
                .content(expected)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isCreated());
        String stringResponse = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals(expected, stringResponse);
        verify(queueMessagingTemplate, times(2)).send(anyString(), any());
    }

    @Test
    public void shouldReadBook() throws Exception {
        String expected = "{" +
                "\"isbn\":\"123-456\"," +
                "\"title\":\"title\"," +
                "\"description\":\"description\"" +
                "}";
        MockHttpServletRequestBuilder request = get("/books/123-456");
        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
        String stringResponse = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals(expected, stringResponse);
        verify(queueMessagingTemplate, times(2)).send(anyString(), any());
    }

    @Test
    public void shouldUpdateBook() throws Exception {
        String expected = "{" +
                "\"isbn\":\"123-456\"," +
                "\"title\":\"title_update\"," +
                "\"description\":\"description_update\"" +
                "}";
        MockHttpServletRequestBuilder request = put("/books")
                .content(expected)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
        String stringResponse = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals(expected, stringResponse);
        verify(queueMessagingTemplate, times(2)).send(anyString(), any());
    }

    @Test
    public void shouldDeleteBook() throws Exception {
        String expected = "123-456";
        MockHttpServletRequestBuilder request = delete("/books/123-456");
        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
        String stringResponse = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals(expected, stringResponse);
        verify(queueMessagingTemplate, times(2)).send(anyString(), any());
    }

    private void createBook() throws Exception {
        String expected = "{" +
                "\"isbn\":\"123-456\"," +
                "\"title\":\"title\"," +
                "\"description\":\"description\"" +
                "}";
        MockHttpServletRequestBuilder request = post("/books")
                .content(expected)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(request);
    }
}