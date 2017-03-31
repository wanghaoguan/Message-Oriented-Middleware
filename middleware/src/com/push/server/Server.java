package com.push.server;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.TangTangQing.Service.push.OfflineMsgUserRemoveCache;
import com.push.server.decode.MessageDecoder;
import com.push.server.other.InitCache;
import com.push.server.tool.ConfigMap;
import com.push.server.tool.XMLParser;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class Server {

	/**
	 * @param args
	 */
	private static Logger log = Logger.getLogger(Server.class.getName());
	
	private XMLParser xmlParser = new XMLParser();
	
	public Server () {
		
		new Thread(new InitCache()).start();
		
		new Thread(new BindThread()).start(); 
		
		startTimer();
	}
	
	public void startTimer(){
		
		Timer timer = new Timer();
		timer.schedule(new OfflineMsgUserRemoveCache(), 60*1000, 60*1000);
	}
	
	public void bind ( String ip , int port ) {
		
		int backlog = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_WAITINGUSERNUMBER));
		final int defaultEventExecutorGroup = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_DEFAULTEVENTEXECUTORGROUP));
		final byte[] delimiterByte = ConfigMap.getValue(XMLParser.PUSH_SERVER_DELIMITER).getBytes();
		final int heartBeatTime = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_HEARTBEATTIME));
		final int maxFrameLength = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_DELIMITERMAXFRAMELENGTH));
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap b = new ServerBootstrap();
			
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, backlog)
			 .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT) //��̬�������Ĵ�С
			 .option(ChannelOption.SO_KEEPALIVE, true)
			 .option(ChannelOption.TCP_NODELAY, true)
			 //ʹ���ڴ��
			 .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .childHandler(new ChannelInitializer<SocketChannel>() {
				 @Override
				 public void initChannel(SocketChannel ch)
						 throws Exception {
					 ByteBuf delimiter = Unpooled.copiedBuffer(delimiterByte);
					
					 //�������̳߳أ�����Ϣ��ҵ����д���
					 EventExecutorGroup e = new DefaultEventExecutorGroup(defaultEventExecutorGroup);
					 ch.pipeline().addLast(new MessageDecoder(maxFrameLength, delimiter));
					 ch.pipeline().addLast(e,new ServerHandler());
					 ch.pipeline().addLast(new IdleStateHandler(0, 0, heartBeatTime));
					 ch.pipeline().addLast(new HeartBeat());
				}
			 });
		
			 
				
			// �󶨶˿ڣ�ͬ���ȴ�ɹ�
		    ChannelFuture f = b.bind(ip,port).sync();
			log.info("ip:"+ip+" port:"+port+" starts running");
		    // �ȴ����˼���˿ڹر�
			f.channel().closeFuture().sync();
				 
		} catch (InterruptedException e) {
			e.printStackTrace();
		
		} finally {
		    // �����˳����ͷ��̳߳���Դ
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			log.info("the push server has been shut down");
		}
	}
	
	class BindThread implements Runnable {

		String ip = ConfigMap.getValue(XMLParser.PUSH_SERVER_IP);
		int port = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_PORT));
		
		@Override
		public void run () {
			
			try {
				
				bind(ip,port);
				
			} catch (Exception e) {
//				e.printStackTrace();
				log.error("starting push server has failed", e);
			}
		}
		
	}
	
	public static void main(String[] args) {
		new Server();
	}

}
