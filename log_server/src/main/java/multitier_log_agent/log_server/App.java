package multitier_log_agent.log_server;

import multitier_log_agent.log_server.config.ConfigModel;
import multitier_log_agent.log_server.config.ConfigParser;

public class App {

    public static void main(String[] args) {

        // check config file name
        String configFile = "config.xml";
        if (args != null && args.length >= 1 && args[0] != null
                && !args[0].isEmpty()) {
            configFile = args[0];
        }
        
        ConfigModel config = ConfigParser.parse(configFile);
        
        ServerApi serverApi = new ServerApi(config);
        serverApi.start();
        
        System.out.println("Press any key to exit...");
        try {
            System.in.read();
        } catch (Exception e) {
        }

        serverApi.stop();
    }
}
