package com.push.server.tool;

import java.io.IOException;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class XMLParser {

	public static final String PUSH_SERVER_IP = "ip";
	public static final String PUSH_SERVER_PORT = "port";
	public static final String PUSH_SERVER_WAITINGUSERNUMBER = "waitingUserNumber";
	public static final String PUSH_SERVER_DELIMITER = "delimiter";
	public static final String PUSH_SERVER_IDDELIMITER = "idDelimiter";
	public static final String PUSH_SERVER_MESSAGEDELIMITER = "messageDelimiter";
	public static final String PUSH_SERVER_MESSAGE_LENGTH = "length";
	public static final String PUSH_SERVER_MESSAGE_TYPE = "type";
	public static final String PUSH_SERVER_HEARTBEATTIME = "heartBeatTime";
	public static final String PUSH_SERVER_DELIMITERMAXFRAMELENGTH = "delimiterMaxFrameLength";
	public static final String PUSH_SERVER_DEFAULTEVENTEXECUTORGROUP = "defaultEventExecutorGroup";
	public static final String PUSH_SERVER_CLEARCACHETIME = "clearCacheTime";
	public static final String PUSH_SERVER_CLEARCACHEINTERVAL = "clearCacheInterval";
	
	private ConfigMap configMap = ConfigMap.getInstance();
	
	public XMLParser(){
		parseContent();
	}
	
	protected String getFileName() {
		String fileName = "";
		
		if (System.getProperty("user.dir") != null) {
//			fileName = System.getProperty("user.dir")+"/middleware/src/Config.xml";
			fileName = System.getProperty("user.dir")+"/config/Config.xml";
		} else {
			fileName = XMLParser.class.getClassLoader().getResource("Config.xml").getPath();
		}
		return fileName;
	}
	
	public void parseContent(){
		String path = getFileName();
		SAXBuilder builder = new SAXBuilder(false);
		try {
			Document doc = builder.build(path);
			Element root = doc.getRootElement(); 
			List<?> rootElement= root.getChildren("server");
			if(rootElement != null && rootElement.size() > 0){
				for(int j=0; j<rootElement.size(); j++){
					Element serverElement = (Element) rootElement.get(j);
					List<?> serverAttrList = serverElement.getAttributes();
					if(serverAttrList != null && serverAttrList.size() > 0){
						for (int i = 0; i < serverAttrList.size(); i++) {
							Attribute attr = (Attribute) serverAttrList.get(i);
//							System.out.println(attr.getName()+":"+attr.getValue());
							configMap.put(attr.getName(), attr.getValue());
							
						}
					}
					
					Element format = serverElement.getChild("format");
					List<?> formatAttrList = format.getAttributes();
					if(formatAttrList != null && formatAttrList.size() > 0){
						for(int n=0; n<formatAttrList.size(); n++){
							Attribute attr = (Attribute) formatAttrList.get(n);
							configMap.put(attr.getName(), attr.getValue());
						}
						Element message = format.getChild("message");
						List<?> messageAttrList = message.getAttributes();
						if(messageAttrList != null && messageAttrList.size() > 0){
							for(int m=0; m<messageAttrList.size(); m++){
								Attribute attr = (Attribute) messageAttrList.get(m);
								configMap.put(attr.getName(), attr.getValue());
							}
						}
					}
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
