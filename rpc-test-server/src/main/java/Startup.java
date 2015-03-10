import com.internal.test.dummy.config.ApplicationConfig;
import com.ytx.rpc.internal.api.IInnerApiServer;
import com.ytx.rpc.internal.api.InnerApiServer;
import com.ytx.rpc.internal.api.handler.CommandWorkerHandler;
import org.springframework.boot.SpringApplication;

/**
 * Created by zhangfuming on 2015/2/3 15:18.
 */
public class Startup {

    public static void main(String[] args)throws Exception{
        SpringApplication app = new SpringApplication(ApplicationConfig.class);
        app.setShowBanner(false);
        app.run(args);
        initServer();
    }

    private static void initServer(){
        IInnerApiServer apiServer = InnerApiServer.getInstance();
        apiServer.start();
    }

}
