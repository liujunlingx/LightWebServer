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

    //for ServerSocket to bind
    private InetAddress ip;
    private int port;

    private Selector selector;

    public Server(InetAddress ip,int port){
        this.ip = ip;
        this.port = port;
    }

    /**
     *
     * @param args
     * @param pkgNames 包名，可多个，扫描每个包下所有的@Controller类
     * @throws IOException
     */
    public static void run(String[] args,String... pkgNames) throws IOException {
        if(args.length < 1 || !args[0].equals("start")){
            log.info("Usage: start [address:port]");
            System.exit(1);
        }

        InetAddress ip = null;
        int port = 0;

        try{
            if(args.length == 2 && args[1].matches(".+:\\d+")){
                String[] addressAndPort = args[1].split(":");
                ip = InetAddress.getByName(addressAndPort[0]);
                port = Integer.valueOf(addressAndPort[1]);
            }else{
                ip = InetAddress.getLocalHost();
                port = 8080;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Server server = new Server(ip,port);
        server.start(pkgNames);
    }

    public void start(String... pkgNames) throws IOException {
        init(pkgNames);
        while(true){
            try{
                if(selector.select(500) == 0)
                    continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
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

    public void init(String... pkgNames){
        long start = System.currentTimeMillis();
        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(ip, port));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("服务器启动 http:/{}:{}/ 耗时:{}ms", ip.getHostAddress(), port, System.currentTimeMillis() - start);
        //扫描所有RequestMapping
        ControllerScan.scanPackage("com.light");
        for(String pkgName : pkgNames){
            ControllerScan.scanPackage(pkgName);
        }
    }

    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    public void read(SelectionKey key) throws IOException {
        //System.out.println("Start read method");
        SocketChannel client = (SocketChannel) key.channel();
        ThreadPool.execute(new RequestHandler(client, selector));
        //TODO keep-alive
        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);//目前一个请求对应一个socket连接，没有实现keep-alive
        //System.out.println("End read method");
    }

    public void write(SelectionKey key) throws IOException {
        //System.out.println("Start write method");
        SocketChannel client = (SocketChannel) key.channel();
        Response response = (Response) key.attachment();
        ByteBuffer byteBuffer = response.getResponseBuffer();
        if(byteBuffer.hasRemaining()){
            client.write(byteBuffer);
        }
        if(!byteBuffer.hasRemaining()){
            key.cancel();
            client.close();
        }
        //System.out.println("End write method");
    }

}
