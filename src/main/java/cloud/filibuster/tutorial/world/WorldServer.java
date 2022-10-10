package cloud.filibuster.tutorial.world;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import org.json.JSONObject;

public class WorldServer {
    final public static int PORT = 6001;

    final private static String SERVICE_NAME = "world";

    private WorldServer() {

    }

    @SuppressWarnings("Varifier")
    public static Server serve() {
        ServerBuilder sb = Server.builder();
        sb.workerGroup(100);
        sb.http(PORT);

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
                return HttpResponse.of(HttpStatus.OK);
            }
        });

        return sb.build();
    }
}
