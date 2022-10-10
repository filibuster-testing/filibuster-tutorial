package cloud.filibuster.tutorial;

import cloud.filibuster.tutorial.hello.HelloServer;
import cloud.filibuster.tutorial.world.WorldServer;
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

public class HelloAndWorldIntegrationTest {
    private static Server helloServer;
    private static Server worldServer;

    @BeforeAll
    public static void startServers() throws InterruptedException {
        helloServer = HelloServer.serve();
        helloServer.start();

        worldServer = WorldServer.serve();
        worldServer.start();

        Thread.sleep(10);
    }

    @AfterAll
    public static void stopServers() throws InterruptedException {
        worldServer.close();
        worldServer.blockUntilShutdown();

        helloServer.close();
        helloServer.blockUntilShutdown();
    }

    @Test
    public void testHelloWorldRoute() {
        WebClient webClient = WebClient.builder("http://localhost:" + HelloServer.PORT).build();
        RequestHeaders getHeaders = RequestHeaders.of(HttpMethod.GET, "/world", HttpHeaderNames.ACCEPT, "application/json");
        AggregatedHttpResponse response = webClient.execute(getHeaders).aggregate().join();
        ResponseHeaders headers = response.headers();
        String statusCode = headers.get(HttpHeaderNames.STATUS);
        assertEquals("200", statusCode);
    }
}
