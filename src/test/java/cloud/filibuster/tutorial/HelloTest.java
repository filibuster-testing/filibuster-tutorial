package cloud.filibuster.tutorial;

import cloud.filibuster.tutorial.hello.HelloServer;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.RequestHeaders;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloTest {
    private static Server helloServer;

    @BeforeAll
    public static void startHelloServer() throws InterruptedException {
        helloServer = HelloServer.serve();
        helloServer.start();
        Thread.sleep(10);
    }

    @AfterAll
    public static void stopHelloServer() throws InterruptedException {
        helloServer.close();
        helloServer.blockUntilShutdown();
    }

    @Test
    public void testHelloRoot() {
        WebClient webClient = WebClient.builder("http://localhost:" + HelloServer.PORT).build();
        RequestHeaders getHeaders = RequestHeaders.of(HttpMethod.GET, "/", HttpHeaderNames.ACCEPT, "application/json");
        AggregatedHttpResponse response = webClient.execute(getHeaders).aggregate().join();
        ResponseHeaders headers = response.headers();
        String statusCode = headers.get(HttpHeaderNames.STATUS);
        assertEquals("200", statusCode);
    }

    @Test
    public void testHelloHealthCheck() {
        WebClient webClient = WebClient.builder("http://localhost:" + HelloServer.PORT).build();
        RequestHeaders getHeaders = RequestHeaders.of(HttpMethod.GET, "/health-check", HttpHeaderNames.ACCEPT, "application/json");
        AggregatedHttpResponse response = webClient.execute(getHeaders).aggregate().join();
        ResponseHeaders headers = response.headers();
        String statusCode = headers.get(HttpHeaderNames.STATUS);
        assertEquals("200", statusCode);
    }
}
