package by.baraznov.authenticationservice.controllers;

import by.baraznov.authenticationservice.config.TestContainersConfig;
import by.baraznov.authenticationservice.dtos.RefreshJwtRequestDTO;
import by.baraznov.authenticationservice.dtos.RequestDTO;
import by.baraznov.authenticationservice.dtos.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestContainersConfig.class)
@Testcontainers
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String LOGIN = "testuser";
    private static final String PASSWORD = "testpass";
    private static String accessToken;
    private static String refreshToken;

    @Test
    @Order(1)
    void test_registration() throws Exception {
        RequestDTO request = new RequestDTO(LOGIN, PASSWORD);

        MvcResult result = mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        ResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDTO.class);
        accessToken = response.accessToken();
        refreshToken = response.refreshToken();

        assertNotNull(accessToken);
        assertNotNull(refreshToken);
    }

    @Test
    @Order(2)
    void test_login() throws Exception {
        RequestDTO request = new RequestDTO(LOGIN, PASSWORD);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andReturn();

        ResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDTO.class);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
    }

    @Test
    @Order(3)
    void test_validAccessToken() throws Exception {
        mockMvc.perform(post("/auth/valid")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Access token is valid"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @Order(4)
    void test_refreshToken() throws Exception {
        RefreshJwtRequestDTO request = new RefreshJwtRequestDTO(refreshToken);

        MvcResult result = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        ResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDTO.class);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
    }
}
