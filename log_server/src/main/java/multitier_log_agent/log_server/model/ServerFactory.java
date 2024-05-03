package multitier_log_agent.log_server.model;

import java.util.concurrent.BlockingQueue;

import multitier_log_agent.log_shared.model.TracePacket;

public abstract class ServerFactory {

    public abstract IServer newInstance(int serverPort, BlockingQueue<TracePacket> commQueue);
    
    public static class ServerFactoryUDP extends ServerFactory {

        @Override
        public IServer newInstance(int serverPort, BlockingQueue<TracePacket> commQueue) {
            return new ServerUDP(serverPort, commQueue);
        }
        
    }
    public static class ServerFactoryTCP extends ServerFactory {

        @Override
        public IServer newInstance(int serverPort, BlockingQueue<TracePacket> commQueue) {
            return new ServerTCP(serverPort, commQueue);
        }
        
    }
}
