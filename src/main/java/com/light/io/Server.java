package com.light.io;

import com.light.http.RequestHandler;
import com.light.http.responses.Response;
import com.light.mvc.ControllerScan;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created on 2018/3/31.
 */
@Slf4j
public class Server {

    private ServerContext serverContext;
    private Selector selector;

    /**
     * 启动服务器
     * @param args 格式:start [address:port]
     * @param controllerPacakgePaths 扫描包下的controller
     * @throws IOException
     */
    public void run(String[] args,String... controllerPacakgePaths) throws IOException {
        //初始化
        init(args,controllerPacakgePaths);
        //启动
        start();
    }

    private void init(String[] args,String... controllerPacakgePaths) throws UnknownHostException {
        long start = System.currentTimeMillis();

        //初始化server context，包括ip、port等信息
        initContext(args);
        //扫描controller
        initController(controllerPacakgePaths);
        //初始化serversocket，开始接受socket连接
        initServer();

        long end = System.currentTimeMillis();
        log.info("服务器启动 http:/{}:{}/ 耗时:{}ms", serverContext.getIp().getHostAddress(),
                serverContext.getPort(), end - start);
    }

    private void initContext(String[] args) throws UnknownHostException {
        //parse command line arguments
        if(args.length < 1 || !args[0].equals("start")){
            log.info("Usage: start [address:port]");
            System.exit(1);
        }

        InetAddress ip = null;
        int port = 0;

        if(args.length == 2 && args[1].matches(".+:\\d+")){
            String[] addressAndPort = args[1].split(":");
            ip = InetAddress.getByName(addressAndPort[0]);
            port = Integer.valueOf(addressAndPort[1]);
        }else{
            ip = InetAddress.getLocalHost();
            port = 8080;
        }

        ServerContext context = new ServerContext();
        context.setIp(ip);
        context.setPort(port);
        this.serverContext = context;
    }

    /**
     * 扫描所有@Controller
     */
    private void initController(String... controllerPacakgePaths){
        ControllerScan.scanPackage("com.light");

        for(String packageName : controllerPacakgePaths){
            log.info("包名：{}，开始扫描包中的@Controller", packageName);
            ControllerScan.scanPackage(packageName);
        }

        //因为这个项目是让别人添加maven依赖就能用的，所以读配置文件的方式不好
//        //从配置文件settings.properties读取需要扫描的controller包名
//        if(PropertiesUtil.getProperty("controller_package") != null){
//            String[] pkgNames = PropertiesUtil.getProperty("controller_package").split(";");
//            for(String pkgName : pkgNames){
//                log.info("包名：{}，开始在包下扫描@Controller注解",pkgName);
//                ControllerScan.scanPackage(pkgName);
//            }
//        }
    }

    private void initServer(){
        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(serverContext.getIp(), serverContext.getPort()));
            serverSocketChannel.configureBlocking(false);
            this.selector = Selector.open();
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void start() throws IOException {
        while(true){
            try{
                if(this.selector.select(500) == 0)
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<SelectionKey> readyKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    accept(key);
                }else if(key.isReadable()){
                    read(key);
                }else if(key.isWritable()){
                    write(key);
                }
                iterator.remove();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        System.out.println("accept");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) {
        System.out.println("read");
        SocketChannel client = (SocketChannel) key.channel();
        ThreadPool.execute(new RequestHandler(client, selector));
        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
    }

    private void write(SelectionKey key) {
        System.out.println("write");
        ThreadPool.execute(() -> {
            SocketChannel client = (SocketChannel) key.channel();
            Response response = (Response) key.attachment();
            ByteBuffer byteBuffer = response.getResponseBuffer();
            try {
                if(byteBuffer.hasRemaining()){
                    client.write(byteBuffer);
                }
                if(!byteBuffer.hasRemaining()){
                    key.cancel();
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
    }

}
