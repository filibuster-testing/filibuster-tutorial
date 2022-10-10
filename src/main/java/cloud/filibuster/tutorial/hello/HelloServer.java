package cloud.filibuster.tutorial.hello;

import cloud.filibuster.tutorial.world.WorldServer;
import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.RequestHeaders;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.common.util.EventLoopGroups;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;

import io.netty.channel.EventLoopGroup;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class HelloServer {
    public final static int PORT = 6000;

    final private static String SERVICE_NAME = "hello";

    final static EventLoopGroup eventLoopGroup = EventLoopGroups.newEventLoopGroup(100);

    final static ClientFactory clientFactory = ClientFactory.builder().workerGroup(eventLoopGroup, /* shutdownOnClose= */true).build();

    final static String worldBaseURI = "http://127.0.0.1:" + WorldServer.PORT + "/";

    final static WebClient webClient = WebClient.builder(worldBaseURI).factory(clientFactory).build();

    private HelloServer() {

    }

    @SuppressWarnings("Varifier")
    public static Server serve() {
        ServerBuilder sb = Server.builder();
        sb.workerGroup(100);
        sb.http(PORT);

        sb.service("/world", new AbstractHttpService() {
            @Override
            protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                RequestHeaders getHeaders1 = RequestHeaders.of(HttpMethod.GET, "/", HttpHeaderNames.ACCEPT, "application/json");
                return HttpResponse.from(webClient.execute(getHeaders1).aggregate()
                        .handle((aggregatedHttpResponse, cause) -> {
                            ResponseHeaders headers = aggregatedHttpResponse.headers();
                            String statusCode = headers.get(HttpHeaderNames.STATUS);

                            if (statusCode.equals("200")) {
                                return HttpResponse.of("Hello, world!");
                            } else {
                                return HttpResponse.of(HttpStatus.FAILED_DEPENDENCY);
                            }
                        }));
            }
        });

        sb.service("/health-check", new AbstractHttpService() {
            @Override
            protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", "OK");
                return HttpResponse.of(jsonObject.toString());
            }
        });

        sb.service("/", new AbstractHttpService() {
            @Override
            protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                return HttpResponse.of("Hello, world!");
            }
        });

        return sb.build();
    }

    public static void main(String[] args) {
        Server server = serve();
        CompletableFuture<Void> future = server.start();
        future.join();
    }
}
