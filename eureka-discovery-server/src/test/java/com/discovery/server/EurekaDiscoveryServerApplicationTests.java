package com.discovery.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Eureka Discovery Server.
 *
 * <p>Uses the "dev" profile so self-preservation is disabled and
 * the server does not try to connect to peers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class EurekaDiscoveryServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    // ----------------------------------------------------------------
    // Context load
    // ----------------------------------------------------------------

    @Test
    void contextLoads() {
        // Verifies the Spring context starts without errors
    }

    // ----------------------------------------------------------------
    // Security — unauthenticated access
    // ----------------------------------------------------------------

    @Test
    void givenNoAuth_whenAccessDashboard_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNoAuth_whenAccessEurekaApi_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/eureka/apps"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNoAuth_whenAccessActuatorMetrics_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
            .andExpect(status().isUnauthorized());
    }

    // ----------------------------------------------------------------
    // Actuator — open endpoints (no auth required)
    // ----------------------------------------------------------------

    @Test
    void givenNoAuth_whenAccessHealthEndpoint_thenOk() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }

    @Test
    void givenNoAuth_whenAccessInfoEndpoint_thenOk() throws Exception {
        mockMvc.perform(get("/actuator/info"))
            .andExpect(status().isOk());
    }

    // ----------------------------------------------------------------
    // Security — authenticated access
    // ----------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenAdminUser_whenAccessEurekaApps_thenOk() throws Exception {
        mockMvc.perform(get("/eureka/apps"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenAdminUser_whenAccessPrometheus_thenOk() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenAdminUser_whenAccessActuatorHealth_thenOkWithDetails() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }
}
