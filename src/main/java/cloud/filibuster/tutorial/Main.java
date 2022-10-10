package cloud.filibuster.tutorial;

import cloud.filibuster.tutorial.hello.HelloServer;
import cloud.filibuster.tutorial.world.WorldServer;
import com.linecorp.armeria.server.Server;

import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Server helloServer = HelloServer.serve();
        Server worldServer = WorldServer.serve();

        CompletableFuture<Void> helloServerFuture = helloServer.start();
        CompletableFuture<Void> worldServerFuture = worldServer.start();

        while (true) {
            Thread.sleep(1000);
        }
    }
}
