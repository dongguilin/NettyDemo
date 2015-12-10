package demo.nio;

/**
 * Created by hadoop on 2015/12/10.
 * NIO时间服务器TimeClient
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {

            }
        }

        new Thread(new TimeClientHandler("127.0.0.1", port), "TimeClient-001").start();
    }
}
