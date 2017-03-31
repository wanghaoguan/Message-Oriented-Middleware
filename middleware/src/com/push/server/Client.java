package com.push.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.ws.EndpointReference;

import com.push.server.tool.DataConvert;
import com.push.server.tool.MessageType;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

public class Client extends JFrame{
	
	public int port;
	public Channel channel = null;
	
	private JLabel portLabel = null;
	private JLabel nameLabel =  null;
	public JTextField portTextField = null;
	public JTextField nameTextField = null;
	public JButton connectButton = null;
	
	private JLabel messageLabel = null;
	public JTextArea messageTextArea = null;
//	public JButton sendButton = null;
	
	private JLabel receiveLabel = null;
	public static JTextArea receiveTextArea = null;
	
	public JLabel idLabel = null;
	public JTextField idTextField1 = null;
	public JTextField idTextField2 = null;
//	public JTextField idTextField3 = null;
//	public JTextField idTextField4 = null;
	
	private JScrollPane srcoll = null;
	
	private ScheduledExecutorService executor = Executors
		    .newScheduledThreadPool(1);
	
	public Client(){
		this.setLayout(null);
		
		portLabel = new JLabel("IP端口：");
		portLabel.setBounds(10, 10, 60, 30);
		portTextField = new JTextField("9050");
		portTextField.setBounds(70, 10, 40, 30);
		nameLabel = new JLabel(" type：");
		nameLabel.setBounds(120, 10, 40, 30);
		nameTextField = new JTextField();
		nameTextField.setBounds(160, 10, 50, 30);
		idLabel = new JLabel("     ID:");
		idLabel.setBounds(210, 10, 40, 30);
		idTextField1 = new JTextField();
		idTextField1.setBounds(250, 10, 50, 30);
		idTextField2 = new JTextField();
		idTextField2.setBounds(310, 10, 50, 30);
//		idTextField3 = new JTextField();
//		idTextField3.setBounds(370, 10, 50, 30);
//		idTextField4 = new JTextField();
//		idTextField4.setBounds(430, 10, 50, 30);
		
		connectButton = new JButton("连接服务器并发送");
		connectButton.setBounds(220, 55, 140, 30);
		connectButton.addActionListener(new Connection());
		
		
		messageLabel = new JLabel("输入发送的消息：");
		messageLabel.setBounds(20, 60, 120, 30);
		messageTextArea = new JTextArea();
		messageTextArea.setBounds(20, 90, 340, 100);
//		sendButton = new JButton("发送");
//		sendButton.setBounds(360, 150, 100, 30);
//		sendButton.addActionListener(new SendMessage());
		
		receiveLabel = new JLabel("收到的消息：");
		receiveLabel.setBounds(20, 210, 100, 30);
		receiveTextArea = new JTextArea();
		receiveTextArea.setBounds(20, 240, 340, 100);
		srcoll = new JScrollPane(receiveTextArea);
		srcoll.setBounds(20, 240, 340, 100);
		
		this.add(portLabel);
		this.add(portTextField);
		this.add(nameLabel);
		this.add(nameTextField);
		this.add(connectButton);
		this.add(messageLabel);
		this.add(messageTextArea);
//		this.add(sendButton);
		this.add(receiveLabel);
//		this.add(receiveTextArea);
		this.add(idLabel);
		this.add(idTextField1);
		this.add(idTextField2);
//		this.add(idTextField3);
//		this.add(idTextField4);
		this.add(srcoll);
		this.setVisible(true);
		this.setBounds(500, 100, 400, 400);
		this.setDefaultCloseOperation(3);
	}
	
	public void connect(int port, String host) throws Exception {
		// 配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch)
					throws Exception {
					ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
//					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new LoginAuthReqHandler());
					ch.pipeline().addLast(new ReceiveMessage());
				}
			});

			// 发起异步连接操作
			ChannelFuture f = b.connect(host, port).sync();

			// 当代客户端链路关闭
			f.channel().closeFuture().sync();
		}catch(Exception e){
			messageTextArea.append("正在努力寻找服务器中。。。。。。\n");
System.out.println("正在努力寻找服务器中。。。。。。");
		} finally {
			// 优雅退出，释放NIO线程组
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
			new Client();
	}
		    
		    
	class Connection implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread(new ConnectionThread()).start();
		}
				
	}
			
	class ConnectionThread implements Runnable{

		@Override
		public void run() {
			String portString = portTextField.getText().trim();
			port = Integer.parseInt(portString);
			try {
//				connect(port, "192.168.137.123");
				connect(port, "115.29.161.214");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
				
	}
	
	public byte[] buildMessage(){
		String id1 = idTextField1.getText().trim();
		String id2 = idTextField2.getText().trim();
//		String id3 = idTextField3.getText().trim();
//		String id4 = idTextField4.getText().trim();
		
		String ID = null;
		byte[] messageBody = null;
		String typeString = nameTextField.getText().trim();
		int type = Integer.parseInt(typeString);
		if(type == MessageType.ANDROIDLOGIN_REQ.value() || type == 6){
			ID = id1;
			messageBody = (ID+"$_").getBytes();
		}else{
//			ID = id1+","+id2+","+id3+","+id4;
//			ID = id1;
			String body = messageTextArea.getText().trim();
//			messageBody = (ID+";"+body+"$_").getBytes();
			messageBody = (body+"$_").getBytes();
		}
		
		int bodyLength = messageBody.length;
		
		byte[] messageHeader = DataConvert.mergeByteArray(DataConvert.intToByteArray(bodyLength),DataConvert.intToByteArray(type));
		
		byte[] message = DataConvert.mergeByteArray(messageHeader, messageBody);
		return message;
	}
			
			
			
	class LoginAuthReqHandler extends ChannelHandlerAdapter{
		
		ByteBuf sendMessage = null;
		private volatile ScheduledFuture<?> heartBeat;

		public LoginAuthReqHandler() {
			byte[] message = buildMessage();
			sendMessage = Unpooled.copiedBuffer(message);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			 ctx.writeAndFlush(sendMessage);
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			
			ByteBuf buf = (ByteBuf) msg;
			buf.markReaderIndex();
			byte[] req = new byte[buf.readableBytes()];
			buf.readBytes(req);
			String body = new String(req,"UTF-8");
System.out.println(body);
			int type = DataConvert.byteArraayToInt(DataConvert.cutOutByte(req, 4, 8));
			if(type == MessageType.ANDROIDLOGIN_RESP.value() ){
System.out.println("登录成功的响应信息！！");
				receiveTextArea.append("登录成功\n");
				heartBeat = ctx.executor().scheduleAtFixedRate(
					    new LoginAuthReqHandler.HeartBeatTask(ctx), 0, 5000,
					    TimeUnit.MILLISECONDS);
			}else{
				
				buf.resetReaderIndex();
				ctx.fireChannelRead(msg);
			}
			
		}

	    @Override
	    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	    	ctx.flush();
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	    	receiveTextArea.append("与服务器的连接中断\n");
	    	ctx.close();
	    }
	    
	    public byte[] buildResp(String str){
			int type = MessageType.HEARTBEAT_REQ.value();
			String body = str+"$_";
			int length = body.length();
			byte[] header = DataConvert.mergeByteArray(DataConvert.intToByteArray(length),DataConvert.intToByteArray(type));
			byte[] resp = DataConvert.mergeByteArray(header, body.getBytes());
			return resp;
		}
	    
	    private class HeartBeatTask implements Runnable {
			
			private final ChannelHandlerContext ctx;

			public HeartBeatTask(final ChannelHandlerContext ctx) {
			    this.ctx = ctx;
			}

			@Override
			public void run() {
				byte[] heartBeat = buildResp("心跳信息");
				ByteBuf buf = Unpooled.copiedBuffer(heartBeat);
			    ctx.writeAndFlush(buf);
			}

		}
	}

}
